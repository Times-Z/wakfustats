package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.model.Heals;
import com.wakfoverlay.domain.fight.model.Shields;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;
import com.wakfoverlay.domain.fight.port.secondary.HealsRepository;
import com.wakfoverlay.domain.fight.port.secondary.ShieldsRepository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.*;

public record UpdateCharacterUseCase(
        CharactersRepository charactersRepository,
        DamagesRepository damagesRepository,
        HealsRepository healsRepository,
        ShieldsRepository shieldsRepository
) implements UpdateCharacter {
    private static final int MAX_TIMESTAMP_DIFFERENCE_MILLIS = 800;

    @Override
    public void create(Character character) {
        Optional<Character> existingCharacter = charactersRepository.character(character.name());

        if (existingCharacter.isEmpty()) {
            charactersRepository.addOrUpdate(character);
        }
    }

    public void update(Character character) {
        charactersRepository.addOrUpdate(character);
    }

    @Override
    public void updateDamages(Character character, Damages damages) {
        Optional<Damages> existingDamages = damagesRepository.find(damages)
                .stream()
                .findFirst();

        if (existingDamages.isEmpty() || !areDamagesTooClose(existingDamages.get(), damages)) {
            damagesRepository.addDamages(damages);
            addOrUpdateDamages(character, damages);
        }
    }

    @Override
    public void updateHeals(Character character, Heals heals) {
        Optional<Heals> existingHeals = healsRepository.find(heals)
                .stream()
                .findFirst();

        if (existingHeals.isEmpty() || !areHealsTooClose(existingHeals.get(), heals)) {
            healsRepository.addHeals(heals);
            addOrUpdateHeals(character, heals);
        }
    }

    @Override
    public void updateShields(Character character, Shields shields) {
        Optional<Shields> existingShields = shieldsRepository.find(shields)
                .stream()
                .findFirst();

        if (existingShields.isEmpty() || !areShieldsTooClose(existingShields.get(), shields)) {
            shieldsRepository.addShields(shields);
            addOrUpdateShields(character, shields);
        }
    }

    @Override
    public void resetCharacters() {
        charactersRepository.resetCharacters();
    }

    private boolean areDamagesTooClose(Damages existingDamages, Damages incomingDamages) {
        Duration duration = Duration.between(existingDamages.timestamp(), incomingDamages.timestamp()).abs();
        return duration.toMillis() <= MAX_TIMESTAMP_DIFFERENCE_MILLIS;
    }

    private boolean areHealsTooClose(Heals existingHeals, Heals incomingHeals) {
        Duration duration = Duration.between(existingHeals.timestamp(), incomingHeals.timestamp()).abs();
        return duration.toMillis() <= MAX_TIMESTAMP_DIFFERENCE_MILLIS;
    }

    private boolean areShieldsTooClose(Shields existingShields, Shields incomingShields) {
        Duration duration = Duration.between(existingShields.timestamp(), incomingShields.timestamp()).abs();
        return duration.toMillis() <= MAX_TIMESTAMP_DIFFERENCE_MILLIS;
    }

    private void addOrUpdateDamages(Character character, Damages damages) {
        if (Objects.equals(character.name().value(), "Jeanne Jackeline Sizt")) {
            System.out.println("Character damages before update : " + character.damages());
        }
        Character updatedCharacter = new Character(
                character.name(),
                character.damages() + damages.amount(),
                character.heals(),
                character.shields(),
                character.summoner()
        );

        charactersRepository.addOrUpdate(updatedCharacter);
    }

    private void addOrUpdateHeals(Character character, Heals heals) {
        Character updatedCharacter = new Character(
                character.name(),
                character.damages(),
                character.heals() + heals.amount(),
                character.shields(),
                character.summoner()
        );

        charactersRepository.addOrUpdate(updatedCharacter);
    }

    private void addOrUpdateShields(Character character, Shields shields) {
        Character updatedCharacter = new Character(
                character.name(),
                character.damages(),
                character.heals(),
                character.shields() + shields.amount(),
                character.summoner()
        );

        charactersRepository.addOrUpdate(updatedCharacter);
    }
}
