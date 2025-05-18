package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Damages;

import java.util.Optional;

public interface DamagesRepository {
    void addDamages(Damages damages);
    Optional<Damages> find(Damages damages);
}
