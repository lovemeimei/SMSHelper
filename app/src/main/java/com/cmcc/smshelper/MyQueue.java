package com.cmcc.smshelper;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by xuan on 2018/2/23.
 */

public class MyQueue<T> {

    LinkedList<T> storage = new LinkedList<>();

    public synchronized void push(T t){

        storage.add(t);
    }

    public T peek(){
        return storage.getFirst();
    }

    public void pop(){
        storage.removeFirst();
    }

    public boolean empty(){
        return storage.isEmpty();
    }
}
