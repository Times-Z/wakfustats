package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class InMemoryDamagesRepository implements DamagesRepository {
    private final Map<DamageKey, List<LocalTime>> damagesMap = new HashMap<>();

    public void addDamages(Damages damages) {
        DamageKey key = new DamageKey(damages.amount(), damages.elements());
        List<LocalTime> timestamps = damagesMap.computeIfAbsent(key, k -> new ArrayList<>());
        if (timestamps.stream().noneMatch(timestamp -> areTimestampsClose(timestamp, damages.timestamp()))) {
            timestamps.add(damages.timestamp());
            timestamps.sort(Comparator.naturalOrder());
        }
    }

    @Override
    public boolean exists(Damages damages) {
        DamageKey key = new DamageKey(damages.amount(), damages.elements());
        return damagesMap.computeIfAbsent(key, k -> new ArrayList<>()).stream()
                .anyMatch(timestamp -> areTimestampsClose(timestamp, damages.timestamp()));
    }

    private boolean areTimestampsClose(LocalTime timestamp1, LocalTime timestamp2) {
        Duration duration = Duration.between(timestamp1, timestamp2).abs();
        return duration.toMillis() <= 800;
    }

    private record DamageKey(int amount, Set<String> elements) {
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
