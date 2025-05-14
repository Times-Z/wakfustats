package com.wakfoverlay.domain.player;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.domain.player.port.secondary.PlayersRepository;

public record UpdatePlayerDamagesUseCase(
        PlayersRepository playersRepository
) implements UpdatePlayerDamages {

    @Override
    public Players update(Player player, Integer damages) {
        Player updatedPlayer = new Player(player.name(), player.damages() + damages);
        return playersRepository.addOrUpdate(updatedPlayer);
    }

    @Override
    public void resetPlayersDamages() {
        playersRepository.resetPlayers();
    }
}
