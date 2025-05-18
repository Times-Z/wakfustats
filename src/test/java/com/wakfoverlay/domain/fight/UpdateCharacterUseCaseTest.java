package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;
import com.wakfoverlay.infrastructure.InMemoryCharactersRepository;
import com.wakfoverlay.infrastructure.InMemoryDamagesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCharacterUseCaseTest {

    private InMemoryCharactersRepository charactersRepository;
    private InMemoryDamagesRepository damagesRepository;
    private UpdateCharacterUseCase updateCharacterUseCase;

    @BeforeEach
    void setUp() {
        charactersRepository = new InMemoryCharactersRepository();
        damagesRepository = new InMemoryDamagesRepository();
        updateCharacterUseCase = new UpdateCharacterUseCase(charactersRepository, damagesRepository);
    }

    @Test
    void should_add_Damages_when_timestamp_difference_is_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new Character.CharacterName("TestCharacter"), 100);
        charactersRepository.addOrUpdate(character);

        Damages newDamages = new Damages(now, 50, Set.of("Fire", "Ice"));

        // When
        updateCharacterUseCase.update(character, newDamages);

        // Then
        assertEquals(1, damagesRepository.getDamagesMap().size());
        assertEquals(150, charactersRepository.getCharacters().get(character.name()).damages());
    }

    @Test
    void should_not_add_Damages_when_timestamp_difference_is_not_enough() {
        // Given
        LocalTime now = LocalTime.of(12, 0, 0);
        Character character = new Character(new Character.CharacterName("TestCharacter"), 100);
        charactersRepository.addOrUpdate(character);

        Damages existingDamages = new Damages(now.minus(500, MILLIS), 50, Set.of("Fire", "Ice"));
        damagesRepository.addDamages(existingDamages);

        Damages newDamages = new Damages(now, 50, Set.of("Fire", "Ice"));

        // When
        damagesRepository.addDamages(newDamages);
        updateCharacterUseCase.update(character, newDamages);

        // Then
        assertEquals(100, charactersRepository.getCharacters().get(character.name()).damages());
    }
}
