package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.FetchStatusEffectUseCase;
import com.wakfoverlay.domain.fight.UpdateCharacterUseCase;
import com.wakfoverlay.domain.fight.UpdateStatusEffectUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.infrastructure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogParserE2ETest {
    private LogParser logParser;
    private InMemoryCharactersRepository charactersRepository;
    private InMemoryTargetedDamagesRepository targetedDamagesRepository;
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
        targetedDamagesRepository = new InMemoryTargetedDamagesRepository();
        damagesRepository = new InMemoryDamagesRepository();
        healsRepository = new InMemoryHealsRepository();
        shieldsRepository = new InMemoryShieldsRepository();
        statusEffectRepository = new InMemoryStatusEffectRepository();
        fetchCharacter = new FetchCharacterUseCase(charactersRepository, targetedDamagesRepository);
        fetchStatusEffect = new FetchStatusEffectUseCase(statusEffectRepository);
        updateCharacter = new UpdateCharacterUseCase(charactersRepository, damagesRepository, healsRepository, shieldsRepository);
        updateStatusEffect = new UpdateStatusEffectUseCase(statusEffectRepository);

        logParser = new LogParser(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect, targetedDamagesRepository);

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
        System.out.println("\n*****Damages*****");
        fetchCharacter.rankedCharactersByDamages().characters().forEach(character -> System.out.println(character.name().value() + ": " + character.damages()));
        System.out.println("\n*****Heals*****");
        fetchCharacter.rankedCharactersByHeals().characters().forEach(character -> System.out.println(character.name().value() + ": " + character.heals()));
        System.out.println("\n*****Shields*****");
        fetchCharacter.rankedCharactersByShields().characters().forEach(character -> System.out.println(character.name().value() + ": " + character.shields()));
    }
}
