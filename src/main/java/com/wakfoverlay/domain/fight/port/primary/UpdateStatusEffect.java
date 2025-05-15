package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;

import java.util.Map;

public interface UpdateStatusEffect {
    void update(StatusEffect statusEffect, Character.CharacterName characterName);
    Map<StatusEffect, Character.CharacterName> all();
    void resetStatusEffects();
}
