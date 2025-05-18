package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Shields;
import com.wakfoverlay.domain.fight.port.secondary.ShieldsRepository;

import java.util.Optional;

public class InMemoryShieldsRepository implements ShieldsRepository {
    @Override
    public void addShields(Shields shields) {

    }

    @Override
    public Optional<Shields> find(Shields shields) {
        return Optional.empty();
    }
}
