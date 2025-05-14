package com.wakfoverlay.domain.player.port.primary;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;

public interface FetchPlayer {
    Player player(String name);
    Players rankedPlayers();
}
