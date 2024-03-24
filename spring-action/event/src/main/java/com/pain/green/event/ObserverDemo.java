package com.pain.green.event;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Observable;
import java.util.Observer;

public class ObserverDemo {
    public static void main(String[] args) {
        EventObservable observable = new EventObservable();
        observable.addObserver(new EventObserver());
        observable.notifyObservers("Hello observer");
    }

    static class EventObservable extends Observable {
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(new EventObject(arg));
            super.clearChanged();
        }
    }

    static class EventObserver implements Observer, EventListener {

        @Override
        public void update(Observable o, Object arg) {
            EventObject eventObject = (EventObject) arg;
            System.out.println(eventObject);
        }
    }
}
