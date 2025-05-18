package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;

import java.time.Duration;
import java.util.Optional;

public record UpdateCharacterUseCase(
        CharactersRepository charactersRepository,
        DamagesRepository damagesRepository
) implements UpdateCharacter {
    private static final int MAX_TIMESTAMP_DIFFERENCE_MILLIS = 800;

    @Override
    public void update(Character character, Damages damages) {
        Optional<Damages> existingDamages = damagesRepository.find(damages)
                .stream()
                .findFirst();

        if (existingDamages.isEmpty() || !areTimestampsTooClose(existingDamages.get(), damages)) {
            damagesRepository.addDamages(damages);
            addOrUpdate(character, damages);
        }
    }

    @Override
    public void resetCharacterDamages() {
        charactersRepository.resetCharacters();
    }

    private boolean areTimestampsTooClose(Damages existingDamages, Damages incommingDamages) {
        Duration duration = Duration.between(existingDamages.timestamp(), incommingDamages.timestamp()).abs();
        return duration.toMillis() <= MAX_TIMESTAMP_DIFFERENCE_MILLIS;
    }

    private void addOrUpdate(Character character, Damages damages) {
        Character updatedCharacter = new Character(
                character.name(),
                character.damages() + damages.amount()
        );

        charactersRepository.addOrUpdate(updatedCharacter);
    }
}
