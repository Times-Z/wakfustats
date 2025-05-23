package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public class AmountKey {
    private final String targetName;
    private final int value;

    public AmountKey(String targetName, int value) {
        this.targetName = targetName;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AmountKey amountKey = (AmountKey) o;
        return value == amountKey.value && Objects.equals(targetName, amountKey.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName, value);
    }
}

