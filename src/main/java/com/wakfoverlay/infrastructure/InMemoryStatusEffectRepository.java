package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.HashMap;
import java.util.Map;

import static com.wakfoverlay.exposition.LogLineParser.normalize;

public class InMemoryStatusEffectRepository implements StatusEffectRepository {
    private final Map<StatusEffect, Character.CharacterName> statusEffects = new HashMap<>();

    @Override
    public void addOrUpdate(StatusEffect statusEffect, Character.CharacterName characterName) {
        statusEffects.put(statusEffect, characterName);
    }

    @Override
    public Character.CharacterName characterFor(StatusEffect.StatusEffectName name) {
        return statusEffects.keySet()
                .stream()
                .filter(it -> normalize(it.subType().name()).equals(name.value()))
                .findFirst()
                .map(statusEffects::get)
                .orElse(new Character.CharacterName("Unknown"));
    }

    @Override
    public void resetStatusEffects() {
        statusEffects.clear();
    }
}
