package com.wakfoverlay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static javafx.scene.layout.Priority.ALWAYS;

public class TitleBar extends HBox {

    public TitleBar(Runnable onOpenFile, Runnable onReset, Runnable onClose) {
        this.setPadding(new Insets(5));
        this.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("DPT Meter");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, ALWAYS);

        Button folderButton = createIconButton("📁", "#2ecc71");
        folderButton.setOnAction(event -> onOpenFile.run());

        Button resetButton = createIconButton("🔄", "#3498db");
        resetButton.setOnAction(event -> onReset.run());

        Button closeButton = createIconButton("✖", "#e74c3c");
        closeButton.setOnAction(event -> onClose.run());

        this.getChildren().addAll(titleLabel, spacer, folderButton, resetButton, closeButton);
    }

    private Button createIconButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: " + color + "; " +
                        "-fx-font-size: 15px; " +
                        "-fx-padding: 2 5; " +
                        "-fx-min-width: 20px; " +
                        "-fx-min-height: 20px; " +
                        "-fx-cursor: hand;"
        );
        return button;
    }
}
