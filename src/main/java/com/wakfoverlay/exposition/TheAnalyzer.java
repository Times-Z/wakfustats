package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchPlayer;
import com.wakfoverlay.domain.fight.port.primary.UpdatePlayer;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.exposition.LogFileReader.FileReadStatus;
import com.wakfoverlay.exposition.LogFileReader.ReadResult;

public class TheAnalyzer {
    private final LogFileReader logFileReader;
    private final LogLineParser logLineParser;
    private String currentFilePath;

    public TheAnalyzer(FetchPlayer fetchPlayer, UpdatePlayer updatePlayer, UpdateStatusEffect updateStatusEffect) {
        this.logFileReader = new LogFileReader();
        this.logLineParser = new LogLineParser(fetchPlayer, updatePlayer, updateStatusEffect);
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
