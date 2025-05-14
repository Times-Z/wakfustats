package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineParser {
    private final UpdatePlayerDamages updatePlayerDamages;
    private final RegexProvider regexProvider;

    public LogLineParser(UpdatePlayerDamages updatePlayerDamages) {
        this.updatePlayerDamages = updatePlayerDamages;
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

        parseSpellCast(line);
        parseDamages(line);
    }

    private void parseSpellCast(String line) {
        Matcher castSpellMatcher = regexProvider.castSpellPattern().matcher(line);

        if (castSpellMatcher.matches()) {
            String character = castSpellMatcher.group(1);
            if (character == null || character.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne de sort: " + line);
            }

            String spellName = castSpellMatcher.group(2);
            if (spellName == null || spellName.trim().isEmpty()) {
                System.out.println("Nom de sort non identifié dans la ligne de sort: " + line);
            }

            // TODO: Ajouter une action à effectuer pour les sorts

        }
    }

    private void parseDamages(String line) {
        Matcher damagesMatcher = regexProvider.damagesPattern().matcher(line);

        if (damagesMatcher.matches()) {
            String character = damagesMatcher.group(1);
            if (character == null || character.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne de dégâts: " + line);
            }

            String damagesStr = damagesMatcher.group(2);
            if (damagesStr == null || damagesStr.trim().isEmpty()) {
                System.out.println("Valeur de dégâts non identifiée dans la ligne de dégâts: " + line);
            }

            String element = damagesMatcher.group(3);
            if (element == null || element.trim().isEmpty()) {
                System.out.println("Élément non identifié dans la ligne de dégâts: " + line);
            }

            // TODO: Ajouter une action à effectuer pour les sorts
        }
    }
}
