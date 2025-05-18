package com.wakfoverlay.domain.logs;

import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.domain.logs.model.ReadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TheFileReaderTest {

    private TheFileReader fileReader;
    private static final String TEST_FILE_PATH = "src/test/resources/wakfu.log";

    @BeforeEach
    void setUp() {
        fileReader = new TheFileReader();
    }

    @Test
    void should_read_all_file() {
        ReadResult result = fileReader.readNewLines(TEST_FILE_PATH);

        assertEquals(FileReadStatus.SUCCESS, result.status());
        assertFalse(result.lines().isEmpty());
    }

    @Test
    void should_reset_position() {
        ReadResult firstRead = fileReader.readNewLines(TEST_FILE_PATH);
        int initialLineCount = firstRead.lines().size();

        fileReader.resetPosition(TEST_FILE_PATH);

        ReadResult afterReset = fileReader.readNewLines(TEST_FILE_PATH);
        assertEquals(initialLineCount, afterReset.lines().size());
    }

    @Test
    void should_set_position() {
        fileReader.setPositionToEnd(TEST_FILE_PATH);

        ReadResult result = fileReader.readNewLines(TEST_FILE_PATH);
        assertEquals(FileReadStatus.SUCCESS, result.status());
        assertTrue(result.lines().isEmpty());
    }

    @Test
    void should_not_found_file() {
        ReadResult result = fileReader.readNewLines("fichier_inexistant.txt");
        assertEquals(FileReadStatus.FILE_NOT_FOUND, result.status());
        assertTrue(result.lines().isEmpty());
    }

    @Test
    void should_not_select_file() {
        ReadResult result = fileReader.readNewLines(null);
        assertEquals(FileReadStatus.NO_FILE_SELECTED, result.status());
        assertTrue(result.lines().isEmpty());

        ReadResult result2 = fileReader.readNewLines("");
        assertEquals(FileReadStatus.NO_FILE_SELECTED, result2.status());
        assertTrue(result2.lines().isEmpty());
    }
}
