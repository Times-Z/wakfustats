package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public record ShieldsKey(int amount) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShieldsKey that = (ShieldsKey) obj;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
