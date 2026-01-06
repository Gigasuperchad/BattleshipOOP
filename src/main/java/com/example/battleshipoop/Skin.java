package com.example.battleshipoop;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Skin {
    private final StringProperty name = new SimpleStringProperty();
    private final String resourcePath;

    public Skin(String name, String resourcePath) {
        this.name.set(name);
        this.resourcePath = resourcePath;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getResourcePath() {
        return resourcePath;
    }
}