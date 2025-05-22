package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;

import java.util.Optional;

public interface CharactersRepository {
    Optional<Character> character(CharacterName name);
    Characters characters();
    void addOrUpdate(Character character);
    void resetCharactersStats();
    void resetCharacters();
}
