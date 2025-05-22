package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Shields;

import java.util.Optional;

public interface ShieldsRepository {
    void addShields(Shields shields);
    Optional<Shields> find(Shields shields);
    void resetShields();
}
