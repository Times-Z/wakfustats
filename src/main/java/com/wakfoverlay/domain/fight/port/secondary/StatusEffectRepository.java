package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;

import java.util.Map;

public interface StatusEffectRepository {
    void addOrUpdate(StatusEffect statusEffect, Character.CharacterName characterName);
    Map<StatusEffect, Character.CharacterName> statusEffects();
    void resetStatusEffects();
}
