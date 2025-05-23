package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.FetchStatusEffectUseCase;
import com.wakfoverlay.domain.fight.UpdateCharacterUseCase;
import com.wakfoverlay.domain.fight.UpdateStatusEffectUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.infrastructure.InMemoryCharactersRepository;
import com.wakfoverlay.infrastructure.InMemoryDamagesRepository;
import com.wakfoverlay.infrastructure.InMemoryHealsRepository;
import com.wakfoverlay.infrastructure.InMemoryShieldsRepository;
import com.wakfoverlay.infrastructure.InMemoryStatusEffectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogParserE2ETest {
    private LogParser logParser;
    private InMemoryCharactersRepository charactersRepository;
    private InMemoryDamagesRepository damagesRepository;
    private InMemoryHealsRepository healsRepository;
    private InMemoryShieldsRepository shieldsRepository;
    private InMemoryStatusEffectRepository statusEffectRepository;
    private FetchCharacterUseCase fetchCharacter;
    private FetchStatusEffectUseCase fetchStatusEffect;
    private UpdateCharacterUseCase updateCharacter;
    private UpdateStatusEffectUseCase updateStatusEffect;

    private String testLogPath;

    @BeforeEach
    void setUp() {
        charactersRepository = new InMemoryCharactersRepository();
        damagesRepository = new InMemoryDamagesRepository();
        healsRepository = new InMemoryHealsRepository();
        shieldsRepository = new InMemoryShieldsRepository();
        statusEffectRepository = new InMemoryStatusEffectRepository();
        fetchCharacter = new FetchCharacterUseCase(charactersRepository);
        fetchStatusEffect = new FetchStatusEffectUseCase(statusEffectRepository);
        updateCharacter = new UpdateCharacterUseCase(charactersRepository, damagesRepository, healsRepository, shieldsRepository);
        updateStatusEffect = new UpdateStatusEffectUseCase(statusEffectRepository);

        logParser = new LogParser(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect);

        Path resourcePath = Paths.get("src", "test", "resources", "wakfu.log");
        testLogPath = resourcePath.toAbsolutePath().toString();

        charactersRepository.resetCharactersStats();
        charactersRepository.resetCharacters();
        damagesRepository.resetDamages();
        healsRepository.resetHeals();
        shieldsRepository.resetShields();
        statusEffectRepository.resetStatusEffects();
    }

    @Test
    void should_parse_log_file_and_display_results() {
        // When
        logParser.readNewLogLines(testLogPath, true);

        // Then
        Character osamoda = fetchCharacter.character(new CharacterName("Jean Jack Deuz"));
        assertEquals(526, osamoda.damages());
        assertEquals(0, osamoda.heals());
        assertEquals(5931, osamoda.shields());

        Character feca = fetchCharacter.character(new CharacterName("Jean Jack Qwartz"));
        assertEquals(196, feca.damages());
        assertEquals(0, feca.heals());
        assertEquals(9485, feca.shields());

        Character steamer = fetchCharacter.character(new CharacterName("Jean Jack Kinte"));
        assertEquals(36503, steamer.damages());
        assertEquals(0, steamer.heals());
        assertEquals(0, steamer.shields());

        Character roublard = fetchCharacter.character(new CharacterName("Jeanne Jackeline Deuz"));
        assertEquals(1381, roublard.damages());
        assertEquals(0, roublard.heals());
        assertEquals(0, roublard.shields());

        Character sadida = fetchCharacter.character(new CharacterName("Jeanne Jackeline Qwartz"));
        assertEquals(1614, sadida.damages());
        assertEquals(7185, sadida.heals());
        assertEquals(0, sadida.shields());

        Character cra = fetchCharacter.character(new CharacterName("Jeanne Jackeline Sizt"));
        assertEquals(20991, cra.damages());
        assertEquals(0, cra.heals());
        assertEquals(30, cra.shields()); // This is wrong
    }
}
