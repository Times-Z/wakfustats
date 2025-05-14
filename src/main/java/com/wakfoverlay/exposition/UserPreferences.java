// UserPreferences.java
package com.wakfoverlay.exposition;

import java.util.prefs.Preferences;

public class UserPreferences {
    private static final String FILE_PATH_KEY = "selectedFilePath";
    private final Preferences prefs;
    private static final String LAST_READ_POSITION_KEY = "lastReadPosition";

    public UserPreferences(Class<?> aClass) {
        this.prefs = Preferences.userNodeForPackage(aClass);
    }

    public void saveFilePath(String filePath) {
        prefs.put(FILE_PATH_KEY, filePath);
    }

    public String getFilePath() {
        return prefs.get(FILE_PATH_KEY, null);
    }

    public void saveLastReadPosition(long position) {
        prefs.putLong(LAST_READ_POSITION_KEY, position);
    }

    public long getLastReadPosition(long defaultValue) {
        return prefs.getLong(LAST_READ_POSITION_KEY, defaultValue);
    }

    public void clearPreferences() {
        try {
            prefs.remove(FILE_PATH_KEY);
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression des préférences : " + e.getMessage());
        }
    }
}
