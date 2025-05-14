package com.wakfoverlay.domain.player;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.domain.player.port.secondary.PlayersRepository;

import java.util.Random;

public record UpdatePlayerDamagesUseCase(
        PlayersRepository playersRepository
) implements UpdatePlayerDamages {

    @Override
    public Players update(Player player, Integer damages) {
        return playersRepository.updatePlayer(player, damages);
    }

    @Override
    public void resetPlayersDamages() {
        playersRepository.resetPlayersDamages();
    }
}
