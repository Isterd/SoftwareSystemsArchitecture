package com.example.architecture.services;

import com.example.architecture.messages.EventMessages;
import com.example.architecture.messages.EventResponse;
import com.example.architecture.messages.ExperimentParamsRequest;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.statistics.Event;
import com.example.architecture.utils.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Simulation {
    Queue<TimeAction> timeLine;
    double currentTime;
    int count;
    Double p0;
    Integer N0;
    Buffer buffer;
    Device device;
    Source source;
    ProductionManager dispatcherInput;
    SelectionManager dispatcherOutput;

    EventResponse eventResponse;

    public void startWork(ExperimentParamsRequest request) {
        buffer = new Buffer(request.getBufferNum());
        device = new Device(request.getDeviceNum(), request.getSourceNum(),
                            request.getAlpha(), request.getBeta());
        source = new Source(request.getSourceNum(), request.getLambda());
        eventResponse = new EventResponse();
        count = 0;
        currentTime = 0;
        dispatcherInput = new ProductionManager();
        dispatcherOutput = new SelectionManager();
        timeLine = new PriorityQueue<>();
        N0 = request.getEventsNum();

        //запуск источников
        for (; count < request.getSourceNum(); count++) {
            timeLine.add(new TimeAction(currentTime, 0, count));
            currentTime += 2;
        }
        while (!timeLine.isEmpty()) {
            TimeAction timeAction = timeLine.poll();
            assert timeAction != null;
            currentTime = timeAction.time;
            switch (timeAction.action) {
                case 0 ->  //создать новую заявку и отправить куда-нибудь (возможно выбить из буфера)
                        addNewRequest(timeAction);
                case 1 -> // освободить один прибор(если есть занятый) и отправить на него заявку из буфера (если есть)
                        completeRequest(timeAction);
            }
        }
        System.out.println("count: " + count);
        System.out.println("reject count: " + dispatcherInput.getCountRejection());
        p0 = calculateP0();
    }



    private void addNewRequest(TimeAction timeAction) {
        //создаем новую заявку
        double req = source.createRequest(timeAction.toolNum, currentTime);
        count++;
        double time;
        if (count <= N0) {
            time = source.deltaTimePoisson();
            timeLine.add(new TimeAction(currentTime + time, 0, timeAction.toolNum));
        }

        if (device.hasEmpty()) {
            dispatcherInput.addToDev(device, source, req, timeAction, eventResponse,
                    currentTime, count, dispatcherInput.getCountRejection(), timeLine);
        } else {
            dispatcherInput.addToBuff(buffer, req, eventResponse, timeAction, currentTime, count);
        }
    }

    private void completeRequest(TimeAction timeAction) { //номер прибора
        if (!device.isEmpty()) {
            //освобождаем прибор
            double oldReq = device.flush(timeAction.toolNum);
            eventResponse.add(new Event(null, "П" + timeAction.toolNum, null, oldReq, currentTime,
                                    EventMessages.flushDev, count, dispatcherInput.getCountRejection()));
        }

        if (!buffer.isEmpty()) {
            dispatcherOutput.selectAndAddToDevice(buffer, device, source, currentTime, timeAction,
                    eventResponse, count, dispatcherInput.getCountRejection(), timeLine);
        }

    }

    double ta = 1.643;
    double si = 0.1;

    private double calculateP0() {
        return (double) dispatcherInput.getCountRejection() / N0;
    }

    public int calculateN0() {
        return (int) ((Math.pow(ta, 2) * (1 - p0)) / (p0 * Math.pow(si, 2)));
    }


    public ExperimentResponse countResults() {
        if (source == null) {
            return null;
        }
        double[][] resultSource = new double[source.size()][7];
        for (int i = 0; i < resultSource.length; i++) {
            //1. кол-во заявок от каждого источника
            resultSource[i][0] = source.countRequests[i];

            //2. вероятность отказа Р_отк
            resultSource[i][1] = (source.countRequests[i] - source.timeRequestsBySource.get(i).size()) / resultSource[i][0];
            if (resultSource[i][1] == 1) {
                resultSource[i][3] = 0;
                resultSource[i][4] = 0;
                resultSource[i][2] = 0;
                resultSource[i][5] = 0;
                resultSource[i][6] = 0;
                continue;
            }

            //4. ср время ожидания Т_БП
            resultSource[i][3] = source.waitTime[i] / source.timeRequestsBySource.get(i).size(); //время ожидания может быть 0

            //5. ср время обслуживания заявки Т_обсл
            resultSource[i][4] = source.workTime[i] / source.timeRequestsBySource.get(i).size();

            //3. ср время пребывания в системе Т_преб = Т_БП + Т_обсл
            resultSource[i][2] = resultSource[i][3] + resultSource[i][4];

            //считаем дисперсию
            double s_BP = resultSource[i][3];
            double s_obsl = resultSource[i][4];

            double d_BP = 0;
            double d_obsl = 0;

            //6. дисперсия Д_БП
            for (double j : source.timeRequestsBySource.get(i)) {
                d_BP += Math.pow((j - s_BP), 2);
            }
            resultSource[i][5] = d_BP / resultSource[i][0];

            //7. дисперсия Д_обсл
            for (double j : device.timeRequestsByDevice.get(i)) {
                d_obsl += Math.pow((j - s_obsl), 2);
            }
            resultSource[i][6] = d_obsl / device.timeRequestsByDevice.get(i).size();
        }

        // коэф использования устройств К_исп
        // (время работы каждого прибора / время реализации)
        double[] resultDevice = new double[device.size()];
        for (int i = 0; i < resultDevice.length; i++) {
            resultDevice[i] = device.workTime[i] / currentTime;
        }
        return new ExperimentResponse(resultSource, resultDevice, N0, p0, buffer.size(), device.getAlpha(), device.getBeta(), source.getLm());
    }
}
