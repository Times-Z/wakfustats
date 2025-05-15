package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public class SpellCast {
    private final LocalTime timestamp;
    private final String casterName;
    private final String spellName;
    private final String additionalInfo;

    public SpellCast(LocalTime timestamp, String casterName, String spellName, String additionalInfo) {
        this.timestamp = timestamp;
        this.casterName = casterName;
        this.spellName = spellName;
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "SpellCast{" +
                "timestamp=" + timestamp +
                ", casterName='" + casterName + '\'' +
                ", spellName='" + spellName + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
