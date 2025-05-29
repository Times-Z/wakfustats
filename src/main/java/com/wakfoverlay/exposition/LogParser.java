package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.fight.FetchCharacterUseCase;
import com.wakfoverlay.domain.fight.port.primary.FetchStatusEffect;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.logs.TheFileReader;
import com.wakfoverlay.domain.logs.TheAnalyzer;
import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.domain.logs.model.ReadResult;

public class LogParser {
    private final TheFileReader theFileReader;
    private final TheAnalyzer theAnalyzer;
    private String currentFilePath;
    private boolean isInInitializationMode = false;

    public LogParser(FetchCharacterUseCase fetchCharacter, FetchStatusEffect fetchStatusEffect,
                     UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect) {
        this.theFileReader = new TheFileReader();
        this.theAnalyzer = new TheAnalyzer(fetchCharacter, fetchStatusEffect, updateCharacter, updateStatusEffect);
        this.currentFilePath = null;
    }

    public FileReadStatus readNewLogLines(String filePath, boolean isFirstLaunch) {
        if (currentFilePath == null || !currentFilePath.equals(filePath)) {
            currentFilePath = filePath;
            if (isFirstLaunch) {
                theFileReader.resetPosition(filePath);
            } else {
                theFileReader.setPositionToEnd(filePath);
            }
        }

        if (isInInitializationMode) {
            return FileReadStatus.SUCCESS;
        }

        ReadResult result = theFileReader.readNewLines(filePath);

        if (result.status() == FileReadStatus.SUCCESS && !result.lines().isEmpty()) {
            result.lines().forEach(theAnalyzer::analyze);
        }

        return result.status();
    }

    public FileReadStatus initializeFromFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return FileReadStatus.NO_FILE_SELECTED;
        }

        isInInitializationMode = true;

        try {
            theFileReader.resetPosition(filePath);
            ReadResult result = theFileReader.readFullFile(filePath);

            if (result.status() == FileReadStatus.SUCCESS && !result.lines().isEmpty()) {
                theAnalyzer.resetAccounting();
                result.lines().forEach(theAnalyzer::analyzeFighter);
            }

            return result.status();
        } finally {
            isInInitializationMode = false;
        }
    }
}
