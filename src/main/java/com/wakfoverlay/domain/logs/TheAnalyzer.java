package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.model.*;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.TargetedDamagesRepository;

import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wakfoverlay.domain.fight.model.StatusEffect.StatusEffectName;
import static com.wakfoverlay.domain.logs.TheNormalizer.normalize;
import static java.util.Optional.empty;

public class TheAnalyzer {
    private final FetchCharacter fetchCharacter;
    private final FetchStatusEffect fetchStatusEffect;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final RegexProvider regexProvider;
    private final TargetedDamagesRepository targetedDamagesRepository;

    private int account = 0;
    private Character lastSpellCaster = null;
    private Optional<CharacterName> lastSummoner = empty();
    private final List<String> summonIds = new ArrayList<>();
    private Optional<CharacterName> firstFighter = empty();
    private boolean waitingForFirstFighter = false;

    public TheAnalyzer(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect, TargetedDamagesRepository targetedDamagesRepository) {
        this.fetchCharacter = fetchCharacter;
        this.fetchStatusEffect = fetchStatusEffect;
        this.updateCharacter = updateCharacter;
        this.updateStatusEffect = updateStatusEffect;
        this.targetedDamagesRepository = targetedDamagesRepository;
        this.regexProvider = new RegexProvider();
    }

    public void analyze(String logLine) {
        Matcher fightCreationMatcher = regexProvider.fightCreationPattern().matcher(logLine);
        Matcher fightEndMatcher = regexProvider.fightEndPattern().matcher(logLine);
        Matcher connectionLostMatcher = regexProvider.connectionLostPattern().matcher(logLine);
        Matcher fighterMatcher = regexProvider.fighterPattern().matcher(logLine);
        Matcher spellCastMatcher = regexProvider.spellCastPattern().matcher(logLine);
        Matcher statusEffectMatcher = regexProvider.statusEffectPattern().matcher(logLine);
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(logLine);
        Matcher healsMatcher = regexProvider.healsPattern().matcher(logLine);
        Matcher shieldsMatcher = regexProvider.shieldsPattern().matcher(logLine);
        Matcher summonerMatcher = regexProvider.summonerPattern().matcher(logLine);
        Matcher summoningMatcher1 = regexProvider.summoningPattern1().matcher(logLine);
        Matcher summoningMatcher2 = regexProvider.summoningPattern2().matcher(logLine);

        if (fightCreationMatcher.find()) {
            handleAccounting(true);
            handleFightCreation();
            targetedDamagesRepository.resetTargetedDamages();
        }

        if (fightEndMatcher.find()) {
            handleAccounting(false);
        }

        if (connectionLostMatcher.find()) {
            handleAccounting(false);
        }

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
            handleSummoning(summoningMatcher1);
        }

