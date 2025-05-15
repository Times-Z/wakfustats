package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

public record FetchCharacterUseCase(
        CharactersRepository charactersRepository
) implements com.wakfoverlay.domain.fight.port.primary.FetchCharacter {
    @Override
    public Character character(CharacterName name) {
        return charactersRepository.character(name)
                .stream()
                .findFirst()
                .orElse(new Character(name, 0));
    }

    @Override
    public Characters rankedCharacters() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.damages(), p1.damages()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
