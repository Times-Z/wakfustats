package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.FetchPlayerUseCase;
import com.wakfoverlay.domain.player.UpdatePlayerDamagesUseCase;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.domain.player.port.secondary.PlayersData;
import com.wakfoverlay.infrastructure.InMemoryPlayersData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class OverlayApp extends Application {

    private static final Integer DEFAULT_WIDTH = 350;
    private static final Integer DEFAULT_HEIGHT = 150;
    private static final String DEFAULT_TITLE = "Wakfu DPS Meter";
    private static final Boolean DEFAULT_ALWAYS_ON_TOP = true;
    private static final Double DEFAULT_OPACITY = 1.0;

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup dependencies manually
        PlayersData playersData = new InMemoryPlayersData();
        FetchPlayerUseCase fetchPlayer = new FetchPlayerUseCase(playersData);
        UpdatePlayerDamagesUseCase updatePlayerDamages = new UpdatePlayerDamagesUseCase(playersData);

        DamagesWindow damagesWindow = new DamagesWindow(fetchPlayer, updatePlayerDamages);

        Scene scene = new Scene(damagesWindow, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.setFill(Color.rgb(18, 18, 18));

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle(DEFAULT_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(DEFAULT_ALWAYS_ON_TOP);
        primaryStage.setOpacity(DEFAULT_OPACITY);

        primaryStage.setResizable(true);

        setupWindowResizingAndDragging(damagesWindow, primaryStage);
        primaryStage.show();

        simulateDamageUpdates(damagesWindow, updatePlayerDamages);
    }

    private void setupWindowResizingAndDragging(DamagesWindow root, Stage stage) {
        final int RESIZE_BORDER = 5;
        final double[] dragOffsetX = new double[1];
        final double[] dragOffsetY = new double[1];
        final double[] initWidth = new double[1];
        final double[] initHeight = new double[1];

        root.setOnMouseMoved(event -> {
            if (event.getX() <= RESIZE_BORDER) {
                if (event.getY() <= RESIZE_BORDER) {
                    root.setCursor(javafx.scene.Cursor.NW_RESIZE);
                } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
                    root.setCursor(javafx.scene.Cursor.SW_RESIZE);
                } else {
                    root.setCursor(javafx.scene.Cursor.W_RESIZE);
                }
            } else if (event.getX() >= stage.getWidth() - RESIZE_BORDER) {
                if (event.getY() <= RESIZE_BORDER) {
                    root.setCursor(javafx.scene.Cursor.NE_RESIZE);
                } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
                    root.setCursor(javafx.scene.Cursor.SE_RESIZE);
                } else {
                    root.setCursor(javafx.scene.Cursor.E_RESIZE);
                }
            } else if (event.getY() <= RESIZE_BORDER) {
                root.setCursor(javafx.scene.Cursor.N_RESIZE);
            } else if (event.getY() >= stage.getHeight() - RESIZE_BORDER) {
                root.setCursor(javafx.scene.Cursor.S_RESIZE);
            } else {
                root.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        root.setOnMousePressed(event -> {
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
        });

        root.setOnMouseDragged(event -> {
            if (root.getCursor() != Cursor.DEFAULT) {
                double deltaX = event.getScreenX() - dragOffsetX[0];
                double deltaY = event.getScreenY() - dragOffsetY[0];

                if (root.getCursor() == Cursor.E_RESIZE || root.getCursor() == Cursor.NE_RESIZE || root.getCursor() == Cursor.SE_RESIZE) {
                    double newWidth = initWidth[0] + deltaX;
                    if (newWidth >= stage.getMinWidth())
                        stage.setWidth(newWidth);
                }

                if (root.getCursor() == Cursor.S_RESIZE || root.getCursor() == Cursor.SE_RESIZE || root.getCursor() == Cursor.SW_RESIZE) {
                    double newHeight = initHeight[0] + deltaY;
                    if (newHeight >= stage.getMinHeight())
                        stage.setHeight(newHeight);
                }

                if (root.getCursor() == Cursor.W_RESIZE || root.getCursor() == Cursor.NW_RESIZE || root.getCursor() == Cursor.SW_RESIZE) {
                    double newWidth = initWidth[0] - deltaX;
                    if (newWidth >= stage.getMinWidth()) {
                        stage.setX(event.getScreenX());
                        stage.setWidth(newWidth);
                    }
                }

                if (root.getCursor() == Cursor.N_RESIZE || root.getCursor() == Cursor.NW_RESIZE || root.getCursor() == Cursor.NE_RESIZE) {
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
        });
    }


    private void simulateDamageUpdates(DamagesWindow damagesWindow, UpdatePlayerDamages updatePlayerDamages) {
        // Simulate display updates every 100ms
        Timeline displayTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> damagesWindow.updateDisplay()));
        displayTimeline.setCycleCount(Timeline.INDEFINITE);
        displayTimeline.play();

        // Simulate player damage updates every 100ms
        Timeline updatePlayersTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> updatePlayerDamages.updatePlayers()));
        updatePlayersTimeline.setCycleCount(Timeline.INDEFINITE);
        updatePlayersTimeline.play();
    }
}
