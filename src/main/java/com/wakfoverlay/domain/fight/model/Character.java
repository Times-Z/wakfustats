package com.wakfoverlay.domain.fight.model;

public record Character(
        CharacterName name,
        int damages,
        int heals,
        int shields,
        boolean isControlledByAI
) {
    public record CharacterName(String value) {
    }
}