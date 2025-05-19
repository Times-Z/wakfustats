package com.wakfoverlay.domain.fight.model;

import java.time.LocalTime;

public record Shields(
        LocalTime timestamp,
        int amount
) {
}
