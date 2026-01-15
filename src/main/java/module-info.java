module com.example.battleshipoop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.battleshipoop to javafx.fxml;
    opens com.example.battleshipoop.app to javafx.fxml;
    opens com.example.battleshipoop.app.controllers to javafx.fxml;
    opens com.example.battleshipoop.models to javafx.fxml;
    opens com.example.battleshipoop.network to javafx.fxml;

    exports com.example.battleshipoop;
    exports com.example.battleshipoop.app;
    exports com.example.battleshipoop.app.controllers;
    exports com.example.battleshipoop.models;
    exports com.example.battleshipoop.network;
}