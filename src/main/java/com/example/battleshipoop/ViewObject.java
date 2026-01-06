package com.example.battleshipoop;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import java.lang.reflect.Constructor;
import java.util.function.Predicate;

public class ViewObject {
    private final StringProperty caption = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final BooleanProperty visible = new SimpleBooleanProperty(true);

    private final String viewTypeName;
    private final boolean singleton;
    private Node instance;
    private Runnable onNavigate;
    private Predicate<Object> canExecute;

    public ViewObject(String viewTypeName, String caption,
                      boolean singleton, Predicate<Object> canExecute,
                      Predicate<Object> isVisible) {
        this.viewTypeName = viewTypeName;
        this.caption.set(caption);
        this.singleton = singleton;
        this.canExecute = canExecute != null ? canExecute : o -> true;

        if (isVisible != null) {
            visible.set(isVisible.test(this));
        }
    }

    public String getCaption() {
        return caption.get();
    }

    public StringProperty captionProperty() {
        return caption;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public void setOnNavigate(Runnable onNavigate) {
        this.onNavigate = onNavigate;
    }

    public void navigate() {
        if (onNavigate != null && (canExecute == null || canExecute.test(this))) {
            onNavigate.run();
        }
    }

    public Node getView() {
        if (singleton) {
            if (instance == null) {
                instance = createViewInstance();
            }
            return instance;
        }

        return createViewInstance();
    }

    private Node createViewInstance() {
        try {
            Class<?> clazz = Class.forName("com.example.battleshipoop.app.controllers." + viewTypeName);
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return (Node) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}