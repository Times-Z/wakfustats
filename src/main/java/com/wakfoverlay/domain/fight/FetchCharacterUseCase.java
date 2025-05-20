package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public record FetchCharacterUseCase(
        CharactersRepository charactersRepository
) implements FetchCharacter {
    @Override
    public Character character(CharacterName name) {
        return charactersRepository.character(name)
                .stream()
                .findFirst()
                .orElse(new Character(name, 0, 0, 0, false));
    }

    @Override
    public Characters rankedCharactersByDamages() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .filter(character -> !character.isControlledByAI())
                .sorted((p1, p2) -> Integer.compare(p2.damages(), p1.damages()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public Characters rankedCharactersByHeals() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .filter(character -> !character.isControlledByAI())
                .sorted((p1, p2) -> Integer.compare(p2.heals(), p1.heals()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public Characters rankedCharactersByShields() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .filter(character -> !character.isControlledByAI())
                .sorted((p1, p2) -> Integer.compare(p2.shields(), p1.shields()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public boolean isAllied(CharacterName characterName) {
        return charactersRepository.characters()
                .characters()
                .stream()
                .anyMatch(character -> character.name().equals(characterName) && !character.isControlledByAI());
    }
}
