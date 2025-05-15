package com.wakfoverlay.domain.fight.model;

public record Character(
        CharacterName name,
        Integer damages
) {
    public record CharacterName(String value) {
    }
}