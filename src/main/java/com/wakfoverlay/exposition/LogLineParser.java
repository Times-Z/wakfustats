package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;

import java.text.Normalizer;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.*;

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
        Matcher spellCastMatcher = regexProvider.spellCastPattern().matcher(logLine);
        Matcher statusEffectMatcher = regexProvider.statusEffectPattern().matcher(logLine);
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(logLine);

        if (spellCastMatcher.find()) {
            handleSpellCasting(spellCastMatcher);
        }

        if (statusEffectMatcher.find()) {
            handleStatusEffect(statusEffectMatcher);
        }

        if (damagesMatcher.find()) {
            handleDamages(damagesMatcher);
        }
    }

    private void handleSpellCasting(Matcher spellCastMatcher) {
        String casterName = spellCastMatcher.group(2);

        if (casterName == null || casterName.isEmpty()) {
            casterName = "Unknown";
        }

        lastSpellCaster = fetchCharacter.character(new Character.CharacterName(casterName));
    }

    private void handleStatusEffect(Matcher statusEffectMatcher) {
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
            effect = new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, TETATOXINE);
        } else if (normalize(statusName).equals(normalize("Intoxiqué"))) {
            effect = new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, INTOXIQUE);
        } else {
            effect = new StatusEffect(timestamp, targetName, new StatusEffectName(normalize(statusName)), level, NO_SUB_TYPE);
        }

        updateStatusEffect.update(effect, lastSpellCaster.name());
    }

    private void handleDamages(Matcher damagesMatcher) {
        LocalTime timestamp = LocalTime.parse(damagesMatcher.group(1), regexProvider.timeFormatterPattern());
        int damages = Integer.parseInt(damagesMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = damagesMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> damagesElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            damagesElements.add(normalize(elementMatcher.group(1).trim()));
        }

        String lastElement = damagesElements.toArray()[damagesElements.size() - 1].toString();
        Character.CharacterName casterName = switch (normalize(lastElement)) {
            case "tetatoxine", "intoxique" -> fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
            default -> lastSpellCaster.name();
        };

        lastSpellCaster = fetchCharacter.character(casterName);

        System.out.println("Last spell caster: " + lastSpellCaster + " with damages: " + damages);
        updateCharacter.update(lastSpellCaster, damages);
    }

    public static String normalize(String text) {
        if (text == null) return null;
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }
}

