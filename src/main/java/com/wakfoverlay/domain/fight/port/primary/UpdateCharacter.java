package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;

public interface UpdateCharacter {
    void update(Character character, Damages damages);
    void resetCharacterDamages();
}
