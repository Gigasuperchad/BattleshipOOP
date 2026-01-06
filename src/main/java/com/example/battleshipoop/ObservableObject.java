package com.example.battleshipoop;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableObject {
    protected void onPropertyChanged(String propertyName) {
        // Может быть переопределен в наследниках
        System.out.println("Property changed: " + propertyName);
    }

    protected <T> Property<T> createProperty(T initialValue, String propertyName) {
        return new SimpleObjectProperty<T>(initialValue) {
            @Override
            protected void fireValueChangedEvent() {
                super.fireValueChangedEvent();
                onPropertyChanged(propertyName);
            }
        };
    }

    protected StringProperty createStringProperty(String initialValue, String propertyName) {
        return new SimpleStringProperty(initialValue) {
            @Override
            protected void fireValueChangedEvent() {
                super.fireValueChangedEvent();
                onPropertyChanged(propertyName);
            }
        };
    }
}