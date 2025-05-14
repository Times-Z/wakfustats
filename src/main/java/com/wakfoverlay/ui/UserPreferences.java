// UserPreferences.java
package com.wakfoverlay.ui;

import java.util.prefs.Preferences;

public class UserPreferences {
    private static final String FILE_PATH_KEY = "selectedFilePath";
    private final Preferences prefs;

    public UserPreferences(Class<?> clazz) {
        this.prefs = Preferences.userNodeForPackage(clazz);
    }

    public void saveFilePath(String filePath) {
        prefs.put(FILE_PATH_KEY, filePath);
    }

    public String getFilePath() {
        return prefs.get(FILE_PATH_KEY, null);
    }

    public void clearPreferences() {
        try {
            prefs.remove(FILE_PATH_KEY);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression des préférences : " + e.getMessage());
        }
    }
}
