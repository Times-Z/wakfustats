package com.wakfoverlay.domain.player;

import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.FetchPlayer;
import com.wakfoverlay.domain.player.port.secondary.PlayersData;

import java.util.ArrayList;
import java.util.stream.Collectors;

public record FetchPlayerUseCase(
        PlayersData playersData
) implements FetchPlayer {
    @Override
    public Players rankedPlayers() {
        return new Players(playersData.allPlayers()
                .players()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.damages(), p1.damages()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
