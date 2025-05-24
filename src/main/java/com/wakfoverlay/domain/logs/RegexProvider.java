package com.wakfoverlay.domain.logs;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class RegexProvider {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
    private static final Pattern FIGHT_CREATION = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(bae:47\\)\\s+-\\s+CREATION DU COMBAT",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern FIGHT_END = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(aZb:92\\)\\s+-\\s+\\[FIGHT] End fight with id (\\d+)",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern CONNECTION_LOST = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(ccN:45\\)\\s+-\\s+Connexion avec le serveur perdue",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern FIGHTER_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?\\]\\s+\\(.*?\\)\\s+-\\s+\\[_FL_\\]\\s+fightId=(\\d+)\\s+(.*?)\\s+breed\\s*:\\s*(\\d+).*?isControlledByAI=(\\w+)",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SPELL_CAST_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3}) \\[.*?\\] \\(.*?\\) - \\[Information \\(jeu\\)\\] (.+?) lance le sort (\\w+)(?: \\((.*?)\\))?",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern STATUS_EFFECT_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3}) \\[.*?] \\(.*?\\) - \\[Information \\(jeu\\)] ([^:]+): ([^()]+?) \\((\\+?\\d+ Niv\\.|Niv\\.\\d+)\\)(?: \\([^)]*\\))?",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern DAMAGES_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(.*?\\)\\s+-\\s+\\[Information \\(jeu\\)]\\s+(.+?):\\s+-([\\d\\s]+)\\s*PV(\\s*(?:\\([^)]*\\)\\s*)*)?",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern HEALS_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(.*?\\)\\s+-\\s+\\[Information \\(jeu\\)]\\s+(.+?):\\s+\\+([\\d\\s]+)\\s*PV(\\s*(?:\\([^)]*\\)\\s*)*)?",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SHIELDS_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(.*?\\)\\s+-\\s+\\[Information \\(jeu\\)]\\s+(.+?):\\s+([\\d\\s]+)\\s*Armure",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SUMMONER_PATTERN = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(.*?\\)\\s+-\\s+\\[Information \\(jeu\\)]\\s+(.+?):\\s*Invoque",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SUMMONING_PATTERN_1 = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(eIu:106\\)\\s+-\\s+Instanciation d'une nouvelle invocation avec un id de -?(\\d+)",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SUMMONING_PATTERN_2 = Pattern
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(eIA:92\\)\\s+-\\s+New summon with id -?(\\d+)",
                    Pattern.UNICODE_CHARACTER_CLASS);

    public DateTimeFormatter timeFormatterPattern() {
        return TIME_FORMATTER;
    }

    public Pattern fightCreationPattern() {
        return FIGHT_CREATION;
    }

    public Pattern fightEndPattern() {
        return FIGHT_END;
    }

    public Pattern connectionLostPattern() {
        return CONNECTION_LOST;
    }

    public Pattern fighterPattern() {
        return FIGHTER_PATTERN;
    }

    public Pattern spellCastPattern() {
        return SPELL_CAST_PATTERN;
    }

    public Pattern statusEffectPattern() {
        return STATUS_EFFECT_PATTERN;
    }

    public Pattern damagesPattern() {
        return DAMAGES_PATTERN;
    }

    public Pattern healsPattern() {
        return HEALS_PATTERN;
    }

    public Pattern shieldsPattern() {
        return SHIELDS_PATTERN;
    }

    public Pattern summonerPattern() {
        return SUMMONER_PATTERN;
    }

    public Pattern summoningPattern1() {
        return SUMMONING_PATTERN_1;
    }

    public Pattern summoningPattern2() {
        return SUMMONING_PATTERN_2;
    }
}
