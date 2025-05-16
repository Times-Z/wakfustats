package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCharactersRepository implements CharactersRepository {
    private final Map<CharacterName, Character> characters = new HashMap<>();

    public InMemoryCharactersRepository() {
    }

    @Override
    public Optional<Character> character(CharacterName name) {
        if (name == null || name.value().trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(characters.get(name));
    }

    @Override
    public Characters characters() {
        return new Characters(new ArrayList<>(characters.values()));
    }

    @Override
    public void addOrUpdate(Character character) {
        System.out.println("Adding or updating character: " + character);
        characters.put(character.name(), character);
    }

    @Override
    public void resetCharacters() {
        characters.clear();
    }
}
