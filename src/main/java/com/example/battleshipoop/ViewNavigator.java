package com.example.battleshipoop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.util.Stack;

public class ViewNavigator {
    private final StackPane container;
    private final Stack<ViewObject> viewStack = new Stack<>();
    private final ObservableList<ViewObject> views = FXCollections.observableArrayList();

    public ViewNavigator(StackPane container) {
        this.container = container;
    }

    public ObservableList<ViewObject> getViews() {
        return views;
    }

    public void addView(ViewObject viewObject) {
        views.add(viewObject);
        // Исправлено: используем лямбда-выражение вместо ссылки на метод
        viewObject.setOnNavigate(() -> navigateToView(viewObject));
    }

    public void navigate(ViewObject viewObject) {
        navigateToView(viewObject);
    }

    public void navigateBack() {
        if (viewStack.size() > 1) {
            viewStack.pop(); // Удаляем текущий вид
            ViewObject previousView = viewStack.pop();
            navigateToView(previousView);
        }
    }

    private void navigateToView(ViewObject viewObject) {
        Node view = viewObject.getView();

        if (view != null && (container.getChildren().isEmpty() ||
                container.getChildren().get(0) != view)) {

            container.getChildren().clear();
            container.getChildren().add(view);

            if (!viewStack.isEmpty() && viewStack.peek() == viewObject) {
                return;
            }

            viewStack.push(viewObject);
        }
    }

    public ViewObject getCurrentView() {
        return viewStack.isEmpty() ? null : viewStack.peek();
    }
}