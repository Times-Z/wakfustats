package com.wakfoverlay.infrastructure;

import java.util.Objects;
import java.util.Set;

public class DamagesAndHealsKey extends ShieldsKey {
    private final Set<String> elements;

    public DamagesAndHealsKey(int value, String targetName, Set<String> elements) {
        super(targetName, value);
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DamagesAndHealsKey that = (DamagesAndHealsKey) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }
}
