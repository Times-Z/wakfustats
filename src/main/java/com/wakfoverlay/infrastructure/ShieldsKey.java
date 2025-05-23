package com.wakfoverlay.infrastructure;

import java.util.Objects;

public class ShieldsKey {
    private final String targetName;
    private final int value;

    public ShieldsKey(String targetName, int value) {
        this.targetName = targetName;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShieldsKey shieldsKey = (ShieldsKey) o;
        return value == shieldsKey.value && Objects.equals(targetName, shieldsKey.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName, value);
    }
}

