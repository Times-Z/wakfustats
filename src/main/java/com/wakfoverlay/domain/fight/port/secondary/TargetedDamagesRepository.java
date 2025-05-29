package com.wakfoverlay.domain.fight.port.secondary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;

import java.util.Map;

public interface TargetedDamagesRepository {
    void resetTargetedDamages();
    void addDamages(Character attacker, Damages damages);
    Map<Character, Damages> targetedDamages();
}
