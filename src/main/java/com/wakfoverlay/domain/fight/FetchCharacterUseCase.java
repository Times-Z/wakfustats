package com.wakfoverlay.domain.fight;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Character.CharacterName;
import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.secondary.CharactersRepository;
import com.wakfoverlay.domain.fight.port.secondary.TargetedDamagesRepository;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.*;

public record FetchCharacterUseCase(
        CharactersRepository charactersRepository,
        TargetedDamagesRepository targetDamagesRepository
) implements FetchCharacter {
    @Override
    public Character character(CharacterName name) {
        return charactersRepository.character(name)
                .stream()
                .findFirst()
                .orElse(new Character(new CharacterName("Unknown"), 0, 0, 0, empty()));
    }

    @Override
    public Characters rankedCharactersByDamages() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.damages(), p1.damages()))
                .filter(character -> character.damages() > 0)
                .collect(toCollection(ArrayList::new)));
    }

    @Override
    public Characters rankedCharactersByDamagesForBoss() {
        return new Characters(
                targetDamagesRepository.targetedDamages()
                        .entrySet()
                        .stream()
                        .collect(groupingBy(
                                entry -> entry.getKey().name(),
                                summingInt(entry -> entry.getValue().amount())
                        ))
                        .entrySet()
                        .stream()
                        .map(this::createCharacterWithBossDamages)
                        .sorted(comparing(Character::damages, reverseOrder()))
                        .filter(character -> character.damages() > 0)
                        .collect(toCollection(ArrayList::new))
        );
    }

    @Override
    public Characters rankedCharactersByHeals() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.heals(), p1.heals()))
                .filter(character -> character.heals() > 0)
                .collect(toCollection(ArrayList::new)));
    }

    @Override
    public Characters rankedCharactersByShields() {
        return new Characters(charactersRepository.characters()
                .characters()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.shields(), p1.shields()))
                .filter(character -> character.shields() > 0)
                .collect(toCollection(ArrayList::new)));
    }

    @Override
    public boolean exist(CharacterName name) {
        return charactersRepository.character(name)
                .stream()
                .findFirst()
                .isPresent();
    }

    @Override
    public boolean isAllied(CharacterName characterName) {
        return charactersRepository.characters()
                .characters()
                .stream()
                .anyMatch(character -> character.name().equals(characterName));
    }

    private Character createCharacterWithBossDamages(Map.Entry<CharacterName, Integer> bossDamageEntry) {
        Character originalCharacter = character(bossDamageEntry.getKey());
        return new Character(
                originalCharacter.name(),
                bossDamageEntry.getValue(),
                originalCharacter.heals(),
                originalCharacter.shields(),
                originalCharacter.summoner()
        );
    }
}
