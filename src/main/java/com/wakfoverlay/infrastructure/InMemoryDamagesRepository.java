package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class InMemoryDamagesRepository implements DamagesRepository {
    private final Map<DamageKey, LocalTime> damagesMap = new HashMap<>();

    @Override
    public void addDamages(Damages damages) {
        DamageKey key = new DamageKey(damages.amount(), damages.elements());
        damagesMap.put(key, damages.timestamp());
    }

    @Override
    public Optional<Damages> find(Damages damages) {
        DamageKey key = new DamageKey(damages.amount(), damages.elements());
        LocalTime timestamp = damagesMap.get(key);
        if (timestamp != null) {
            return Optional.of(new Damages(timestamp, damages.amount(), damages.elements()));
        }
        return Optional.empty();
    }

    public Map<DamageKey, LocalTime> getDamagesMap() {
        return Collections.unmodifiableMap(damagesMap);
    }
}
