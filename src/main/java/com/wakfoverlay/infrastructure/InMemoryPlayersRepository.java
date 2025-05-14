package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.secondary.PlayersRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPlayersRepository implements PlayersRepository {
    private final Map<String, Player> players = new HashMap<>();

    public InMemoryPlayersRepository() {
    }

    @Override
    public Players allPlayers() {
        return new Players(new ArrayList<>(players.values()));
    }

    @Override
    public Players addOrUpdate(Player player) {
        players.put(player.name(), player);
        return allPlayers();
    }

    @Override
    public void resetPlayers() {
        players.clear();
    }
}
