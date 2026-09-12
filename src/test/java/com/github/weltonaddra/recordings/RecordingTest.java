package com.github.weltonaddra.recordings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordingTest {

    @Test
    void storesTrimmedFields() {
        Recording recording = new Recording("  Blue in Green  ", " Miles Davis ", 337);
        assertEquals("Blue in Green", recording.getTitle());
        assertEquals("Miles Davis", recording.getArtist());
        assertEquals(337, recording.getPlayTimeSeconds());
    }

    @Test
    void formatsPlayTimeAsMinutesAndSeconds() {
        assertEquals("3:05", new Recording("A", "B", 185).formatPlayTime());
        assertEquals("0:59", new Recording("A", "B", 59).formatPlayTime());
        assertEquals("10:00", new Recording("A", "B", 600).formatPlayTime());
    }

    @Test
    void rejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> new Recording("", "Artist", 100));
        assertThrows(IllegalArgumentException.class, () -> new Recording("Title", "   ", 100));
        assertThrows(IllegalArgumentException.class, () -> new Recording("Title", "Artist", 0));
        assertThrows(IllegalArgumentException.class, () -> new Recording("Title", "Artist", -5));
    }

    @Test
    void comparatorsOrderRecordings() {
        Recording a = new Recording("alpha", "Zed", 300);
        Recording b = new Recording("Beta", "anna", 100);

        assertTrue(Recording.byTitle().compare(a, b) < 0, "alpha sorts before Beta (case-insensitive)");
        assertTrue(Recording.byArtist().compare(b, a) < 0, "anna sorts before Zed");
        assertTrue(Recording.byPlaytime().compare(b, a) < 0, "100s sorts before 300s");
    }
}
