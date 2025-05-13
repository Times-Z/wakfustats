package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.FetchPlayerUseCase;
import com.wakfoverlay.domain.player.UpdatePlayerDamagesUseCase;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.domain.player.port.secondary.PlayersData;
import com.wakfoverlay.infrastructure.InMemoryPlayersData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class OverlayApp extends Application {

    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 300;
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
        DamagesWindow damagesWindow = new DamagesWindow(fetchPlayer);
        // ********************************************

        MainWindow mainWindow = new MainWindow(damagesWindow);

        Scene scene = new Scene(mainWindow, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(DEFAULT_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(DEFAULT_ALWAYS_ON_TOP);
        primaryStage.setOpacity(DEFAULT_OPACITY);

        setupWindowDragging(mainWindow, primaryStage);

        primaryStage.show();

        simulateDamageUpdates(damagesWindow, updatePlayerDamages);
    }

    private void setupWindowDragging(StackPane root, Stage stage) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
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
