package com.wakfoverlay.domain.player.port.secondary;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;

public interface PlayersData {
    Players allPlayers();
    Players updatePlayer(Player player, int damages);
}
