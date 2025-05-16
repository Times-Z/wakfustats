package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;

public record UpdateCharacterUseCase(
        CharactersRepository charactersRepository,
        DamagesRepository damagesRepository
) implements UpdateCharacter {

    @Override
    public void update(Character character, Damages damages) {
        boolean duplicated = damagesRepository.exists(damages);

        if (duplicated) {
            return;
        }
        damagesRepository.addDamages(damages);
        Character updatedCharacter = new Character(character.name(), character.damages() + damages.amount());
        charactersRepository.addOrUpdate(updatedCharacter);
    }

    @Override
    public void resetCharacterDamages() {
        charactersRepository.resetCharacters();
    }
}
