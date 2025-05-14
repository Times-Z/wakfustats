package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.player.FetchPlayerUseCase;
import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;

import java.util.Objects;
import java.util.regex.Matcher;

public class LogLineParser {
    private final FetchPlayerUseCase fetchPlayer;
    private final UpdatePlayerDamages updatePlayer;
    private final RegexProvider regexProvider;

    private Player lastSpellCaster = null;

    public LogLineParser(FetchPlayerUseCase fetchPlayer, UpdatePlayerDamages updatePlayer) {
        this.fetchPlayer = fetchPlayer;
        this.updatePlayer = updatePlayer;
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

        boolean isSpellCast = parseSpellCast(line);

        if (!isSpellCast) {
            parseDamages(line);
        }
    }

    private boolean parseSpellCast(String line) {
        Matcher castSpellMatcher = regexProvider.castSpellPattern().matcher(line);

        if (castSpellMatcher.matches()) {
            String characterName = castSpellMatcher.group(1);
            if (characterName == null || characterName.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne de sort: " + line);
                return true;
            }

            String spellName = castSpellMatcher.group(2);
            if (spellName == null || spellName.trim().isEmpty()) {
                System.out.println("Nom de sort non identifié dans la ligne de sort: " + line);
                return true;
            }

            System.out.println("Sort lancé par " + characterName + ": " + spellName);

            lastSpellCaster = fetchPlayer.player(characterName);

            return true;
        }

        return false;
    }

    private void parseDamages(String line) {
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(line);

        if (damagesMatcher.matches()) {
            String damages = damagesMatcher.group(2).replaceAll("\\s+", "");;
            if (damages.trim().isEmpty()) {
                System.out.println("Valeur de dégâts non identifiée dans la ligne de dégâts: " + line);
                return;
            }

            int damageValue;
            try {
                damageValue = Math.abs(Integer.parseInt(damages));
            } catch (NumberFormatException e) {
                System.out.println("Format de dégâts invalide: " + damages);
                return;
            }

            updatePlayer.update(
                    new Player(lastSpellCaster.name(), lastSpellCaster.damages()),
                    damageValue
            );
        }
    }
}
