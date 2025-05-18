package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheAnalyzerTest {

    @Mock
    private FetchCharacterUseCase fetchCharacter;

    @Mock
    private FetchStatusEffect fetchStatusEffect;

    @Mock
    private UpdateCharacter updateCharacter;

    @Mock
    private UpdateStatusEffect updateStatusEffect;

    @Captor
    private ArgumentCaptor<Character> characterCaptor;

    @Captor
    private ArgumentCaptor<CharacterName> characterNameCaptor;

    @Captor
    private ArgumentCaptor<Damages> damagesCaptor;

    @Captor
    private ArgumentCaptor<StatusEffect> statusEffectCaptor;

    @Captor
    private ArgumentCaptor<StatusEffectName> statusEffectNameCaptor;

    private TheAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new TheAnalyzer(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect);
    }

    @Test
    void should_analyze_normal_spell_cast_log() {
        // Given
        String logLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(logLine);

        // Then
        verify(fetchCharacter).character(characterNameCaptor.capture());
        assertEquals("Jeanne Jackeline Qwartz", characterNameCaptor.getValue().value());
    }

    @Test
    void should_analyze_critical_spell_cast_log() {
        // Given
        String logLine = "INFO 13:14:14,947 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque (Critiques)";
        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(logLine);

        // Then
        verify(fetchCharacter).character(characterNameCaptor.capture());
        assertEquals("Jeanne Jackeline Qwartz", characterNameCaptor.getValue().value());
    }

    @Test
    void should_analyze_distortion_status_effect_log() {
        // Given
        String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Kinte lance le sort Bourrasque";
        String statusLogLine = "INFO 10:07:24,886 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Kinte: Distorsion (Niv.8)";
        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Kinte"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(spellLogLine);
        analyzer.analyze(statusLogLine);

        // Then
        verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockCharacter.name()));

        StatusEffect capturedEffect = statusEffectCaptor.getValue();
        assertEquals("distorsion", capturedEffect.name().value());
        assertEquals(DISTORSION, capturedEffect.subType());
    }

    @Test
    void should_analyze_precision_status_effect_log() {
        // Given
        String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Sizt lance le sort Bourrasque";
        String statusLogLine = "INFO 13:54:48,615 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Sizt: Précision (+160 Niv.)";
        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Sizt"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(spellLogLine);
        analyzer.analyze(statusLogLine);

        // Then
        verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockCharacter.name()));

        StatusEffect capturedEffect = statusEffectCaptor.getValue();
        assertEquals("precision", capturedEffect.name().value());
        assertEquals(NO_SUB_TYPE, capturedEffect.subType());
    }

    @Test
    void should_analyze_toxines_status_effect_log() {
        // Given
        String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
        String statusLogLine = "INFO 13:14:14,000 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: Toxines (Niv.2)";
        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(spellLogLine);
        analyzer.analyze(statusLogLine);

        // Then
        verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockCharacter.name()));

        StatusEffect capturedEffect = statusEffectCaptor.getValue();
        assertEquals("toxines", capturedEffect.name().value());
        assertEquals(TETATOXINE, capturedEffect.subType());
    }

    @Test
    void should_analyze_single_element_damages_log() {
        // Given
        String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
        String damagesLogLine = "INFO 13:14:04,548 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -1 713 PV (Eau)";

        Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0);
        when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

        // When
        analyzer.analyze(spellLogLine);
        analyzer.analyze(damagesLogLine);

        // Then
        verify(updateCharacter).update(eq(mockCharacter), damagesCaptor.capture());

        Damages capturedDamages = damagesCaptor.getValue();
        assertEquals(1713, capturedDamages.amount());

        Set<String> expectedElements = new LinkedHashSet<>();
        expectedElements.add("eau");
        assertEquals(expectedElements, capturedDamages.elements());
        assertEquals(LocalTime.parse("13:14:04").plus(548, MILLIS), capturedDamages.timestamp());
    }

    @Test
    void should_analyze_multi_element_damages_log() {
        // Given
        String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
        String statusLogLine = "INFO 13:14:14,000 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: Toxines (Niv.2)";
        String damagesLogLine = "INFO 13:14:07,232 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -550 PV (Eau) (Feu) (Toxines)";

        CharacterName toxinesCaster = new CharacterName("Jeanne Jackeline Qwartz");
        Character mockToxinesCaster = new Character(toxinesCaster, 0);

        when(fetchCharacter.character(eq(toxinesCaster))).thenReturn(mockToxinesCaster);

        // When
        analyzer.analyze(spellLogLine);
        analyzer.analyze(statusLogLine);
        analyzer.analyze(damagesLogLine);

        // Then
        verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockToxinesCaster.name()));
        verify(updateCharacter).update(eq(mockToxinesCaster), damagesCaptor.capture());

        Damages capturedDamages = damagesCaptor.getValue();
        assertEquals(550, capturedDamages.amount());

        Set<String> expectedElements = new LinkedHashSet<>();
        expectedElements.add("eau");
        expectedElements.add("feu");
        expectedElements.add("toxines");
        assertEquals(expectedElements, capturedDamages.elements());
    }

    @Test
    void should_handle_damages_without_spell_caster() {
        // Given
        String damagesLogLine = "INFO 13:14:04,548 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -1 713 PV (Eau)";

        Character unknownCharacter = new Character(new CharacterName("Unknown"), 0);
        when(fetchCharacter.character(eq(new CharacterName("Unknown")))).thenReturn(unknownCharacter);

        // When
        analyzer.analyze(damagesLogLine);

        // Then
        verify(updateCharacter).update(eq(unknownCharacter), any(Damages.class));
    }

    @Test
    void should_ignore_irrelevant_log_lines() {
        // Given
        String irrelevantLog = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Ceci est un message qui ne correspond à aucun pattern";

        // When
        analyzer.analyze(irrelevantLog);

        // Then
        verifyNoInteractions(updateCharacter, updateStatusEffect, fetchStatusEffect);
    }
}
