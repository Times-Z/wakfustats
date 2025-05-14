package com.wakfoverlay.exposition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TheAnalyzer {
    private final List<String> logLines = new ArrayList<>();
    private long lastReadPosition = 0;
    private final UserPreferences userPreferences;

    public TheAnalyzer(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        this.lastReadPosition = userPreferences.getLastReadPosition(0);
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

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            if (file.length() < lastReadPosition) {
                lastReadPosition = 0;
            }

            file.seek(lastReadPosition);
            String line;
            while ((line = file.readLine()) != null) {
                logLines.add(line);
                System.out.println(line); // TODO: remove this line
            }
            lastReadPosition = file.getFilePointer();
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
