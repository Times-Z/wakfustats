package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.port.primary.UpdatePlayer;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;

public record UpdatePlayerUseCase(
        CharactersRepository charactersRepository
) implements UpdatePlayer {

    @Override
    public void update(Character character, Integer damages) {
        Character updatedCharacter = new Character(character.name(), character.damages() + damages);
        charactersRepository.addOrUpdate(updatedCharacter);
    }

    @Override
    public void resetPlayersDamages() {
        charactersRepository.resetCharacters();
    }
}