        if (summoningMatcher2.find()) {
            handleSummoning(summoningMatcher2);
        }
    }

    public void analyzeFighter(String logLine) {
        Matcher fightCreationMatcher = regexProvider.fightCreationPattern().matcher(logLine);
        Matcher fightEndMatcher = regexProvider.fightEndPattern().matcher(logLine);
        Matcher connectionLostMatcher = regexProvider.connectionLostPattern().matcher(logLine);
        Matcher fighterMatcher = regexProvider.fighterPattern().matcher(logLine);

        if (fightCreationMatcher.find()) {
            handleAccounting(true);
        }

        if (fightEndMatcher.find()) {
            handleAccounting(false);
        }

        if (connectionLostMatcher.find()) {
            handleAccounting(false);
        }

        if (fighterMatcher.find()) {
            handleFighter(fighterMatcher);
        }
    }

    public void resetAccounting() {
        account = 0;
    }

    private void handleFightCreation() {
        firstFighter = empty();
        waitingForFirstFighter = true;
    }

    private void handleAccounting(boolean increasing) {
        if (increasing) {
            account++;
        } else {
            account--;

            if (account < 0) {
                account = 0;
                firstFighter = empty();
                waitingForFirstFighter = false;
            }
        }
    }

    private void handleFighter(Matcher fighterMatcher) {
        CharacterName characterName = new CharacterName(fighterMatcher.group(3));
        boolean isControlledByAI = Boolean.parseBoolean(fighterMatcher.group(5));

        if (waitingForFirstFighter) {
            firstFighter = Optional.of(characterName);
            waitingForFirstFighter = false;
        }

        if (!fetchCharacter.exist(characterName) && !isControlledByAI) {
            Character character = new Character(characterName, 0, 0, 0, empty());
            updateCharacter.create(character);
        }

        if (!fetchCharacter.exist(characterName) && isControlledByAI && lastSummoner.isPresent()) {
            Character summoner = fetchCharacter.character(lastSummoner.get());
            Character summon = new Character(characterName, 0, 0, 0, Optional.of(summoner));

            updateCharacter.create(summon);
        }

        lastSummoner = empty();
    }

    private void handleSpellCasting(Matcher spellCastMatcher) {
        CharacterName casterName = new CharacterName(spellCastMatcher.group(2));

        if (fetchCharacter.exist(casterName)) {
            lastSpellCaster = fetchCharacter.character(casterName);

            if (lastSpellCaster.summoner().isPresent()) {
                lastSpellCaster = fetchCharacter.character(lastSpellCaster.summoner().get().name());
            }
        }
    }

    private void handleStatusEffect(Matcher statusEffectMatcher) {
        LocalTime timestamp = LocalTime.parse(statusEffectMatcher.group(1), regexProvider.timeFormatterPattern());
        String statusName = statusEffectMatcher.group(3);

        StatusEffect effect;
        switch (normalize(statusName)) {
            case "garde feuille" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), "priere sadida");
            case "toxines" ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), "tetatoxine");
            default ->
                    effect = new StatusEffect(timestamp, new StatusEffectName(normalize(statusName)), normalize(statusName));
        }

        if (lastSpellCaster == null) {
            lastSpellCaster = new Character(new Character.CharacterName("Unknown"), 0, 0, 0, empty());
        }

        updateStatusEffect.update(effect, lastSpellCaster.name());
    }

    private void handleDamages(Matcher damagesMatcher) {
        LocalTime timestamp = LocalTime.parse(damagesMatcher.group(1), regexProvider.timeFormatterPattern());

        String targetName = damagesMatcher.group(2);
        int damageAmount = Integer.parseInt(damagesMatcher.group(3).replaceAll("[^\\d-]+", ""));

        if (friendlyFire(targetName)) {
            return;
        }

        String elements = damagesMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> damagesElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            damagesElements.add(normalize(elementMatcher.group(1).trim()));
        }

        Damages damages = new Damages(timestamp, normalize(targetName), damageAmount, damagesElements);

        String lastElement = normalize(damagesElements.toArray()[damagesElements.size() - 1].toString());
        CharacterName casterName = new CharacterName("Unknown");
        if (!Objects.equals(lastElement, "lumiere") &&
                !Objects.equals(lastElement, "stasis") &&
                !Objects.equals(lastElement, "feu") &&
                !Objects.equals(lastElement, "air") &&
                !Objects.equals(lastElement, "eau") &&
                !Objects.equals(lastElement, "terre")
        ) {
            casterName = fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
        }

        if (fetchCharacter.exist(casterName) && !casterName.value().equals("Unknown")) {
            Character casterToAttribute = fetchCharacter.character(casterName);

            if (casterToAttribute.summoner().isPresent()) {
                casterToAttribute = fetchCharacter.character(casterToAttribute.summoner().get().name());
            }

            updateCharacter.updateDamages(casterToAttribute, damages, multiAccounting(), account);
            lastSpellCaster = casterToAttribute;
        } else {
            lastSpellCaster = fetchCharacter.character(lastSpellCaster.name());
            updateCharacter.updateDamages(lastSpellCaster, damages, multiAccounting(), account);
        }

        if (firstFighter.isPresent() && firstFighter.get().value().equals(targetName)) {
            if (!multiAccounting()) {
                targetedDamagesRepository.addDamages(lastSpellCaster, damages);
            }

            if (multiAccounting()) {
                Damages newDamages = new Damages(damages.timestamp(), damages.targetName(), Math.ceilDivExact(damages.amount(), account), damages.elements());
                targetedDamagesRepository.addDamages(lastSpellCaster, newDamages);
            }
        }
    }

    private void handleHeals(Matcher healsMatcher) {
        LocalTime timestamp = LocalTime.parse(healsMatcher.group(1), regexProvider.timeFormatterPattern());
        String targetName = healsMatcher.group(2);

        int healsAmount = Integer.parseInt(healsMatcher.group(3).replaceAll("[^\\d-]+", ""));

        String elements = healsMatcher.group(4);
        Matcher elementMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(elements);
        Set<String> healsElements = new LinkedHashSet<>();
        while (elementMatcher.find()) {
            healsElements.add(normalize(elementMatcher.group(1).trim()));
        }

        Heals heals;
        heals = new Heals(timestamp, normalize(targetName), healsAmount, healsElements);

        String lastElement = healsElements.toArray()[healsElements.size() - 1].toString();
        CharacterName casterName = switch (normalize(lastElement)) {
            case "priere sadida" -> fetchStatusEffect.characterFor(new StatusEffectName(lastElement));
            case "engraine" ->  {
                heals = new Heals(timestamp, normalize(targetName), 0, healsElements);
                yield null;
            }
            default -> {
                if (lastSpellCaster == null) {
                    lastSpellCaster = new Character(new CharacterName("Unknown"), 0, 0, 0, empty());
                }

                yield lastSpellCaster.name();
            }
        };

        if (fetchCharacter.exist(casterName)) {
            Character casterToAttribute = fetchCharacter.character(casterName);

            if (casterToAttribute.summoner().isPresent()) {
                casterToAttribute = fetchCharacter.character(casterToAttribute.summoner().get().name());
            }

            updateCharacter.updateHeals(casterToAttribute, heals, multiAccounting(), account);
        } else {
            lastSpellCaster = fetchCharacter.character(lastSpellCaster.name());
            updateCharacter.updateHeals(lastSpellCaster, heals, multiAccounting(), account);
        }
    }

    private void handleShields(Matcher shieldsMatcher) {
        LocalTime timestamp = LocalTime.parse(shieldsMatcher.group(1), regexProvider.timeFormatterPattern());
        String targetName = shieldsMatcher.group(2);

        int shieldsAmount = Integer.parseInt(shieldsMatcher.group(3).replaceAll("[^\\d-]+", ""));

        Shields shields = new Shields(timestamp, normalize(targetName), shieldsAmount);

        if (lastSpellCaster.summoner().isPresent()) {
            lastSpellCaster = fetchCharacter.character(lastSpellCaster.summoner().get().name());
        }

        lastSpellCaster = fetchCharacter.character(lastSpellCaster.name());
        updateCharacter.updateShields(lastSpellCaster, shields, multiAccounting(), account);
    }

    private void handleSummoner(Matcher summonMatcher) {
        CharacterName summonerName = new CharacterName(summonMatcher.group(2));
        lastSummoner = empty();

        if (fetchCharacter.exist(summonerName)) {
            lastSummoner = Optional.ofNullable(fetchCharacter.character(summonerName).name());
        }
    }

    private void handleSummoning(Matcher summoningMatcher1) {
        String summonId = summoningMatcher1.group(2);

        summonIds.add(summonId);
    }

    private boolean multiAccounting() {
        return account > 1;
    }

    private boolean friendlyFire(String targetName) {
        return fetchCharacter.isAllied(new CharacterName(targetName));
    }
}
