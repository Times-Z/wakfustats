package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;
import java.util.Set;

public record Damages(
        LocalTime timestamp,
        String targetName,
        int amount,
        Set<String> elements
) {
}
