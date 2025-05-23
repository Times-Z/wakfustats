package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public record Shields(
        LocalTime timestamp,
        String targetName,
        int amount
) {
}
