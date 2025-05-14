package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.exposition.LogFileReader.FileReadStatus;
import com.wakfoverlay.exposition.LogFileReader.ReadResult;

public class TheAnalyzer {
    private final LogFileReader logFileReader;
    private final LogLineParser logLineParser;
    private String currentFilePath;

    public TheAnalyzer(UserPreferences userPreferences, UpdatePlayerDamages updatePlayerDamages) {
        this.logFileReader = new LogFileReader(userPreferences);
        this.logLineParser = new LogLineParser(updatePlayerDamages);
        this.currentFilePath = null;
    }

    public FileReadStatus readNewLogLines(String filePath) {
        if (currentFilePath == null || !currentFilePath.equals(filePath)) {
            currentFilePath = filePath;
            logFileReader.resetPosition(filePath);
        }

        ReadResult result = logFileReader.readNewLines(filePath);

        if (result.status() == FileReadStatus.SUCCESS && !result.lines().isEmpty()) {
            result.lines().forEach(logLineParser::parseLine);
        }

        return result.status();
    }

    public void resetReadPosition() {
        if (currentFilePath != null) {
            logFileReader.resetPosition(currentFilePath);
        }
    }
}
