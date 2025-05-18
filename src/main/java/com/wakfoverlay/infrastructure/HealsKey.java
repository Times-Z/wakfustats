package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public record HealsKey(int amount, Set<String> elements) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HealsKey that = (HealsKey) obj;
        return amount == that.amount && Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, elements);
    }
}
