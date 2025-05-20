package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public class AmountKey {
    private final int value;

    public AmountKey(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AmountKey that = (AmountKey) obj;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

