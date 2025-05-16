package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.StatusEffectRepository;

import java.util.Optional;

public class FetchStatusEffectUseCase implements FetchStatusEffect {
    private final StatusEffectRepository statusEffectRepository;

    public FetchStatusEffectUseCase(StatusEffectRepository statusEffectRepository) {
        this.statusEffectRepository = statusEffectRepository;
    }

    @Override
    public Character.CharacterName characterFor(StatusEffect.StatusEffectName name) {
        return statusEffectRepository.characterFor(name);
    }
}
