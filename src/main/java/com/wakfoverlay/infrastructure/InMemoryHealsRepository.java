package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Heals;
import com.wakfoverlay.domain.fight.port.secondary.HealsRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryHealsRepository implements HealsRepository {
    private final Map<DamagesAndHealsKey, LocalTime> healsMap = new HashMap<>();

    @Override
    public void addHeals(Heals heals) {
        DamagesAndHealsKey key = new DamagesAndHealsKey(heals.amount(), heals.targetName(), heals.elements());
        healsMap.put(key, heals.timestamp());
    }

    @Override
    public Optional<Heals> find(Heals heals) {
        DamagesAndHealsKey key = new DamagesAndHealsKey(heals.amount(), heals.targetName(), heals.elements());
        LocalTime timestamp = healsMap.get(key);
        if (timestamp != null) {
            return Optional.of(new Heals(timestamp, heals.targetName(), heals.amount(), heals.elements()));
        }
        return Optional.empty();
    }

    public Map<DamagesAndHealsKey, LocalTime> getHealsMap() {
        return Map.copyOf(healsMap);
    }

    @Override
    public void resetHeals() {
        healsMap.clear();
    }
}
