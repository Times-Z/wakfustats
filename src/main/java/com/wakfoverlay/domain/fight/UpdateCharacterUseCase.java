package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;

public record UpdateCharacterUseCase(
        CharactersRepository charactersRepository
) implements UpdateCharacter {

    @Override
    public void update(Character character, Integer damages) {
        Character updatedCharacter = new Character(character.name(), character.damages() + damages);
        charactersRepository.addOrUpdate(updatedCharacter);
    }

    @Override
    public void resetCharacterDamages() {
        charactersRepository.resetCharacters();
    }
}
