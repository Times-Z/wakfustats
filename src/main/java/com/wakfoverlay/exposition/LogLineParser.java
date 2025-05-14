package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.player.model.Player;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineParser {
    private static final Pattern CAST_SPELL_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*(.*)\\s+lance\\s+le\\s+sort\\s+(.*)");
    private static final Pattern DAMAGES_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*([^:]+):\\s*([+-]\\d+)\\s+PV\\s+\\((.*)\\)");

    private final UpdatePlayerDamages updatePlayerDamages;

    public LogLineParser(UpdatePlayerDamages updatePlayerDamages) {
        this.updatePlayerDamages = updatePlayerDamages;
    }

    public void parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            System.out.println("Ligne vide ou null ignorée");
            return;
        }

        parseSpellCast(line);
        parseDamages(line);
    }

    private void parseSpellCast(String line) {
        Matcher castSpellMatcher = CAST_SPELL_PATTERN.matcher(line);

        if (castSpellMatcher.matches()) {
            String character = castSpellMatcher.group(1);
            String spellName = castSpellMatcher.group(2);

            if (character == null || character.trim().isEmpty() ||
                    spellName == null || spellName.trim().isEmpty()) {
                System.out.println("Données de sort incomplètes: " + line);
            }

            // TODO: Ajouter une action à effectuer pour les sorts

        }
    }

    private void parseDamages(String line) {
        Matcher damagesMatcher = DAMAGES_PATTERN.matcher(line);

        if (damagesMatcher.matches()) {
            String character = damagesMatcher.group(1);
            String damagesStr = damagesMatcher.group(2);
            String element = damagesMatcher.group(3);

            if (character == null || character.trim().isEmpty()) {
                System.out.println("Personnage non identifié dans la ligne de dégâts: " + line);
            }

            int damages;
            try {
                damages = Integer.parseInt(damagesStr);
            } catch (NumberFormatException e) {
                System.out.println("Valeur de dégâts invalide: " + damagesStr);
                damages = 0;
            }

            Player player = new Player(character, 0);
            updatePlayerDamages.update(player, damages);
        }
    }
}
