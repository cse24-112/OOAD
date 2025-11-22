package com.bankingsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("OOAD JavaFX");
        stage.setScene(new Scene(new Label("Hello, JavaFX!"), 320, 180));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}