package com.wakfoverlay.domain.player.port.secondary;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;

import java.util.Optional;

public interface PlayersRepository {
    Optional<Player> player(String name);
    Players allPlayers();
    Players addOrUpdate(Player player);
    void resetPlayers();
}
