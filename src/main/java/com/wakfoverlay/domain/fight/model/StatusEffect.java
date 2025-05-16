package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public class StatusEffect {
//    private final LocalTime timestamp;
//    private final String targetName;
    private final StatusEffectName name;
//    private final Integer level;
    private final SubType subType;

    public StatusEffect(LocalTime timestamp, String targetName, StatusEffectName name, Integer level, SubType subType) {
//        this.timestamp = timestamp;
//        this.targetName = targetName;
        this.name = name;
//        this.level = level;
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
//                "timestamp=" + timestamp +
//                ", targetName='" + targetName + '\'' +
                ", statusName='" + name + '\'' +
//                ", level=" + level +
                ", subType=" + subType +
                '}';
    }

    public record StatusEffectName(String value) {
    }

    public enum SubType {
        NO_SUB_TYPE,
        TETATOXINE,
        INTOXIQUE
    }
}
