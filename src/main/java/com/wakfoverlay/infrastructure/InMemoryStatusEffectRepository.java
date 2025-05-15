package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryStatusEffectRepository implements StatusEffectRepository {
    private final Map<StatusEffect, Character.CharacterName> statusEffects = new HashMap<>();

    @Override
    public void addOrUpdate(StatusEffect statusEffect, Character.CharacterName characterName) {
        statusEffects.put(statusEffect, characterName);
    }

    @Override
    public Optional<Character.CharacterName> characterFor(StatusEffect.StatusEffectName name) {
        return statusEffects.keySet()
                .stream()
                .filter(it -> it.name().equals(name))
                .findFirst()
                .map(statusEffects::get);
    }

    @Override
    public void resetStatusEffects() {
        statusEffects.clear();
    }
}
