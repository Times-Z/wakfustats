package com.wakfoverlay.exposition;

import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TheAnalyzer {
    private final List<String> logLines = new ArrayList<>();
    private long lastReadPosition;
    private final UserPreferences userPreferences;
    private final UpdatePlayerDamages updatePlayerDamages;

    public TheAnalyzer(UserPreferences userPreferences, UpdatePlayerDamages updatePlayerDamages) {
        this.userPreferences = userPreferences;
        this.lastReadPosition = userPreferences.getLastReadPosition(0);
        this.updatePlayerDamages = updatePlayerDamages;
    }

//    public FileReadStatus readLogFile(String filePath) {
//        logLines.clear();
//
//        if (filePath == null || filePath.isEmpty()) {
//            return FileReadStatus.NO_FILE_SELECTED;
//        }
//
//        Path path = Paths.get(filePath);
//        if (!Files.exists(path)) {
//            return FileReadStatus.FILE_NOT_FOUND;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                logLines.add(line);
//            }
//        } catch (IOException e) {
//            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
//            return FileReadStatus.IO_ERROR;
//        }
//
//        if (logLines.isEmpty()) {
//            return FileReadStatus.EMPTY_FILE;
//        }
//
//        return FileReadStatus.SUCCESS;
//    }

    public FileReadStatus readNewLogLines(String filePath) {
        logLines.clear();

        if (filePath == null || filePath.isEmpty()) {
            return FileReadStatus.NO_FILE_SELECTED;
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return FileReadStatus.FILE_NOT_FOUND;
        }

        try (Stream<String> lines = Files.lines(path)) {
            List<String> newLines = lines.skip(lastReadPosition).toList();
            logLines.addAll(newLines);
            lastReadPosition += newLines.size();
            userPreferences.saveLastReadPosition(lastReadPosition);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            return FileReadStatus.IO_ERROR;
        }

        return FileReadStatus.SUCCESS;
    }

    public enum FileReadStatus {
        SUCCESS,
        NO_FILE_SELECTED,
        FILE_NOT_FOUND,
        EMPTY_FILE,
        IO_ERROR
    }
}
