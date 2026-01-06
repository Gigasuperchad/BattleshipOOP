module com.example.battleshipoop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.battleshipoop to javafx.fxml;
    exports com.example.battleshipoop;
    exports com.example.battleshipoop.app;
    opens com.example.battleshipoop.app to javafx.fxml;
    exports com.example.battleshipoop.app.controllers;
    opens com.example.battleshipoop.app.controllers to javafx.fxml;
}