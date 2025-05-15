package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.Map;

public class UpdateStatusEffectUseCase implements UpdateStatusEffect {
    private final StatusEffectRepository statusEffectRepository;

    public UpdateStatusEffectUseCase(StatusEffectRepository statusEffectRepository) {
        this.statusEffectRepository = statusEffectRepository;
    }

    @Override
    public void update(StatusEffect statusEffect, Character.CharacterName characterName) {
        statusEffectRepository.addOrUpdate(statusEffect, characterName);
    }

    @Override
    public void resetStatusEffects() {
        statusEffectRepository.resetStatusEffects();
    }
}
