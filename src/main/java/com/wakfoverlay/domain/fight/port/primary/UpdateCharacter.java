package com.wakfoverlay.domain.fight.port.primary;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Damages;
import com.wakfoverlay.domain.fight.model.Heals;
import com.wakfoverlay.domain.fight.model.Shields;

public interface UpdateCharacter {
    void create(Character character);
    void updateDamages(Character character, Damages damages, boolean multiAccounting, int numberOfAccounts);
    void updateHeals(Character character, Heals heals, boolean multiAccounting, int numberOfAccounts);
    void updateShields(Character character, Shields shields, boolean multiAccounting, int numberOfAccounts);
    void resetCharactersStats();
    void resetCharacters();
}
