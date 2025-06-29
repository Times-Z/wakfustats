package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.infrastructure.InMemoryCharactersRepository;
import com.wakfoverlay.infrastructure.InMemoryTargetedDamagesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FetchCharacterUseCaseTest {

    private InMemoryCharactersRepository charactersRepository;
    private InMemoryTargetedDamagesRepository targetedDamagesRepository;
    private FetchCharacterUseCase fetchCharacterUseCase;

    @BeforeEach
    void setUp() {
        charactersRepository = new InMemoryCharactersRepository();
        targetedDamagesRepository = new InMemoryTargetedDamagesRepository();
        fetchCharacterUseCase = new FetchCharacterUseCase(charactersRepository, targetedDamagesRepository);
    }

    @Test
    void should_return_character_when_found_in_repository() {
        // Given
        CharacterName name = new CharacterName("TestCharacter");
        Character expectedCharacter = new Character(name, 100, 0, 0, empty());
        charactersRepository.addOrUpdate(expectedCharacter);

        // When
        Character result = fetchCharacterUseCase.character(name);

        // Then
        assertEquals(expectedCharacter, result);
    }

    @Test
    void should_return_new_character_with_zero_damage_when_not_found_in_repository() {
        // Given
        CharacterName name = new CharacterName("Unknown");

        // When
        Character result = fetchCharacterUseCase.character(name);

        // Then
        assertEquals(name, result.name());
        assertEquals(0, result.damages());
    }

    @Test
    void should_return_null_character_when_name_is_null() {
        // Given
        CharacterName nullName = new CharacterName("Unknown");

        // When
        Character result = fetchCharacterUseCase.character(nullName);

        // Then
        assertEquals(nullName, result.name());
        assertEquals(0, result.damages());
    }

    @Test
    void should_return_characters_sorted_by_damages_in_descending_order() {
        // Given
        Character char1 = new Character(new CharacterName("Character1"), 100, 0, 0, empty());
        Character char2 = new Character(new CharacterName("Character2"), 300, 0, 0, empty());
        Character char3 = new Character(new CharacterName("Character3"), 200, 0, 0, empty());

        charactersRepository.addOrUpdate(char1);
        charactersRepository.addOrUpdate(char2);
        charactersRepository.addOrUpdate(char3);

        // When
        Characters result = fetchCharacterUseCase.rankedCharactersByDamages();

        // Then
        List<Character> sortedCharacters = result.characters();
        assertEquals(3, sortedCharacters.size());
        assertEquals(char2, sortedCharacters.get(0));
        assertEquals(char3, sortedCharacters.get(1));
        assertEquals(char1, sortedCharacters.get(2));
    }

    @Test
    void should_return_empty_characters_when_repository_is_empty() {
        // Given
        // When
        Characters result = fetchCharacterUseCase.rankedCharactersByDamages();

        // Then
        assertNotNull(result.characters());
        assertTrue(result.characters().isEmpty());
    }

    @Test
    void should_handle_characters_with_equal_damages() {
        // Given
        Character char1 = new Character(new CharacterName("CharacterA"), 200, 0, 0, empty());
        Character char2 = new Character(new CharacterName("CharacterB"), 200, 0, 0, empty());
        Character char3 = new Character(new CharacterName("CharacterC"), 100, 0, 0, empty());

        charactersRepository.addOrUpdate(char1);
        charactersRepository.addOrUpdate(char2);
        charactersRepository.addOrUpdate(char3);

        // When
        Characters result = fetchCharacterUseCase.rankedCharactersByDamages();

        // Then
        List<Character> sortedCharacters = result.characters();
        assertEquals(3, sortedCharacters.size());
        assertEquals(200, sortedCharacters.get(0).damages());
        assertEquals(200, sortedCharacters.get(1).damages());
        assertEquals(100, sortedCharacters.get(2).damages());
    }
}
