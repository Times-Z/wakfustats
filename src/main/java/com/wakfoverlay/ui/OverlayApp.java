package com.wakfoverlay.ui;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.FetchStatusEffectUseCase;
import com.wakfoverlay.domain.fight.UpdateCharacterUseCase;
import com.wakfoverlay.domain.fight.UpdateStatusEffectUseCase;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.*;
import com.wakfoverlay.exposition.LogParser;
import com.wakfoverlay.infrastructure.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;

public class OverlayApp extends Application {

    private static final Integer DEFAULT_WIDTH = 400;
    private static final Integer DEFAULT_HEIGHT = 200;
    private static final String DEFAULT_TITLE = "Wakfu DPS Meter";
    private static final Boolean DEFAULT_ALWAYS_ON_TOP = true;
    private static final Double DEFAULT_OPACITY = 1.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup dependencies manually
        CharactersRepository charactersRepository = new InMemoryCharactersRepository();
        TargetedDamagesRepository targetedDamagesRepository = new InMemoryTargetedDamagesRepository();
        DamagesRepository damagesRepository = new InMemoryDamagesRepository();
        HealsRepository healsRepository = new InMemoryHealsRepository();
        ShieldsRepository shieldsRepository = new InMemoryShieldsRepository();
        FetchCharacterUseCase fetchCharacter = new FetchCharacterUseCase(charactersRepository, targetedDamagesRepository);
        UpdateCharacterUseCase updateCharacter = new UpdateCharacterUseCase(charactersRepository, damagesRepository, healsRepository, shieldsRepository);

        StatusEffectRepository statusEffectRepository = new InMemoryStatusEffectRepository();
        FetchStatusEffectUseCase fetchStatusEffect = new FetchStatusEffectUseCase(statusEffectRepository);
        UpdateStatusEffect updateStatusEffect = new UpdateStatusEffectUseCase(statusEffectRepository);

        LogParser logParser = new LogParser(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect, targetedDamagesRepository);

        MainWindow mainWindow = new MainWindow(fetchCharacter, updateCharacter, updateStatusEffect, damagesRepository, targetedDamagesRepository, healsRepository, shieldsRepository, logParser);

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
        Timeline fileReadTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            mainWindow.readLogData();
        }));

        Timeline displayTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            Platform.runLater(mainWindow::updateDisplay);
        }));

        fileReadTimeline.setCycleCount(Timeline.INDEFINITE);
        displayTimeline.setCycleCount(Timeline.INDEFINITE);

        fileReadTimeline.play();
        displayTimeline.play();
    }
}
