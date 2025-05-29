package com.wakfoverlay.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CharacterColorManager {
    private static final List<String> CHARACTER_COLORS = Arrays.asList(
            "#33fffe", "#EE1DED", "#7DFA00", "#FF4A00", "#FBE33B", "#6D1FA9",
            "#FF69B4", "#00CED1", "#32CD32", "#FF6347", "#9370DB", "#20B2AA",
            "#FF1493", "#00FF7F", "#FF8C00", "#BA55D3", "#00BFFF", "#ADFF2F"
    );

    private static final Map<String, String> characterColorMapping = new ConcurrentHashMap<>();
    private static final Set<String> usedColors = ConcurrentHashMap.newKeySet();

    public static String getColorForCharacter(String characterName) {
        return characterColorMapping.computeIfAbsent(characterName, name -> {
            for (String color : CHARACTER_COLORS) {
                if (!usedColors.contains(color)) {
                    usedColors.add(color);
                    return color;
                }
            }

            int hash = Math.abs(characterName.hashCode());
            int colorIndex = hash % CHARACTER_COLORS.size();
            return CHARACTER_COLORS.get(colorIndex);
        });
    }

    public static void clearCache() {
        characterColorMapping.clear();
        usedColors.clear();
    }

    public static void removeCharacter(String characterName) {
        String color = characterColorMapping.remove(characterName);
        if (color != null) {
            usedColors.remove(color);
        }
    }

    public static int getAvailableColorsCount() {
        return CHARACTER_COLORS.size() - usedColors.size();
    }

    public static Map<String, String> getAllAssignedColors() {
        return new ConcurrentHashMap<>(characterColorMapping);
    }

    public static boolean assignSpecificColor(String characterName, String color) {
        if (CHARACTER_COLORS.contains(color) && !usedColors.contains(color)) {
            String oldColor = characterColorMapping.put(characterName, color);
            if (oldColor != null) {
                usedColors.remove(oldColor);
            }
            usedColors.add(color);
            return true;
        }
        return false;
    }
}
