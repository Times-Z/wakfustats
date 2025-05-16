package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;

import java.util.Optional;

public interface StatusEffectRepository {
    void addOrUpdate(StatusEffect statusEffect, Character.CharacterName characterName);
    Character.CharacterName characterFor(StatusEffectName name);
    void resetStatusEffects();
}
