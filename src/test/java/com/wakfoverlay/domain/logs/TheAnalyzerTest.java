package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.*;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Optional.empty;
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
    private ArgumentCaptor<Heals> healsCaptor;

    @Captor
    private ArgumentCaptor<Shields> shieldsCaptor;

    @Captor
    private ArgumentCaptor<StatusEffect> statusEffectCaptor;

    @Captor
    private ArgumentCaptor<StatusEffectName> statusEffectNameCaptor;

    private TheAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new TheAnalyzer(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect);
    }

    @Nested
    class FighterTests {
        @Test
        void should_analyze_fighter_log() {
            // Given
            String logLine = " INFO 08:25:31,419 [AWT-EventQueue-0] (eLk:1384) - [_FL_] fightId=1552150922 Jeanne Jackeline Qwartz breed : 10 [10303110] isControlledByAI=empty() obstacleId : -1 join the fight at {Point3 : (-2, -11, 0)}";

            // When
            analyzer.analyze(logLine);

            // Then
            verify(updateCharacter).create(characterCaptor.capture());
        }
    }

    @Nested
    class SpellCastingTests {
        @Test
        void should_analyze_normal_spell_cast_log() {
            // Given
            String logLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
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
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(logLine);

            // Then
            verify(fetchCharacter).character(characterNameCaptor.capture());
            assertEquals("Jeanne Jackeline Qwartz", characterNameCaptor.getValue().value());
        }
    }

    @Nested
    class StatusEffectTests {
        @Test
        void should_analyze_distortion_status_effect_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Kinte lance le sort Bourrasque";
            String statusLogLine = "INFO 10:07:24,886 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Kinte: Distorsion (Niv.8)";
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Kinte"), 0, 0, 0, empty());
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
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Sizt"), 0, 0, 0, empty());
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
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
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
        void should_analyze_garde_feuille_status_effect_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Arbre de sadida";
            String statusLogLine = " INFO 22:41:55,582 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz: Garde feuille (Niv.6)";
            String healsLogLine = " INFO 22:41:24,967 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz: +774 PV (Lumière) (Eau) (Prière Sadida)";
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(statusLogLine);
            analyzer.analyze(healsLogLine);

            // Then
            verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockCharacter.name()));

            StatusEffect capturedEffect = statusEffectCaptor.getValue();
            assertEquals("garde feuille", capturedEffect.name().value());
            assertEquals(PRIERE_SADIDA, capturedEffect.subType());
        }

        @Test
        void should_analyze_engraine_status_effect_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Arbre de sadida";
            String statusLogLine = " INFO 22:41:55,582 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz: Engraine (Niv.6)";
            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(statusLogLine);

            // Then
            verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockCharacter.name()));

            StatusEffect capturedEffect = statusEffectCaptor.getValue();
            assertEquals("engraine", capturedEffect.name().value());
            assertEquals(NO_SUB_TYPE, capturedEffect.subType());
        }
    }

    @Nested
    class DamagesTests {
        @Test
        void should_analyze_single_element_damages_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Bourrasque";
            String damagesLogLine = "INFO 13:14:04,548 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -1 713 PV (Eau)";

            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(damagesLogLine);

            // Then
            verify(updateCharacter).updateDamages(eq(mockCharacter), damagesCaptor.capture());

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
            Character mockToxinesCaster = new Character(toxinesCaster, 0, 0, 0, empty());

            when(fetchCharacter.character(eq(toxinesCaster))).thenReturn(mockToxinesCaster);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(statusLogLine);
            analyzer.analyze(damagesLogLine);

            // Then
            verify(updateStatusEffect).update(statusEffectCaptor.capture(), eq(mockToxinesCaster.name()));
            verify(updateCharacter).updateDamages(eq(mockToxinesCaster), damagesCaptor.capture());

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

            Character unknownCharacter = new Character(new CharacterName("Unknown"), 0, 0, 0, empty());
            when(fetchCharacter.character(eq(new CharacterName("Unknown")))).thenReturn(unknownCharacter);

            // When
            analyzer.analyze(damagesLogLine);

            // Then
            verify(updateCharacter).updateDamages(eq(unknownCharacter), any(Damages.class));
        }
    }

    @Nested
    class HealsTests {
        @Test
        void should_analyze_single_element_healing_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Engrais";
            String healsLogLine = "INFO 21:02:54,410 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] La Gonflable: +1 200 PV (Eau)";

            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(healsLogLine);

            // Then
            verify(updateCharacter).updateHeals(eq(mockCharacter), healsCaptor.capture());

            Heals capturedHeals = healsCaptor.getValue();
            assertEquals(1200, capturedHeals.amount());

            Set<String> expectedElements = new LinkedHashSet<>();
            expectedElements.add("eau");
            assertEquals(expectedElements, capturedHeals.elements());
            assertEquals(LocalTime.parse("21:02:54").plus(410, MILLIS), capturedHeals.timestamp());
        }

        @Test
        void should_ignore_engraine_healing_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Engrais";
            String healsLogLine = "INFO 21:02:54,410 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] La Gonflable: +1 200 PV (Engrainé)";

            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(healsLogLine);

            // Then
            verify(updateCharacter, never()).updateHeals(any(Character.class), any(Heals.class));
        }
    }

    @Nested
    class ShieldTests {
        @Test
        void should_analyze_shielding_log() {
            // Given
            String spellLogLine = "INFO 13:14:13,763 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Qwartz lance le sort Engrais";
            String shieldsLogLine = "INFO 21:02:56,407 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jeanne Jackeline Kinte: 1 324 Armure";

            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(shieldsLogLine);

            // Then
            verify(updateCharacter).updateShields(eq(mockCharacter), shieldsCaptor.capture());

            Shields capturedShields = shieldsCaptor.getValue();
            assertEquals(1324, capturedShields.amount());
        }
    }

    @Nested
    class SummonTests {
        @Test
        void should_analyze_summoner_log() {
            // Given
            String spellLogLine = " INFO 20:33:40,436 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jean Jack Deuz lance le sort Gobgob";
            String summonerLogLine = " INFO 20:33:42,268 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jean Jack Deuz: Invoque un(e) Gobgob";

            Character mockCharacter = new Character(new CharacterName("Jeanne Jackeline Qwartz"), 0, 0, 0, empty());
            when(fetchCharacter.character(any(CharacterName.class))).thenReturn(mockCharacter);

            // When
            analyzer.analyze(spellLogLine);
            analyzer.analyze(summonerLogLine);

            // Then
            // TODO: find assertion
        }

        @Test
        void should_analyze_summoning_logs() {
            // Given
            String summoningLog1 = " INFO 20:52:16,267 [AWT-EventQueue-0] (eIu:106) - Instanciation d'une nouvelle invocation avec un id de -1811995151979217";
            String summoningLog2 = " INFO 20:52:18,100 [AWT-EventQueue-0] (eIA:92) - New summon with id -1811995151979156";

            // When
            analyzer.analyze(summoningLog1);
            analyzer.analyze(summoningLog2);

            // Then
            // TODO: find assertion
        }

        @Test
        void should_analyze_summon_log() {
            // Given
            String summonLogLine = " INFO 20:33:47,602 [AWT-EventQueue-0] (eLk:1384) - [_FL_] fightId=1648132072 Piou Rouge breed : 87 [-1811995151994155] isControlledByAI=true obstacleId : 17 join the fight at {Point3 : (0, -16, 8)}";

            // When
            analyzer.analyze(summonLogLine);

            // Then
            // TODO: find assertion
        }
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
