package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.logs.model.FilePosition;
import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.domain.logs.model.ReadResult;

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

public class TheFileReader {
    private final Map<String, Long> filePositions = new HashMap<>();

    public TheFileReader() {
    }

    public ReadResult readNewLines(String filePath) {
        if (!isValidFilePath(filePath)) {
            return createErrorResult(FileReadStatus.NO_FILE_SELECTED);
        }

        Path path = Paths.get(filePath);

        if (!fileExists(path)) {
            filePositions.remove(filePath);
            return createErrorResult(FileReadStatus.FILE_NOT_FOUND);
        }

        try {
            FilePosition filePosition = getAdjustedFilePosition(filePath, path);

            List<String> newLines = readLinesFromPosition(path, filePosition);

            updateFilePosition(filePath, filePosition.lineCount());

            return createSuccessResult(newLines);
        } catch (IOException e) {
            return createErrorResult(FileReadStatus.IO_ERROR);
        }
    }

    public void setPositionToEnd(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            filePositions.put(filePath, 0L);
            return;
        }
        try (Stream<String> lines = Files.lines(path)) {
            long lineCount = lines.count();
            filePositions.put(filePath, lineCount);
        } catch (IOException e) {
            filePositions.put(filePath, 0L);
        }
    }

    public void resetPosition(String filePath) {
        if (filePath != null) {
            filePositions.put(filePath, 0L);
        }
    }

    private boolean isValidFilePath(String filePath) {
        return filePath != null && !filePath.isEmpty();
    }

    private boolean fileExists(Path path) {
        return Files.exists(path);
    }

    private FilePosition getAdjustedFilePosition(String filePath, Path path) throws IOException {
        long fileSize = Files.size(path);
        Long lastPosition = filePositions.getOrDefault(filePath, 0L);

        if (fileSize < lastPosition) {
            lastPosition = 0L;
        }

        return new FilePosition(lastPosition, countLines(path));
    }

    private long countLines(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.count();
        }
    }

    private List<String> readLinesFromPosition(Path path, FilePosition filePosition) throws IOException {
        long lastPosition = filePosition.lastPosition();
        long lineCount = filePosition.lineCount();

        if (lastPosition > lineCount) {
            lastPosition = 0L;
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines.skip(lastPosition).collect(Collectors.toList());
        }
    }

    private void updateFilePosition(String filePath, long newPosition) {
        filePositions.put(filePath, newPosition);
    }

    private ReadResult createSuccessResult(List<String> lines) {
        if (lines.isEmpty()) {
            return new ReadResult(FileReadStatus.SUCCESS, Collections.emptyList());
        }
        return new ReadResult(FileReadStatus.SUCCESS, lines);
    }

    private ReadResult createErrorResult(FileReadStatus status) {
        return new ReadResult(status, List.of());
    }
}
