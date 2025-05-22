package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public class StatusEffect {
    private final StatusEffectName name;
    private final SubType subType;

    public StatusEffect(LocalTime timestamp, StatusEffectName name, SubType subType) {
        this.name = name;
        this.subType = subType;
    }

    public StatusEffectName name() {
        return name;
    }

    public SubType subType() {
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

    public enum SubType {
        NO_SUB_TYPE,
        DISTORSION,
        INTOXIQUE,
        MAUDIT,
        PRIERE_SADIDA,
        TETATOXINE,
    }
}
