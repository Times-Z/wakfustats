package com.wakfoverlay.domain.player.port.primary;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;

public interface UpdatePlayerDamages {
    Players update(Player player, Integer damages);
    // TODO: remove when real data is available
    void updatePlayers();
}
