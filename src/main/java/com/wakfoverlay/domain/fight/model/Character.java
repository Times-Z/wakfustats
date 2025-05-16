package com.wakfoverlay.domain.fight.model;

public record Character(
        CharacterName name,
        int damages
) {
    public record CharacterName(String value) {
    }
}