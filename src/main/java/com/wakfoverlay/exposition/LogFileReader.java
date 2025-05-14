package com.wakfoverlay.exposition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileReader {
    private final Map<String, Long> filePositions = new HashMap<>();
    private final UserPreferences userPreferences;

    public LogFileReader(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public ReadResult readNewLines(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return new ReadResult(FileReadStatus.NO_FILE_SELECTED, List.of());
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            filePositions.remove(filePath);
            return new ReadResult(FileReadStatus.FILE_NOT_FOUND, List.of());
        }

        try {
            long fileSize = Files.size(path);
            long lineCount;
            Long lastPosition = filePositions.getOrDefault(filePath, 0L);

            if (fileSize < lastPosition) {
                lastPosition = 0L;
            }
            
            List<String> newLines;
            try (Stream<String> lines = Files.lines(path)) {
                List<String> allLines = lines.toList();
                lineCount = allLines.size();

                if (lastPosition > lineCount) {
                    lastPosition = 0L;
                }
                
                newLines = allLines.stream()
                    .skip(lastPosition)
                    .collect(Collectors.toList());
            }

            filePositions.put(filePath, lineCount);

            if (newLines.isEmpty()) {
                return new ReadResult(FileReadStatus.SUCCESS, Collections.emptyList());
            }

            return new ReadResult(FileReadStatus.SUCCESS, newLines);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            return new ReadResult(FileReadStatus.IO_ERROR, List.of());
        }
    }

    public void resetPosition(String filePath) {
        if (filePath != null) {
            filePositions.remove(filePath);
        }
    }

    public record ReadResult(FileReadStatus status, List<String> lines) {}

    public enum FileReadStatus {
        SUCCESS,
        NO_FILE_SELECTED,
        FILE_NOT_FOUND,
        EMPTY_FILE,
        IO_ERROR
    }
}
