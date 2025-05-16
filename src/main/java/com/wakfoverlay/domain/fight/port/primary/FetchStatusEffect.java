package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;

import java.util.Optional;

public interface FetchStatusEffect {
    CharacterName characterFor(StatusEffectName name);
}
