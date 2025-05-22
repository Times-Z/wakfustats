package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.StatusEffect;

public interface UpdateStatusEffect {
    void update(StatusEffect statusEffect, CharacterName characterName);
    void resetStatusEffects();
}
