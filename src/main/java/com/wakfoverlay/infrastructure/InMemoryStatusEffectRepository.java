package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.HashMap;
import java.util.Map;

import static com.wakfoverlay.domain.logs.TheNormalizer.normalize;

public class InMemoryStatusEffectRepository implements StatusEffectRepository {
    private final Map<StatusEffect, CharacterName> statusEffects = new HashMap<>();

    @Override
    public void addOrUpdate(StatusEffect statusEffect, CharacterName characterName) {
        statusEffects.put(statusEffect, characterName);
    }

    @Override
    public CharacterName characterFor(StatusEffectName name) {
        return statusEffects.entrySet().stream()
                .filter(entry -> normalize(entry.getKey().subType().name()).equals(normalize(name.value())))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new CharacterName("Unknown"));
    }
}
