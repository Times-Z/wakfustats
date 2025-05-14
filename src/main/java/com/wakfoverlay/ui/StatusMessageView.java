package com.wakfoverlay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StatusMessageView extends HBox {

    public StatusMessageView(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        this.getChildren().add(messageLabel);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20, 0, 0, 0));
    }
}
