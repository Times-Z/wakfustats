package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public class AmountAndElementsKey extends AmountKey {
    private final Set<String> elements;

    public AmountAndElementsKey(int value, Set<String> elements) {
        super(value);
        this.elements = elements;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AmountAndElementsKey that = (AmountAndElementsKey) obj;
        return super.equals(obj) && Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }
}
