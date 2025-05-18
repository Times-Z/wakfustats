package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.*;
import static com.wakfoverlay.domain.logs.TheNormalizer.normalize;

public class TheAnalyzer {
    private final FetchCharacterUseCase fetchCharacter;
    private final FetchStatusEffect fetchStatusEffect;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final RegexProvider regexProvider;

    private Character lastSpellCaster = null;

    public TheAnalyzer(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect) {
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
        String statusName = statusEffectMatcher.group(3);
        String statusLevel = statusEffectMatcher.group(4);

        Matcher levelMatcher = Pattern.compile("(\\d+)").matcher(statusLevel);
        Integer level = null;
        if (levelMatcher.find()) {
            level = Integer.parseInt(levelMatcher.group(1));
        }

        StatusEffect effect;
        switch (normalize(statusName)) {
            case "toxines" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, TETATOXINE);
            case "intoxique" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, INTOXIQUE);
            case "maudit" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, MAUDIT);
            case "distorsion" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, DISTORSION);
            default ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, NO_SUB_TYPE);
        }

        if (lastSpellCaster == null) {
            lastSpellCaster = new Character(new Character.CharacterName("Unknown"), 0);
        }

        updateStatusEffect.update(effect, lastSpellCaster.name());
    }

    private void handleDamages(Matcher damagesMatcher) {
        LocalTime timestamp = LocalTime.parse(damagesMatcher.group(1), regexProvider.timeFormatterPattern());
        int damageAmount = Integer.parseInt(damagesMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = damagesMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> damagesElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            damagesElements.add(normalize(elementMatcher.group(1).trim()));
        }

        Damages damages = new Damages(timestamp, damageAmount, damagesElements);

        String lastElement = damagesElements.toArray()[damagesElements.size() - 1].toString();
        Character.CharacterName casterName = switch (normalize(lastElement)) {
            case "tetatoxine",
                 "intoxique",
                 "maudit",
                 "distorsion" -> fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
            default -> {
                if (lastSpellCaster == null) {
                    lastSpellCaster = new Character(new Character.CharacterName("Unknown"), 0);
                }

                yield lastSpellCaster.name();
            }
        };

        lastSpellCaster = fetchCharacter.character(casterName);

        updateCharacter.update(lastSpellCaster, damages);
    }
}

