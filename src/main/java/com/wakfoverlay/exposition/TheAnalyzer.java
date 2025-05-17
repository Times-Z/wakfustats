package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.exposition.LogFileReader.FileReadStatus;
import com.wakfoverlay.exposition.LogFileReader.ReadResult;

public class TheAnalyzer {
    private final LogFileReader logFileReader;
    private final LogLineParser logLineParser;
    private String currentFilePath;

    private boolean firstLaunch = true;

    public TheAnalyzer(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect) {
        this.logFileReader = new LogFileReader();
        this.logLineParser = new LogLineParser(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect);
        this.currentFilePath = null;
    }

    public FileReadStatus readNewLogLines(String filePath) {
        if (currentFilePath == null || !currentFilePath.equals(filePath)) {
            currentFilePath = filePath;
            if (firstLaunch) {
                logFileReader.setPositionToEnd(filePath); // ← ICI
                firstLaunch = false;
            } else {
                logFileReader.resetPosition(filePath);
            }
        }

        ReadResult result = logFileReader.readNewLines(filePath);

        if (result.status() == FileReadStatus.SUCCESS && !result.lines().isEmpty()) {
            result.lines().forEach(logLineParser::analyze);
        }

        return result.status();
    }

    public void resetReadPosition() {
        if (currentFilePath != null) {
            logFileReader.resetPosition(currentFilePath);
        }
    }
}
