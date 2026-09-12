package com.github.weltonaddra.recordings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordingLibraryTest {

    private static Recording r(String title, String artist, int seconds) {
        return new Recording(title, artist, seconds);
    }

    @Test
    void findsArtistsCaseInsensitive() {
        RecordingLibrary library = new RecordingLibrary();
        library.add(r("So What", "Miles Davis", 545));
        library.add(r("Hello", "Adele", 295));
        library.add(r("Blue in Green", "Miles davis", 337));

        assertEquals(2, library.findByArtistContains("MILES").size());
        assertTrue(library.findByArtistContains("zzz").isEmpty());
    }

    @Test
    void removeAtReturnsTheRemovedRecording() {
        RecordingLibrary library = new RecordingLibrary();
        library.add(r("A", "Artist", 100));
        library.add(r("B", "Artist", 200));

        Recording removed = library.removeAt(0);
        assertEquals("A", removed.getTitle());
        assertEquals(1, library.size());
        assertThrows(IndexOutOfBoundsException.class, () -> library.removeAt(9));
    }

    @Test
    void summarizesPlaytime() {
        RecordingLibrary library = new RecordingLibrary();
        library.add(r("A", "Artist", 100));
        library.add(r("B", "Artist", 200));
        library.add(r("C", "Artist", 300));

        assertEquals(600, library.totalPlayTimeSeconds());
        assertEquals(200.0, library.averagePlayTimeSeconds(), 0.001);
        assertEquals("C", library.longest().orElseThrow().getTitle());
        assertEquals("A", library.shortest().orElseThrow().getTitle());
    }

    @Test
    void emptyLibrarySummariesAreSafe() {
        RecordingLibrary library = new RecordingLibrary();
        assertEquals(0, library.totalPlayTimeSeconds());
        assertEquals(0.0, library.averagePlayTimeSeconds(), 0.001);
        assertTrue(library.longest().isEmpty());
        assertTrue(library.shortest().isEmpty());
    }
}
