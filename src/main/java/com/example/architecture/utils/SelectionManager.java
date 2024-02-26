package com.example.architecture.utils;

import com.example.architecture.messages.EventMessages;
import com.example.architecture.messages.EventResponse;
import com.example.architecture.statistics.Event;

import java.util.Queue;

public class SelectionManager {

    public void selectAndAddToDevice(Buffer buffer, Device device, Source source, double currentTime, TimeAction timeAction,
                                     EventResponse eventResponse, int count, int countRejection, Queue<TimeAction> timeLine) {
        Integer bufNum = buffer.getNextIndex();
        if (bufNum == null) {
            return;
        }
        Double requestForDevice = buffer.chooseRequest(bufNum);
        if (requestForDevice != null) {
            //добавляем на прибор заявку из буфера
            int numDevice = device.addToDevice(requestForDevice);
            int numSource = source.getNumSource(requestForDevice);
            double time = device.calculateDeltaTimeEvenly();
            source.sumWorkTime(numSource, time);
            source.sumWaitTime(numSource, currentTime - source.startTime[numSource]);
            device.sumWorkTime(timeAction.toolNum, numSource, time);
            eventResponse.add(new Event(null, "П" + numDevice, "Б" + bufNum, requestForDevice, currentTime,
                    EventMessages.bufReq + EventMessages.whereDev, count, countRejection));
            timeLine.add(new TimeAction(currentTime + time, 1, timeAction.toolNum));
        }
    }
}
