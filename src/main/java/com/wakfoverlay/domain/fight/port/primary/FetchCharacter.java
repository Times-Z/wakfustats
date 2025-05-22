package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;

import java.util.Optional;

public interface FetchCharacter {
    Character character(CharacterName name);
    Characters rankedCharactersByDamages();
    Characters rankedCharactersByHeals();
    Characters rankedCharactersByShields();
    boolean exist(CharacterName name);
    boolean isAllied(CharacterName characterName);
}
