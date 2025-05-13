package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.FetchPlayer;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

import static javafx.scene.layout.Priority.ALWAYS;

public class DamagesWindow extends VBox {
    private final List<String> EVOLUTIVE_RECTANGLE_COLORS = Arrays.asList(
            "#33fffe", "#EE1DED", "#7DFA00", "#FF4A00", "#FBE33B", "#6D1FA9"
    );

    private final VBox playersContainer;
    private final Pane resizeHandle;

    private final FetchPlayer fetchPlayer;
    private final UpdatePlayerDamages updatePlayerDamages;

    public DamagesWindow(FetchPlayer players, UpdatePlayerDamages updatePlayerDamages) {
        this.setStyle("-fx-background-color: rgb(18, 18, 18); -fx-border-color: rgb(51, 51, 51); -fx-border-width: 1;");
        this.setSpacing(2);
        this.setPadding(new Insets(5, 5, 5, 5));

        this.fetchPlayer = players;
        this.updatePlayerDamages = updatePlayerDamages;

        HBox titleBar = createTitleBar();
        this.getChildren().add(titleBar);

        playersContainer = new VBox();
        playersContainer.setSpacing(2);

        ScrollPane scrollPane = new ScrollPane(playersContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: rgb(18, 18, 18); -fx-background-color: rgb(18, 18, 18);");

        VBox.setVgrow(scrollPane, ALWAYS);

        resizeHandle = new Pane();
        resizeHandle.setPrefHeight(5);
        resizeHandle.setMaxHeight(5);
        resizeHandle.setMinHeight(5);
        resizeHandle.setStyle("-fx-background-color: transparent;");
        resizeHandle.setCursor(Cursor.S_RESIZE);

        this.getChildren().addAll(scrollPane, resizeHandle);

        setupResizeHandlers();
    }

    private void setupResizeHandlers() {
        final double[] startY = new double[1];
        final double[] startHeight = new double[1];

        resizeHandle.setOnMousePressed(event -> {
            startY[0] = event.getScreenY();
            if (getScene() != null && getScene().getWindow() != null) {
                startHeight[0] = getScene().getWindow().getHeight();
            }
            event.consume();
        });

        resizeHandle.setOnMouseDragged(event -> {
            if (getScene() != null && getScene().getWindow() != null) {
                Stage stage = (Stage) getScene().getWindow();
                double deltaY = event.getScreenY() - startY[0];
                double newHeight = startHeight[0] + deltaY;
                if (newHeight > 100) {
                    stage.setHeight(newHeight);
                }
            }
            event.consume();
        });
    }

    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setPadding(new Insets(5));
        titleBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label label = new Label("DPT Meter");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, ALWAYS);

        Button resetButton = createIconButton("🔄", "#3498db");
        resetButton.setOnAction(event -> resetStats());

        Button closeButton = createIconButton("✖", "#e74c3c");
        closeButton.setOnAction(event -> closeWindow());

        titleBar.getChildren().addAll(label, spacer, resetButton, closeButton);

        return titleBar;
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

    private void resetStats() {
        updatePlayerDamages.resetPlayersDamages();
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void updateDisplay() {
        playersContainer.getChildren().clear();

        Players rankedPlayers = fetchPlayer.rankedPlayers();
        int totalDamages = rankedPlayers.players().stream().mapToInt(Player::damages).sum();

        for (int i = 0; i < rankedPlayers.players().size(); i++) {
            Player player = rankedPlayers.players().get(i);

            HBox playerBox = new HBox();
            playerBox.setPadding(new Insets(2, 5, 2, 5));
            playerBox.setSpacing(10);

            Label playerNameLabel = new Label(player.name());
            playerNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            playerNameLabel.setMinWidth(50);

            Pane pane = new Pane();
            pane.setPrefSize(100, 20);
            HBox.setHgrow(pane, ALWAYS);

            Rectangle border = new Rectangle(0, 0, 100, 20);
            border.setStroke(Color.GRAY);
            border.setFill(Color.TRANSPARENT);

            Rectangle evolutiveRectangle = new Rectangle(0, 0, (totalDamages == 0 ? 0 : (player.damages() / (double) totalDamages) * 100), 20);
            String bgColor = EVOLUTIVE_RECTANGLE_COLORS.get(i % EVOLUTIVE_RECTANGLE_COLORS.size());
            evolutiveRectangle.setFill(Color.web(bgColor));

            Label damageLabel = new Label(player.damages() + " (" + String.format("%.2f", ((double) player.damages() / totalDamages) * 100) + "%)");
            damageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            pane.getChildren().addAll(border, evolutiveRectangle);

            playerBox.getChildren().addAll(playerNameLabel, pane, damageLabel);

            playersContainer.getChildren().add(playerBox);
        }
    }
}
