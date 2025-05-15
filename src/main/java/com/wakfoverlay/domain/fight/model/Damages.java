package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;
import java.util.Set;

public class Damages {
    private final LocalTime timestamp;
    private final int damageAmount;
    private final Set<String> elements;

    public Damages(LocalTime timestamp, int damageAmount, Set<String> elements) {
        this.timestamp = timestamp;
        this.damageAmount = damageAmount;
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "Damages{" +
                "timestamp=" + timestamp +
                ", damageAmount=" + damageAmount +
                ", elements=" + elements +
                '}';
    }
}
