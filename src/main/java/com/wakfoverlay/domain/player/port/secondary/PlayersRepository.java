package com.wakfoverlay.domain.player.port.secondary;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;

public interface PlayersRepository {
    Players allPlayers();
    Players addOrUpdate(Player player, int damages);
    void resetPlayers();
}
