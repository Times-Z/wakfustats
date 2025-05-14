package com.wakfoverlay.ui;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WindowResizer {
    private final Node parent;
    private final Pane resizeHandle;

    public WindowResizer(Node parent) {
        this.parent = parent;
        this.resizeHandle = createResizeHandle();
        setupResizeHandlers();
    }

    public Pane getResizeHandle() {
        return resizeHandle;
    }

    private Pane createResizeHandle() {
        Pane handle = new Pane();
        handle.setPrefHeight(5);
        handle.setMaxHeight(5);
        handle.setMinHeight(5);
        handle.setStyle("-fx-background-color: transparent;");
        handle.setCursor(Cursor.S_RESIZE);
        return handle;
    }

    private void setupResizeHandlers() {
        final double[] startY = new double[1];
        final double[] startHeight = new double[1];

        resizeHandle.setOnMousePressed(event -> {
            startY[0] = event.getScreenY();
            if (parent.getScene() != null && parent.getScene().getWindow() != null) {
                startHeight[0] = parent.getScene().getWindow().getHeight();
            }
            event.consume();
        });

        resizeHandle.setOnMouseDragged(event -> {
            if (parent.getScene() != null && parent.getScene().getWindow() != null) {
                Stage stage = (Stage) parent.getScene().getWindow();
                double deltaY = event.getScreenY() - startY[0];
                double newHeight = startHeight[0] + deltaY;
                if (newHeight > 100) {
                    stage.setHeight(newHeight);
                }
            }
            event.consume();
        });
    }
}
