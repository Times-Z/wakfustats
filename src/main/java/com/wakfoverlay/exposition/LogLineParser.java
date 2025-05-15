package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;

import java.text.Normalizer;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.INTOXIQUE;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.NO_SUB_TYPE;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.TETRATOXINE;

public class LogLineParser {
    private final FetchCharacterUseCase fetchCharacter;
    private final FetchStatusEffect fetchStatusEffect;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final RegexProvider regexProvider;

    private Character lastSpellCaster = null;

    public LogLineParser(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect) {
        this.fetchCharacter = fetchCharacter;
        this.fetchStatusEffect = fetchStatusEffect;
        this.updateCharacter = updateCharacter;
        this.updateStatusEffect = updateStatusEffect;
        this.regexProvider = new RegexProvider();
    }

    public void analyze(String logLine) {
        List<Object> result = new ArrayList<>();
        Matcher spellCastMatcher = regexProvider.spellCastPattern().matcher(logLine);
        Matcher statusEffectMatcher = regexProvider.statusEffectPattern().matcher(logLine);
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(logLine);

        if (spellCastMatcher.matches()) {
            handleSpellCasting(spellCastMatcher, result);
        }

        if (statusEffectMatcher.matches()) {
            handleStatusEffect(statusEffectMatcher, result);
        }

        if (damagesMatcher.matches()) {
            handleDamages(damagesMatcher, result);
        }

    }

    private void handleSpellCasting(Matcher spellCastMatcher, List<Object> result) {
        String casterName = spellCastMatcher.group(2);

        if (casterName == null || casterName.isEmpty()) {
            casterName = "Unknown";
        }

        lastSpellCaster = fetchCharacter.character(new Character.CharacterName(casterName));

        // TODO: remove after tests
        // LocalTime timestamp = LocalTime.parse(spellCastMatcher.group(1), regexProvider.timeFormatterPattern());
        // String spellName = spellCastMatcher.group(3);
        // String additionalInfo = spellCastMatcher.group(4);
        // result.add(new SpellCast(timestamp, casterName, spellName, additionalInfo));
    }

    private void handleStatusEffect(Matcher statusEffectMatcher, List<Object> result) {
        LocalTime timestamp = LocalTime.parse(statusEffectMatcher.group(1), regexProvider.timeFormatterPattern());
        String targetName = statusEffectMatcher.group(2);
        String statusName = statusEffectMatcher.group(3);
        String statusLevel = statusEffectMatcher.group(4);

        Matcher levelMatcher = Pattern.compile("(\\d+)").matcher(statusLevel);
        Integer level = null;
        if (levelMatcher.find()) {
            level = Integer.parseInt(levelMatcher.group(1));
        }

        // TODO: Put this in a method
        StatusEffect effect;
        if (normalize(statusName).equals(normalize("Toxines"))) {
            effect = new StatusEffect(
                    timestamp,
                    targetName,
                    new StatusEffectName(normalize(statusName)),
                    level,
                    TETRATOXINE
            );
            //result.add(new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, TETRATOXINE));

        } else if (normalize(statusName).equals(normalize("Intoxiqué"))) {
            effect = new StatusEffect(
                    timestamp,
                    targetName,
                    new StatusEffectName(normalize(statusName)),
                    level,
                    INTOXIQUE
            );
            //result.add(new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, INTOXIQUE));

        } else {
            effect = new StatusEffect(
                    timestamp,
                    targetName,
                    new StatusEffectName(normalize(statusName)),
                    level,
                    NO_SUB_TYPE
            );
            //result.add(new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, NO_SUB_TYPE));
        }

        updateStatusEffect.update(effect, lastSpellCaster.name());
    }

    private void handleDamages(Matcher damagesMatcher, List<Object> result) {
        LocalTime timestamp = LocalTime.parse(damagesMatcher.group(1), regexProvider.timeFormatterPattern());
        int damages = Integer.parseInt(damagesMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = damagesMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> damagesElements = new LinkedHashSet<>();

        while (elementMatcher.find()) {
            damagesElements.add(normalize(elementMatcher.group(1).trim()));
        }

        // TODO: change this
        String lastElement = damagesElements.toArray()[damagesElements.size() - 1].toString();
        Optional<Character.CharacterName> casterName = fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
        System.out.println("Personnage identifie pour: " + casterName.orElse(null));

        Character characterToApplyDamages;
        if (casterName.isPresent()) {
            // TODO: could be null so...
            characterToApplyDamages = fetchCharacter.character(casterName.get());
            updateCharacter.update(characterToApplyDamages, damages);
        }

        //result.add(new Damages(timestamp, damages, damagesElements));
    }

    private String normalize(String text) {
        if (text == null) return null;
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

//    public static void main(String[] args) {
//        RegexProvider regexProvider = new RegexProvider();
//        LogLineParserBis parser = new LogLineParserBis(regexProvider);
//        String logLine1 = "INFO 13:40:17,320 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jean Jack Kinte lance le sort Ecume (Critiques)";
//        String logLine11 = "INFO 13:40:17,320 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Jean Jack Kinte lance le sort Ecume";
//        String logLine2 = "INFO 22:41:19,419 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: Intoxiqué (Niv.19)";
//        String logLine21 = "INFO 22:41:19,419 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: Maudit (+49 Niv.)";
//        String logLine3 = "INFO 22:45:55,208 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -221 PV (Lumière) (Feu) (Tétratoxine)";
//        String logLine31 = "INFO 22:45:55,208 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -1 221 PV (Lumière) (Tétratoxine)";
//        String logLine32 = "INFO 22:45:55,208 [AWT-EventQueue-0] (aSn:174) - [Information (jeu)] Sac à patates: -1 231 221 PV (Tétratoxine)";
//
//        System.out.println(parser.analyze(logLine1));
//        System.out.println(parser.analyze(logLine11));
//        System.out.println(parser.analyze(logLine2));
//        System.out.println(parser.analyze(logLine21));
//        System.out.println(parser.analyze(logLine3));
//        System.out.println(parser.analyze(logLine31));
//        System.out.println(parser.analyze(logLine32));
//    }
}

