package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.secondary.PlayersData;

import java.util.ArrayList;

public class InMemoryPlayersData implements PlayersData {
    private final ArrayList<Player> players = new ArrayList<Player>();

    public InMemoryPlayersData() {
        players.add(new Player("Joueur 1", 0));
        players.add(new Player("Joueur 2", 0));
        players.add(new Player("Joueur 3", 0));
        players.add(new Player("Joueur 4", 0));
        players.add(new Player("Joueur 5", 0));
        players.add(new Player("Joueur 6", 0));
    }

    @Override
    public Players allPlayers() {
        return new Players(players);
    }

    @Override
    public Players updatePlayer(Player player, int damages) {
        players.replaceAll(it -> {
            if (it.name().equals(player.name())) {
                Integer newDamages = it.damages() + damages;
                return new Player(it.name(), newDamages);
            }
            return it;
        });

        return new Players(players);
    }
}
