package com.wakfoverlay.domain.fight.model;

import java.util.Optional;

public record Character(
        CharacterName name,
        int damages,
        int heals,
        int shields,
        boolean isControlledByAI,
        Optional<Character> summoner
) {
    // TODO: will remove when separate character from summons
    public Character(CharacterName name, int damages, int heals, int shields, boolean isControlledByAI) {
        this(name, damages, heals, shields, isControlledByAI, Optional.empty());
    }

    public record CharacterName(String value) {
    }
}