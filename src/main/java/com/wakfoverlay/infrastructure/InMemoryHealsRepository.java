package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Heals;
import com.wakfoverlay.domain.fight.port.secondary.HealsRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryHealsRepository implements HealsRepository {
    private final Map<AmountAndElementsKey, LocalTime> healsMap = new HashMap<>();

    @Override
    public void addHeals(Heals heals) {
        AmountAndElementsKey key = new AmountAndElementsKey(heals.amount(), heals.elements());
        healsMap.put(key, heals.timestamp());
    }

    @Override
    public Optional<Heals> find(Heals heals) {
        AmountAndElementsKey key = new AmountAndElementsKey(heals.amount(), heals.elements());
        LocalTime timestamp = healsMap.get(key);
        if (timestamp != null) {
            return Optional.of(new Heals(timestamp, heals.amount(), heals.elements()));
        }
        return Optional.empty();
    }

    public Map<AmountAndElementsKey, LocalTime> getHealsMap() {
        return Map.copyOf(healsMap);
    }

    public void resetHeals() {
        healsMap.clear();
    }
}
