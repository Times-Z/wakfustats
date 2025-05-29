package com.wakfoverlay.infrastructure;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.port.secondary.TargetedDamagesRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTargetedDamagesRepository implements TargetedDamagesRepository {
    private final Map<Character, Damages> targetedDamages = new HashMap<>();

    @Override
    public void resetTargetedDamages() {
        targetedDamages.clear();
    }

    @Override
    public void addDamages(Character attacker, Damages damages) {
        targetedDamages.merge(attacker, damages, (existing, newDamages) ->
                new Damages(existing.timestamp(), existing.targetName(),existing.amount() + newDamages.amount(), existing.elements()));
    }

    @Override
    public Map<Character, Damages> targetedDamages() {
        return targetedDamages;
    }
}
