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

    public record DamageKey(int amount, Set<String> elements) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DamageKey that = (DamageKey) obj;
            return amount == that.amount && Objects.equals(elements, that.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amount, elements);
        }
    }
}
