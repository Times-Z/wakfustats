package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStatusEffectRepository implements StatusEffectRepository {
    private final Map<StatusEffect, Character.CharacterName> statusEffects = new HashMap<>();

    @Override
    public void addOrUpdate(StatusEffect statusEffect, Character.CharacterName characterName) {
        statusEffects.put(statusEffect, characterName);
    }

    @Override
    public Map<StatusEffect, Character.CharacterName> statusEffects() {
        return statusEffects;
    }

    @Override
    public void resetStatusEffects() {
        statusEffects.clear();
    }

    // TODO: les status effect perdent la moitié de leur niveau a chaque fois qu'un dégat est infligé
    // pas tous :(
}
