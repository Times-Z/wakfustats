package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public class StatusEffect {
    private final StatusEffectName name;
    private final String subType;

    public StatusEffect(LocalTime timestamp, StatusEffectName name, String subType) {
        this.name = name;
        this.subType = subType;
    }

    public StatusEffectName name() {
        return name;
    }

    public String subType() {
        return subType;
    }

    @Override
    public String toString() {
        return "StatusEffect{" +
                ", statusName='" + name + '\'' +
                ", subType=" + subType +
                '}';
    }

    public record StatusEffectName(String value) {
    }
}
