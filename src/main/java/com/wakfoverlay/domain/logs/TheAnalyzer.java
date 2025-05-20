package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.*;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import static com.wakfoverlay.domain.fight.model.StatusEffect.SubType.*;
import static com.wakfoverlay.domain.logs.TheNormalizer.normalize;

public class TheAnalyzer {
    private final FetchCharacter fetchCharacter;
    private final FetchStatusEffect fetchStatusEffect;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final RegexProvider regexProvider;

    private Character lastSpellCaster = null;
    private Character lastSummoner = null;
    private List<String> summonIds = List.of();

    public TheAnalyzer(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect) {
        this.fetchCharacter = fetchCharacter;
        this.fetchStatusEffect = fetchStatusEffect;
        this.updateCharacter = updateCharacter;
        this.updateStatusEffect = updateStatusEffect;
        this.regexProvider = new RegexProvider();
    }

    public void analyze(String logLine) {
        Matcher fighterMatcher = regexProvider.fighterPattern().matcher(logLine);
        Matcher spellCastMatcher = regexProvider.spellCastPattern().matcher(logLine);
        Matcher statusEffectMatcher = regexProvider.statusEffectPattern().matcher(logLine);
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(logLine);
        Matcher healsMatcher = regexProvider.healsPattern().matcher(logLine);
        Matcher shieldsMatcher = regexProvider.shieldsPattern().matcher(logLine);
        Matcher summonerMatcher = regexProvider.summonerPattern().matcher(logLine);
        Matcher summoningMatcher1 = regexProvider.summoningPattern1().matcher(logLine);
        Matcher summoningMatcher2 = regexProvider.summoningPattern2().matcher(logLine);
        Matcher summonMatcher = regexProvider.summonPattern().matcher(logLine);

        if (fighterMatcher.find()) {
            handleFighter(fighterMatcher);
        }

        if (spellCastMatcher.find()) {
            handleSpellCasting(spellCastMatcher);
        }

        if (statusEffectMatcher.find()) {
            handleStatusEffect(statusEffectMatcher);
        }

        if (damagesMatcher.find()) {
            handleDamages(damagesMatcher);
        }

        if (healsMatcher.find()) {
            handleHeals(healsMatcher);
        }

        if (shieldsMatcher.find()) {
            handleShields(shieldsMatcher);
        }

        if (summonerMatcher.find()) {
            handleSummoner(summonerMatcher);
        }

        if (summoningMatcher1.find()) {
            System.out.println("Summon ID from matcher1: " + summoningMatcher1.group(2));
        }

        if (summoningMatcher2.find()) {
            System.out.println("Summon ID from matcher2: " + summoningMatcher2.group(2));
        }

        if (summonMatcher.find()) {
            String summonName = summonMatcher.group(2);
            String summonId = summonMatcher.group(3);

            Optional<String> optionalSummon = summonIds.stream()
                    .filter(it -> it.equals(summonId))
                    .findFirst();

            summonIds.remove(summonId);

            if (optionalSummon.isPresent()) {
                Character summon = new Character(
                        new CharacterName(summonName),
                        0,
                        0,
                        0,
                        true,
                        Optional.of(lastSummoner));

                updateCharacter.create(summon);
            }
        }
        // modifier les damages pour check les invocations et récupérer les dégats
        // assigner les dégats au summoner
    }

    public void analyzeFighter(String logLine) {
        Matcher fighterMatcher = regexProvider.fighterPattern().matcher(logLine);

        if (fighterMatcher.find()) {
            handleFighter(fighterMatcher);
        }
    }

    private void handleFighter(Matcher fighterMatcher) {
        String fighterName = fighterMatcher.group(3);
        boolean isControlledByAI = Boolean.parseBoolean(fighterMatcher.group(5));

        CharacterName name = new CharacterName(fighterName);

        Character character = new Character(name, 0, 0, 0, isControlledByAI);
        updateCharacter.create(character);
    }

    private void handleSpellCasting(Matcher spellCastMatcher) {
        String casterName = spellCastMatcher.group(2);

        if (casterName == null || casterName.isEmpty()) {
            casterName = "Unknown";
        }

        lastSpellCaster = fetchCharacter.character(new CharacterName(casterName));
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
            case "garde feuille" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, PRIERE_SADIDA);
            default ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), level, NO_SUB_TYPE);
        }

        if (lastSpellCaster == null) {
            lastSpellCaster = new Character(new Character.CharacterName("Unknown"), 0, 0, 0, false); // TODO: isControlloed true ?
        }

        updateStatusEffect.update(effect, lastSpellCaster.name());
    }

    private void handleDamages(Matcher damagesMatcher) {
        LocalTime timestamp = LocalTime.parse(damagesMatcher.group(1), regexProvider.timeFormatterPattern());

        String targetName = damagesMatcher.group(2);
        if (friendlyFire(targetName)) {
            return;
        }

        int damageAmount = Integer.parseInt(damagesMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = damagesMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> damagesElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            damagesElements.add(normalize(elementMatcher.group(1).trim()));
        }

        Damages damages = new Damages(timestamp, damageAmount, damagesElements);

        String lastElement = damagesElements.toArray()[damagesElements.size() - 1].toString();
        CharacterName casterName = switch (normalize(lastElement)) {
            case "tetatoxine",
                 "intoxique",
                 "maudit",
                 "distorsion",
                 "garde feuille" -> fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
            default -> {
                if (lastSpellCaster == null) {
                    lastSpellCaster = new Character(new CharacterName("Unknown"), 0, 0, 0, false);
                }

                yield lastSpellCaster.name();
            }
        };

        lastSpellCaster = fetchCharacter.character(casterName);

        updateCharacter.updateDamages(lastSpellCaster, damages);
    }

    private void handleHeals(Matcher healsMatcher) {
        LocalTime timestamp = LocalTime.parse(healsMatcher.group(1), regexProvider.timeFormatterPattern());
        int healsAmount = Integer.parseInt(healsMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = healsMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> healsElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            healsElements.add(normalize(elementMatcher.group(1).trim()));
        }

        Heals heals = new Heals(timestamp, healsAmount, healsElements);

        String lastElement = healsElements.toArray()[healsElements.size() - 1].toString();
        CharacterName casterName = switch (normalize(lastElement)) {
            case "priere sadida" -> fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
            case "engraine" -> null;
            default -> {
                if (lastSpellCaster == null) {
                    lastSpellCaster = new Character(new CharacterName("Unknown"), 0, 0, 0, false);
                }

                yield lastSpellCaster.name();
            }
        };

        if (casterName != null) {
            lastSpellCaster = fetchCharacter.character(casterName);
            updateCharacter.updateHeals(lastSpellCaster, heals);
        }
    }

    private void handleShields(Matcher shieldsMatcher) {
        LocalTime timestamp = LocalTime.parse(shieldsMatcher.group(1), regexProvider.timeFormatterPattern());
        int shieldsAmount = Integer.parseInt(shieldsMatcher.group(3).replaceAll("[^\\d-]+", ""));

        Shields shields = new Shields(timestamp, shieldsAmount);

        updateCharacter.updateShields(lastSpellCaster, shields);
    }

    private void handleSummoner(Matcher summonMatcher) {
        CharacterName summonerName = new CharacterName(summonMatcher.group(2));
        Character character = fetchCharacter.character(summonerName);

        if (!character.isControlledByAI()) {
            lastSummoner = character;
        }
    }

    private boolean friendlyFire(String targetName) {
        return fetchCharacter.isAllied(new CharacterName(targetName));
    }
}
