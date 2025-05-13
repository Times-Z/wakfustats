package com.wakfoverlay.domain.player;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.domain.player.port.secondary.PlayersData;

import java.util.Random;

public record UpdatePlayerDamagesUseCase(
        PlayersData playersData
) implements UpdatePlayerDamages {

    @Override
    public Players update(Player player, Integer damages) {
        return playersData.updatePlayer(player, damages);
    }

    @Override
    public void updatePlayers() {
        // Récupérer tous les joueurs
        Players players = playersData.allPlayers();

        // Sélectionner un joueur aléatoire
        Random random = new Random();
        int index = random.nextInt(players.players().size());
        Player player = players.players().get(index);

        // Générer des dégats aléatoires entre 50 et 300
        int damages = random.nextInt(300 - 100 + 1) + 50;

        // Mettre à jour les dégats du joueur sélectionné
        playersData.updatePlayer(player, damages);
    }
}
