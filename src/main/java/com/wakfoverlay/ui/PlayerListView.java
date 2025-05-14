package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.List;

import static javafx.scene.layout.Priority.ALWAYS;

public class PlayerListView extends VBox {
    private static final List<String> PLAYER_COLORS = Arrays.asList(
            "#33fffe", "#EE1DED", "#7DFA00", "#FF4A00", "#FBE33B", "#6D1FA9"
    );

    private static final int NAME_MAX_LENGTH = 20;
    private static final double NAME_COLUMN_WIDTH = 120;

    public PlayerListView(Players playersData) {
        this.setSpacing(2);

        List<Player> players = playersData.players();
        int totalDamages = calculateTotalDamages(players);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            HBox playerRow = createPlayerRow(player, i, totalDamages);
            this.getChildren().add(playerRow);
        }
    }

    private int calculateTotalDamages(List<Player> players) {
        return players.stream().mapToInt(Player::damages).sum();
    }

    private HBox createPlayerRow(Player player, int index, int totalDamages) {
        HBox playerBox = new HBox();
        playerBox.setPadding(new Insets(2, 5, 2, 5));
        playerBox.setSpacing(10);

        Label nameLabel = createNameLabel(player);
        HBox nameContainer = new HBox(nameLabel);
        nameContainer.setPrefWidth(NAME_COLUMN_WIDTH);
        nameContainer.setMinWidth(NAME_COLUMN_WIDTH);

        Pane damageBar = createDamageBar(player, index, totalDamages);

        Label damageLabel = createDamageLabel(player, totalDamages);

        playerBox.getChildren().addAll(nameContainer, damageBar, damageLabel);
        return playerBox;
    }

    private Label createNameLabel(Player player) {
        String displayName = player.name();
        String fullName = player.name();

        if (displayName.length() > NAME_MAX_LENGTH) {
            displayName = displayName.substring(0, NAME_MAX_LENGTH - 3) + "...";
        }

        Label label = new Label(displayName);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        label.setUserData(fullName);

        label.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(() -> {
            if (label.getScene() != null && label.getScene().getWindow() != null &&
                    label.getScene().getWindow().getWidth() > 500) {
                return fullName;
            } else {
                if (fullName.length() > NAME_MAX_LENGTH) {
                    return fullName.substring(0, NAME_MAX_LENGTH - 3) + "...";
                }
                return fullName;
            }
        }, label.sceneProperty(), label.widthProperty()));

        return label;
    }

    private Pane createDamageBar(Player player, int index, int totalDamages) {
        Pane pane = new Pane();
        pane.setPrefSize(100, 20);
        HBox.setHgrow(pane, ALWAYS);

        Rectangle border = new Rectangle(0, 0, 100, 20);
        border.setStroke(Color.GRAY);
        border.setFill(Color.TRANSPARENT);

        double damagePercentage = totalDamages == 0 ? 0 : (player.damages() / (double) totalDamages) * 100;
        Rectangle progressBar = new Rectangle(0, 0, damagePercentage, 20);

        String color = PLAYER_COLORS.get(index % PLAYER_COLORS.size());
        progressBar.setFill(Color.web(color));

        pane.getChildren().addAll(border, progressBar);
        return pane;
    }

    private Label createDamageLabel(Player player, int totalDamages) {
        double percentage = totalDamages == 0 ? 0 : ((double) player.damages() / totalDamages) * 100;
        String text = player.damages() + " (" + String.format("%.2f", percentage) + "%)";

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        return label;
    }
}
