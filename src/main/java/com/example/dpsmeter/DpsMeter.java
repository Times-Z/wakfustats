package com.example.dpsmeter;

import com.example.player.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;

public class DpsMeter extends VBox {
    private final StubPlayersData stubPlayersData;

    public DpsMeter() {
        this.setSpacing(2);
        this.stubPlayersData = new StubPlayersData(this);
        updateDisplay(); // Appel initial pour afficher les données
    }

    public void updateDisplay() {
        this.getChildren().clear();

        Player[] players = stubPlayersData.getPlayers();
        // Tri des joueurs par dégats décroissants
        Arrays.sort(players, (p1, p2) -> Integer.compare(p2.damages(), p1.damages()));

        int totalDamages = Arrays.stream(players).mapToInt(Player::damages).sum();

        for (int i = 0; i < players.length; i++) {
            Player player = players[i];

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
