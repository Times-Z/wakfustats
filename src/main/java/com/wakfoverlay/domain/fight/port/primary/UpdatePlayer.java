package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;

public interface UpdatePlayer {
    void update(Character character, Integer damages);
    void resetPlayersDamages();
}
