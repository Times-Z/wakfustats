package com.wakfoverlay.ui;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WindowResizer {
    private final Node root;
    private final Stage stage;
    private static final int RESIZE_BORDER = 5;

    private double xOffset = 0;
    private double yOffset = 0;
    private final double[] dragOffsetX = new double[1];
    private final double[] dragOffsetY = new double[1];
    private final double[] initWidth = new double[1];
    private final double[] initHeight = new double[1];

    public WindowResizer(Node root, Stage stage) {
        this.root = root;
        this.stage = stage;
        setupResizeAndDragHandlers();
    }

    public void setupResizeAndDragHandlers() {
        root.setOnMouseMoved(this::handleMouseMove);
        root.setOnMousePressed(this::handleMousePressed);
        root.setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMouseMove(MouseEvent event) {
        if (event.getX() <= RESIZE_BORDER) {
            if (event.getY() <= RESIZE_BORDER) {
                root.setCursor(Cursor.NW_RESIZE);
            } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
                root.setCursor(Cursor.SW_RESIZE);
            } else {
                root.setCursor(Cursor.W_RESIZE);
            }
        } else if (event.getX() >= stage.getWidth() - RESIZE_BORDER) {
            if (event.getY() <= RESIZE_BORDER) {
                root.setCursor(Cursor.NE_RESIZE);
            } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
                root.setCursor(Cursor.SE_RESIZE);
            } else {
                root.setCursor(Cursor.E_RESIZE);
            }
        } else if (event.getY() <= RESIZE_BORDER) {
            root.setCursor(Cursor.N_RESIZE);
        } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
            root.setCursor(Cursor.S_RESIZE);
        } else {
            root.setCursor(Cursor.DEFAULT);
        }
    }

    private void handleMousePressed(MouseEvent event) {
        if (root.getCursor() != Cursor.DEFAULT) {
            dragOffsetX[0] = event.getScreenX();
            dragOffsetY[0] = event.getScreenY();
            initWidth[0] = stage.getWidth();
            initHeight[0] = stage.getHeight();
            event.consume();
        } else {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (root.getCursor() != Cursor.DEFAULT) {
            double deltaX = event.getScreenX() - dragOffsetX[0];
            double deltaY = event.getScreenY() - dragOffsetY[0];

            if (root.getCursor() == Cursor.E_RESIZE ||
                    root.getCursor() == Cursor.NE_RESIZE ||
                    root.getCursor() == Cursor.SE_RESIZE) {
                double newWidth = initWidth[0] + deltaX;
                if (newWidth >= stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                }
            }

            if (root.getCursor() == Cursor.S_RESIZE ||
                    root.getCursor() == Cursor.SE_RESIZE ||
                    root.getCursor() == Cursor.SW_RESIZE) {
                double newHeight = initHeight[0] + deltaY;
                if (newHeight >= stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                }
            }

            if (root.getCursor() == Cursor.W_RESIZE ||
                    root.getCursor() == Cursor.NW_RESIZE ||
                    root.getCursor() == Cursor.SW_RESIZE) {
                double newWidth = initWidth[0] - deltaX;
                if (newWidth >= stage.getMinWidth()) {
                    stage.setX(event.getScreenX());
                    stage.setWidth(newWidth);
                }
            }

            if (root.getCursor() == Cursor.N_RESIZE ||
                    root.getCursor() == Cursor.NW_RESIZE ||
                    root.getCursor() == Cursor.NE_RESIZE) {
                double newHeight = initHeight[0] - deltaY;
                if (newHeight >= stage.getMinHeight()) {
                    stage.setY(event.getScreenY());
                    stage.setHeight(newHeight);
                }
            }

            event.consume();
        } else {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }
}
