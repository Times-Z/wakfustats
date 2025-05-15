package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;

public interface UpdateCharacter {
    void update(Character character, int damages);
    void resetCharacterDamages();
}
