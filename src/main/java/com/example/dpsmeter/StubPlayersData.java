package com.example.dpsmeter;

import com.example.player.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Random;

public class StubPlayersData {
    private final Player[] players;
    private final DpsMeter dpsMeter;

    public StubPlayersData(DpsMeter dpsMeter) {
        this.dpsMeter = dpsMeter;
        this.players = new Player[]{
                new Player("Joueur 1", 0, 0),
                new Player("Joueur 2", 0, 0),
                new Player("Joueur 3", 0, 0),
                new Player("Joueur 4", 0, 0),
                new Player("Joueur 5", 0, 0),
                new Player("Joueur 6", 0, 0)
        };

        // Mettre à jour les dégats à intervalles réguliers
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> updatePlayers()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updatePlayers() {
        Random random = new Random();
        int index = random.nextInt(players.length);
        Player player = players[index];
        int damages = random.nextInt(30 - 10 + 1) + 10;

        // Mettre à jour les dégats du joueur sélectionné
        players[index] = new Player(player.name(), player.damages() + damages, 0);

        // Mettre à jour le total des dégats et les pourcentages
        int totalDamages = Arrays.stream(players).mapToInt(Player::damages).sum();
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            double percentage = (double) p.damages() / totalDamages * 100;
            players[i] = new Player(p.name(), p.damages(), percentage);
        }

        dpsMeter.updateDisplay();
    }

    public Player[] getPlayers() {
        return players;
    }
}
