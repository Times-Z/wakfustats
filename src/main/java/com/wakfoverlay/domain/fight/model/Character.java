package com.wakfoverlay.domain.fight.model;

import java.util.Optional;

public record Character(
        CharacterName name,
        int damages,
        int heals,
        int shields,
        Optional<Character> summoner
) {
    public record CharacterName(String value) {
    }
}