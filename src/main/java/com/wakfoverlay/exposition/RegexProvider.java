package com.wakfoverlay.exposition;

import java.util.regex.Pattern;

public class RegexProvider {
    private final Pattern CAST_SPELL_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*(.*)\\s+lance\\s+le\\s+sort\\s+(.*)");
    private final Pattern DAMAGES_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*([^:]+):\\s*([-]\\d+)\\s+PV\\s+\\((.*)\\)");
    private final Pattern HEALS_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*([^:]+):\\s*([+]\\d+)\\s+PV\\s+\\((.*)\\)");
    private final Pattern STATUS_EFFECT_PATTERN = Pattern
            .compile(".*\\[Information\\s*\\(jeu\\)\\]\\s*([^:]+):\\s*([^()]+)\\s*\\(Niv\\.(\\d+)\\)");



    public Pattern castSpellPattern() {
        return CAST_SPELL_PATTERN;
    }

    public Pattern damagesPattern() {
        return DAMAGES_PATTERN;
    }

    public Pattern healsPattern() {
        return HEALS_PATTERN;
    }

    public Pattern statusEffectPattern() {
        return STATUS_EFFECT_PATTERN;
    }
}
