package com.wakfoverlay.domain.player;

import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.FetchPlayer;
import com.wakfoverlay.domain.player.port.secondary.PlayersRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public record FetchPlayerUseCase(
        PlayersRepository playersRepository
) implements FetchPlayer {
    @Override
    public Players rankedPlayers() {
        return new Players(playersRepository.allPlayers()
                .players()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.damages(), p1.damages()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
