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
    public void resetPlayersDamages() {
        playersData.resetPlayersDamages();
    }

    @Override
    public void updatePlayers() {
        Players players = playersData.allPlayers();

        Random random = new Random();
        int index = random.nextInt(players.players().size());
        Player player = players.players().get(index);

        int damages = random.nextInt(300 - 100 + 1) + 50;

        playersData.updatePlayer(player, damages);
    }
}
