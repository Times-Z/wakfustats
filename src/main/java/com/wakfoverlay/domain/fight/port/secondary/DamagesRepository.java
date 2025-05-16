package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Damages;

public interface DamagesRepository {
    void addDamages(Damages damages);
    boolean exists(Damages damages);
}
