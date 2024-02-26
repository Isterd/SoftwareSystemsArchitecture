package com.example.architecture.utils;

import com.example.architecture.messages.EventMessages;
import com.example.architecture.messages.EventResponse;
import com.example.architecture.statistics.Event;
import lombok.Getter;

import java.util.Queue;

@Getter
public class ProductionManager {
    int countRejection = 0;

    public void addToDev(Device device, Source source, double req, TimeAction timeAction, EventResponse eventResponse,
                         double currentTime, int count, int countRejection, Queue<TimeAction> timeline) {
        int num = device.addToDevice(req);
        double time = device.calculateDeltaTimeEvenly();

        source.sumWaitTime(timeAction.toolNum, 0);
        source.sumWorkTime(timeAction.toolNum, time);

        device.sumWorkTime(num, timeAction.toolNum, time);
        eventResponse.add(new Event("И" + timeAction.toolNum, "П" + num, null, req, currentTime,
                EventMessages.newReq + EventMessages.whereDev, count, countRejection));
        timeline.add(new TimeAction(currentTime + time, 1, num));
    }

    public void addToBuff(Buffer buffer, double req, EventResponse eventResponse, TimeAction timeAction, double currentTime, int count) {
        if (!buffer.hasEmpty()) {
            countRejection++;
            //очищаем место в буфере (выбираем самую старую заявку) //суммировать буферное время
            double oldestReq = buffer.getOldestRequest();
            int num = buffer.flush();
            eventResponse.add(new Event(null, null, "Б" + num, oldestReq, currentTime, EventMessages.reject,
                    count, countRejection));
        }
        //добавляем заявку в буфер
        int num = buffer.addToBuff(req);
        eventResponse.add(new Event("И" + timeAction.toolNum, null, "Б" + num, req, currentTime,
                EventMessages.newReq + EventMessages.whereBuf, count, countRejection));
    }
}
