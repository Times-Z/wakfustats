package com.wakfoverlay.domain.logs;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class RegexProvider {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
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
            .compile("INFO (\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[.*?]\\s+\\(.*?\\)\\s+-\\s+\\[Information \\(jeu\\)]\\s+(.+?):\\s+\\+([\\d\\s]+)\\s*Armure",
                    Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern SUMMON_PATTERN = Pattern
            .compile("",
                    Pattern.UNICODE_CHARACTER_CLASS);

    public DateTimeFormatter timeFormatterPattern() {
        return TIME_FORMATTER;
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

    public Pattern summonPattern() {
        return SUMMON_PATTERN;
    }
}
