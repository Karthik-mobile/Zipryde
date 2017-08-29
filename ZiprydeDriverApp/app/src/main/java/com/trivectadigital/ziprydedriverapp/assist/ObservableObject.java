package com.trivectadigital.ziprydedriverapp.assist;

import java.util.Observable;

/**
 * Created by naveendevaraj on 8/28/17.
 */

public class ObservableObject extends Observable {
    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance() {
        return instance;
    }

    private ObservableObject() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }

    public void onActivityChange( ) {
        synchronized (this) {
            setChanged();
            notifyObservers();
        }
    }
}
