package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchPlayer;
import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.StatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdatePlayer;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static com.wakfoverlay.domain.fight.model.StatusEffect.*;

// TODO: Refactor to apply more responsibility segregation
public class LogLineParser {
    private final FetchPlayer fetchPlayer;
    private final UpdatePlayer updatePlayer;
    private final UpdateStatusEffect updateStatusEffect;
    private final RegexProvider regexProvider;

    private Character lastSpellCaster = null;

    private final Map<String, Instant> lastProcessedLines = new HashMap<>();
    private static final long DUPLICATE_THRESHOLD_MS = 1500;

    public LogLineParser(FetchPlayer fetchPlayer, UpdatePlayer updatePlayer, UpdateStatusEffect updateStatusEffect) {
        this.fetchPlayer = fetchPlayer;
        this.updatePlayer = updatePlayer;
        this.updateStatusEffect = updateStatusEffect;
        this.regexProvider = new RegexProvider();
    }

    public void parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            System.out.println("Ligne vide ignorée");
            return;
        }

        if (!line.contains("[Information") || !line.contains("(jeu)]")) {
            System.out.println("Ligne non pertinente ignorée");
            return;
        }

        if (isDuplicate(line)) {
            System.out.println("Ligne dupliquée ignorée: " + line);
            return;
        }

        // TODO: refacto this
        parseSpellCast(line);
        parseStatusEffect(line);
        parseDamages(line);
    }

    // TODO: not working
    private boolean isDuplicate(String line) {
        Instant now = Instant.now();
        if (lastProcessedLines.containsKey(line)) {
            Instant lastTime = lastProcessedLines.get(line);
            if (now.toEpochMilli() - lastTime.toEpochMilli() < DUPLICATE_THRESHOLD_MS) {
                return true;
            }
        }

        lastProcessedLines.put(line, now);
        cleanupOldEntries(now);
        return false;
    }

    private void cleanupOldEntries(Instant now) {
        lastProcessedLines.entrySet().removeIf(entry ->
                now.toEpochMilli() - entry.getValue().toEpochMilli() > 10000);
    }

    private void parseSpellCast(String line) {
        Matcher castSpellMatcher = regexProvider.castSpellPattern().matcher(line);

        if (castSpellMatcher.matches()) {
            String characterName = castSpellMatcher.group(1);
            if (characterName == null || characterName.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne de sort: " + line);
                characterName = "Unknown";
            }

            String spellName = castSpellMatcher.group(2);
            if (spellName == null || spellName.trim().isEmpty()) {
                System.out.println("Nom de sort non identifié dans la ligne de sort: " + line);
                spellName = "Unknown";
            }

            System.out.println("Sort lancé par " + characterName + ": " + spellName);

            lastSpellCaster = fetchPlayer.player(new CharacterName(characterName));

        }

    }

    private void parseDamages(String line) {
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(line);
        int damageValue;

        if (damagesMatcher.matches()) {
            String damages = damagesMatcher.group(2).replaceAll("\\s+", "");
            if (damages.trim().isEmpty()) {
                System.out.println("Valeur de dégâts non identifiée dans la ligne de dégâts: " + line);
            }

            try {
                damageValue = Math.abs(Integer.parseInt(damages));
            } catch (NumberFormatException e) {
                System.out.println("Format de dégâts invalide: " + damages);
                damageValue = 0;
            }

            updatePlayer.update(
                    new Character(lastSpellCaster.name(), lastSpellCaster.damages()),
                    damageValue
            );
        }
    }

    private void parseStatusEffect(String line) {
        Matcher statusEffectMatcher = regexProvider.statusEffectPattern().matcher(line);

        if (statusEffectMatcher.find()) {
            String name = statusEffectMatcher.group(1);
            if (name == null || name.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne d'effet de statut: " + line);
                name = "Unknown";
            }

            String statusEffect = statusEffectMatcher.group(2);
            if (statusEffect == null || statusEffect.trim().isEmpty()) {
                System.out.println("Nom d'effet de statut non identifié dans la ligne d'effet de statut: " + line);
                statusEffect = "Unknown";
            }

            System.out.println("Effet de statut appliqué à " + name + ": " + statusEffect);
            StatusEffect effect = new StatusEffect(statusEffect);

            Character character = fetchPlayer.player(lastSpellCaster.name());
            updateStatusEffect.update(effect, character.name());
            Map<StatusEffect, CharacterName> all = updateStatusEffect.all();
            System.out.println("Status effects: " + all);
        }
    }
}
