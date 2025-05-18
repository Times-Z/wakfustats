package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Heals;

import java.util.Optional;

public interface HealsRepository {
    void addHeals(Heals heals);
    Optional<Heals> find(Heals heals);
}
