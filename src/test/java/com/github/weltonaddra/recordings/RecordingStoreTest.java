package com.github.weltonaddra.recordings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordingStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void roundTripsRecordings() throws IOException {
        Path file = tempDir.resolve("library.csv");
        List<Recording> recordings = List.of(
                new Recording("Blue in Green", "Miles Davis", 337),
                new Recording("Hello, Goodbye", "The \"Fab\" Four", 208));

        RecordingStore.save(file, recordings);
        List<Recording> loaded = RecordingStore.load(file);

        assertEquals(2, loaded.size());
        assertEquals("Hello, Goodbye", loaded.get(1).getTitle());
        assertEquals("The \"Fab\" Four", loaded.get(1).getArtist());
        assertEquals(337, loaded.get(0).getPlayTimeSeconds());
    }

    @Test
    void missingFileLoadsAsEmptyList() throws IOException {
        assertEquals(List.of(), RecordingStore.load(tempDir.resolve("nope.csv")));
    }

    @Test
    void malformedLineReportsItsLineNumber() throws IOException {
        Path file = tempDir.resolve("bad.csv");
        Files.write(file, List.of(
                RecordingStore.HEADER,
                "Title,Artist,120",
                "Sad,Artist,not-a-number"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> RecordingStore.load(file));
        assertTrue(error.getMessage().contains("Line 3"), error.getMessage());
    }
}
