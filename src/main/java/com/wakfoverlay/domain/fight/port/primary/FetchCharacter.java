package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;

public interface FetchCharacter {
    Character character(CharacterName name);
    Characters rankedCharactersByDamages();
    Characters rankedCharactersByHeals();
    Characters rankedCharactersByShields();
    boolean isAllied(CharacterName characterName);
}
