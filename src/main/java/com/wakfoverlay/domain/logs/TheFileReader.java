package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.domain.logs.model.ReadResult;
import com.wakfoverlay.domain.logs.model.ReadMode;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TheFileReader {
    private final Map<String, Long> fileBytePositions = new HashMap<>();
    private final Map<String, Long> lastModifiedTimes = new HashMap<>();
    private final Map<String, Long> fileSizes = new HashMap<>();

    public TheFileReader() {
    }

    public ReadResult readNewLines(String filePath) {
        return readLines(filePath, ReadMode.INCREMENTAL);
    }

    public ReadResult readFullFile(String filePath) {
        return readLines(filePath, ReadMode.INITIALIZATION);
    }

    private ReadResult readLines(String filePath, ReadMode mode) {
        if (!isValidFilePath(filePath)) {
            return createErrorResult(FileReadStatus.NO_FILE_SELECTED);
        }

        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            cleanupFileTracking(filePath);
            return createErrorResult(FileReadStatus.FILE_NOT_FOUND);
        }

        try {
            long currentFileSize = Files.size(path);

            if (hasFileBeenTruncated(filePath, currentFileSize)) {
                handleFileTruncation(filePath);
            }

            if (mode == ReadMode.INITIALIZATION) {
                resetPosition(filePath);
            }

            if (mode == ReadMode.INCREMENTAL && !hasFileBeenModified(filePath, path)) {
                return createSuccessResult(Collections.emptyList());
            }

            List<String> newLines = readLinesFromBytePosition(path, filePath);
            updateFileTracking(filePath, path, currentFileSize);

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
            fileBytePositions.put(filePath, 0L);
            return;
        }
        try {
            long fileSize = Files.size(path);
            fileBytePositions.put(filePath, fileSize);
            fileSizes.put(filePath, fileSize);
        } catch (IOException e) {
            fileBytePositions.put(filePath, 0L);
            fileSizes.put(filePath, 0L);
        }
    }

    public void resetPosition(String filePath) {
        if (filePath != null) {
            fileBytePositions.put(filePath, 0L);
        }
    }

    private boolean isValidFilePath(String filePath) {
        return filePath != null && !filePath.isEmpty();
    }

    private boolean hasFileBeenTruncated(String filePath, long currentFileSize) {
        Long lastKnownSize = fileSizes.get(filePath);
        Long currentPosition = fileBytePositions.get(filePath);

        return (currentPosition != null && currentFileSize < currentPosition) ||
                (lastKnownSize != null && currentFileSize < lastKnownSize);
    }

    private void handleFileTruncation(String filePath) {
        // Reset position de lecture au début
        fileBytePositions.put(filePath, 0L);
        System.out.println("Détection de troncature du fichier: " + filePath);
    }

    private boolean hasFileBeenModified(String filePath, Path path) throws IOException {
        long currentModified = Files.getLastModifiedTime(path).toMillis();
        Long lastModified = lastModifiedTimes.get(filePath);

        return lastModified == null || currentModified > lastModified;
    }

    private void updateFileTracking(String filePath, Path path, long fileSize) throws IOException {
        long currentModified = Files.getLastModifiedTime(path).toMillis();
        lastModifiedTimes.put(filePath, currentModified);
        fileSizes.put(filePath, fileSize);
    }

    private void cleanupFileTracking(String filePath) {
        fileBytePositions.remove(filePath);
        lastModifiedTimes.remove(filePath);
        fileSizes.remove(filePath);
    }

    private List<String> readLinesFromBytePosition(Path path, String filePath) throws IOException {
        List<String> newLines = new ArrayList<>();
        long startPosition = fileBytePositions.getOrDefault(filePath, 0L);

        if (startPosition == 0L) {
            try {
                newLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                fileBytePositions.put(filePath, Files.size(path));
                return newLines;
            } catch (IOException e) {
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            if (startPosition > raf.length()) {
                startPosition = 0L;
            }

            raf.seek(startPosition);
            String line;
            while ((line = raf.readLine()) != null) {
                newLines.add(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }

            fileBytePositions.put(filePath, raf.getFilePointer());
        }
        return newLines;
    }

    private ReadResult createSuccessResult(List<String> lines) {
        return new ReadResult(FileReadStatus.SUCCESS, lines);
    }

    private ReadResult createErrorResult(FileReadStatus status) {
        return new ReadResult(status, List.of());
    }
}
