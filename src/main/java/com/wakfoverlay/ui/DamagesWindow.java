package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.FetchPlayer;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DamagesWindow extends VBox {
    private final FetchPlayer fetchPlayer;

    public DamagesWindow(FetchPlayer players) {
        this.setSpacing(2);
        this.fetchPlayer = players;
    }

    public void updateDisplay() {
        this.getChildren().clear();

        Players rankedPlayers = fetchPlayer.rankedPlayers();
        int totalDamages = rankedPlayers.players().stream().mapToInt(Player::damages).sum();

        for (int i = 0; i < rankedPlayers.players().size(); i++) {
            Player player = rankedPlayers.players().get(i);

            HBox playerBox = new HBox();
            playerBox.setSpacing(5);

            Label rangLabel = new Label((i + 1) + ".");
            Label nomLabel = new Label(player.name());

            Pane barrePane = new Pane();
            barrePane.setPrefSize(100, 20);

            Rectangle contour = new Rectangle(0, 0, 100, 20);
            contour.setStroke(Color.GRAY);
            contour.setFill(Color.TRANSPARENT);

            Rectangle barre = new Rectangle(0, 0, (totalDamages == 0 ? 0 : (player.damages() / (double) totalDamages) * 100), 20);
            barre.setFill(Color.color(0, 0, 1, 0.5));

            Label degatsLabel = new Label(player.damages() + " (" + String.format("%.2f", ((double) player.damages() / totalDamages) * 100) + "%)");

            barrePane.getChildren().addAll(contour, barre);

            playerBox.getChildren().addAll(rangLabel, nomLabel, barrePane, degatsLabel);
            this.getChildren().add(playerBox);
        }
    }
}
