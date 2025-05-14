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
        players.put("Joueur 1", new Player("Joueur 1", 0));
        players.put("Joueur 2", new Player("Joueur 2", 0));
        players.put("Joueur 3", new Player("Joueur 3", 0));
        players.put("Joueur 4", new Player("Joueur 4", 0));
        players.put("Joueur 5", new Player("Joueur 5", 0));
        players.put("Joueur 6", new Player("Joueur 6", 0));
    }

    @Override
    public Players allPlayers() {
        return new Players(new ArrayList<>(players.values()));
    }

    @Override
    public Players updatePlayer(Player player, int damages) {
        Player existingPlayer = players.get(player.name());
        if (existingPlayer != null) {
            int newDamages = existingPlayer.damages() + damages;
            players.put(player.name(), new Player(player.name(), newDamages));
        }
        return allPlayers();
    }

    @Override
    public void resetPlayersDamages() {
        players.replaceAll((name, player) -> new Player(name, 0));
    }
}
