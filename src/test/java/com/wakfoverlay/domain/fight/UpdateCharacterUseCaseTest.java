package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.model.Heals;
import com.wakfoverlay.infrastructure.InMemoryCharactersRepository;
import com.wakfoverlay.infrastructure.InMemoryDamagesRepository;
import com.wakfoverlay.infrastructure.InMemoryHealsRepository;
import com.wakfoverlay.infrastructure.InMemoryShieldsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Set;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateCharacterUseCaseTest {

    private InMemoryCharactersRepository charactersRepository;
    private InMemoryDamagesRepository damagesRepository;
    private InMemoryHealsRepository healsRepository;
    private InMemoryShieldsRepository shieldsRepository;
    private UpdateCharacterUseCase updateCharacterUseCase;

    @BeforeEach
    void setUp() {
        charactersRepository = new InMemoryCharactersRepository();
        damagesRepository = new InMemoryDamagesRepository();
        healsRepository = new InMemoryHealsRepository();
        shieldsRepository = new InMemoryShieldsRepository();
        updateCharacterUseCase = new UpdateCharacterUseCase(charactersRepository, damagesRepository, healsRepository, shieldsRepository);
    }

    @Test
    void should_add_Damages_when_timestamp_difference_is_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new Character.CharacterName("TestCharacter"), 100, 0, 0, empty());
        charactersRepository.addOrUpdate(character);

        Damages newDamages = new Damages(now, "Pouet", 50, Set.of("Fire", "Ice"));

        // When
        updateCharacterUseCase.updateDamages(character, newDamages, false, 1);

        // Then
        assertEquals(1, damagesRepository.getDamagesMap().size());
        assertEquals(150, charactersRepository.getCharacters().get(character.name()).damages());
    }

    @Test
    void should_not_add_Damages_when_timestamp_difference_is_not_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new Character.CharacterName("TestCharacter"), 100, 0, 0, empty());
        charactersRepository.addOrUpdate(character);

        Damages existingDamages = new Damages(now.minus(500, MILLIS), "Pouet", 50, Set.of("Fire", "Ice"));
        damagesRepository.addDamages(existingDamages);

        Damages newDamages = new Damages(now, "Pouet", 50, Set.of("Fire", "Ice"));

        // When
        damagesRepository.addDamages(newDamages);
        updateCharacterUseCase.updateDamages(character, newDamages, false, 1);

        // Then
        assertEquals(150, charactersRepository.getCharacters().get(character.name()).damages());
    }

    @Test
    void should_add_Heals_when_timestamp_difference_is_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new CharacterName("TestCharacter"), 100, 0, 0, empty());
        charactersRepository.addOrUpdate(character);

        Heals newHeals = new Heals(now, "Pouet", 50, Set.of("Fire", "Ice"));

        // When
        updateCharacterUseCase.updateHeals(character, newHeals, false, 1);

        // Then
        assertEquals(1, healsRepository.getHealsMap().size());
        assertEquals(50, charactersRepository.getCharacters().get(character.name()).heals());
    }

    @Test
    void should_not_add_Heals_when_timestamp_difference_is_not_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new Character.CharacterName("TestCharacter"), 100, 0, 0, empty());
        charactersRepository.addOrUpdate(character);

        Heals existingHeals = new Heals(now.minus(500, MILLIS), "Pouet", 50, Set.of("Fire", "Ice"));
        healsRepository.addHeals(existingHeals);

        Heals newHeals = new Heals(now, "Pouet", 50, Set.of("Fire", "Ice"));

        // When
        healsRepository.addHeals(newHeals);
        updateCharacterUseCase.updateHeals(character, newHeals, true, 2);

        // Then
        assertEquals(25, charactersRepository.getCharacters().get(character.name()).heals());
    }
}
