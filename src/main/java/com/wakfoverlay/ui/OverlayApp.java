package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.FetchPlayerUseCase;
import com.wakfoverlay.domain.player.UpdatePlayerDamagesUseCase;
import com.wakfoverlay.domain.player.port.secondary.PlayersRepository;
import com.wakfoverlay.exposition.TheAnalyzer;
import com.wakfoverlay.infrastructure.InMemoryPlayersRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup dependencies manually
        PlayersRepository playersRepository = new InMemoryPlayersRepository();
        FetchPlayerUseCase fetchPlayer = new FetchPlayerUseCase(playersRepository);
        UpdatePlayerDamagesUseCase updatePlayerDamages = new UpdatePlayerDamagesUseCase(playersRepository);
        TheAnalyzer theAnalyzer = new TheAnalyzer(fetchPlayer, updatePlayerDamages);

        MainWindow mainWindow = new MainWindow(fetchPlayer, updatePlayerDamages, theAnalyzer);

        Scene scene = new Scene(mainWindow, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.setFill(Color.rgb(18, 18, 18));

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle(DEFAULT_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(DEFAULT_ALWAYS_ON_TOP);
        primaryStage.setOpacity(DEFAULT_OPACITY);
        primaryStage.setResizable(true);

        WindowResizer windowResizer = new WindowResizer(mainWindow, primaryStage);

        primaryStage.show();
        updateData(mainWindow);
    }

    private void updateData(MainWindow mainWindow) {
        Timeline displayTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> mainWindow.updateDisplay()));
        displayTimeline.setCycleCount(Timeline.INDEFINITE);
        displayTimeline.play();
    }
}
