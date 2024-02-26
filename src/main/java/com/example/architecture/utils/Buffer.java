package com.example.architecture.utils;

import lombok.AllArgsConstructor;
import java.util.*;

public class Buffer {

    @AllArgsConstructor
    static class Pair {
        double value; //номер заявки
        int index; //номер ячейки буфера
    }

    public double[] array;
    Queue<Pair> queue;
    Set<Double> set;

    int choosingIterator = -1;
    int insertingIterator = -1;

    public Buffer(int bufferNum) {
        array = new double[bufferNum];
        queue = new LinkedList<>();
        set = new HashSet<>();
    }

    public boolean hasEmpty() {
        return set.size() < array.length;
    }

    //Добавление в буфер по кольцу
    public int addToBuff(double request) {
        insertingIterator++;
        for (int i = 0; i < array.length; i++) {
            if (insertingIterator >= array.length) {
                insertingIterator = 0;
            }

            if (array[insertingIterator] == 0) {
                array[insertingIterator] = request;
                set.add(request);
                queue.add(new Pair(request, insertingIterator));
                return insertingIterator;
            }
            insertingIterator++;
        }
        return -1;
    }

    //отказ (самый старый в буфере)
    public int flush() {
        while (!queue.isEmpty() && !set.contains(queue.peek().value)) {
            queue.poll(); //удаляем уже невалидные данные
        }
        Pair pair = queue.poll();
        assert pair != null;
        array[pair.index] = 0;
        set.remove(pair.value);
        return pair.index;
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    //Выбор заявки из буфера по кольцу
    public Integer getNextIndex() {
        choosingIterator++;
        for (int i = 0; i < array.length; i++) {
            if (choosingIterator >= array.length) {
                choosingIterator = 0;
            }
            if (array[choosingIterator] != 0) {
                return choosingIterator;
            }
            choosingIterator++;
        }
        return null;
    }

    public Double chooseRequest(int index) {
        Double request = array[index];
        set.remove(request);
        array[index] = 0;
        return request;
    }


    public Double getOldestRequest() {
        while (!queue.isEmpty() && !set.contains(queue.peek().value)) {
            queue.poll(); //удаляем уже невалидные данные
        }
        assert queue.peek() != null;
        return queue.peek().value;
    }

    public int size() {
        return array.length;
    }
}
