package com.wakfoverlay.exposition;

import java.util.prefs.Preferences;

public class UserPreferences {
    private static final String FILE_PATH_KEY = "selectedFilePath";
    private final Preferences prefs;

    public UserPreferences(Class<?> aClass) {
        this.prefs = Preferences.userNodeForPackage(aClass);
    }

    public void saveFilePath(String filePath) {
        prefs.put(FILE_PATH_KEY, filePath);
    }

    public String getFilePath() {
        return prefs.get(FILE_PATH_KEY, null);
    }
}
