package com.github.weltonaddra.recordings;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordingSorterTest {

    private static Recording r(String title, String artist, int seconds) {
        return new Recording(title, artist, seconds);
    }

    @Test
    void sortsByTitleCaseInsensitive() {
        List<Recording> input = List.of(
                r("Zanzibar", "Zed", 100),
                r("blue", "Amy", 200),
                r("Apple", "Cal", 300));

        List<Recording> sorted = RecordingSorter.sortedBy(input, SortField.TITLE);

        assertEquals(List.of("Apple", "blue", "Zanzibar"),
                sorted.stream().map(Recording::getTitle).toList());
    }

    @Test
    void sortsByArtistThenTitle() {
        List<Recording> input = List.of(
                r("Second", "Amy", 100),
                r("First", "Amy", 200),
                r("Zeta", "Bob", 300));

        List<Recording> sorted = RecordingSorter.sortedBy(input, SortField.ARTIST);

        assertEquals(List.of("First", "Second", "Zeta"),
                sorted.stream().map(Recording::getTitle).toList());
    }

    @Test
    void sortsByPlaytimeAscending() {
        List<Recording> input = List.of(
                r("Long", "A", 500),
                r("Short", "B", 60),
                r("Medium", "C", 240));

        List<Recording> sorted = RecordingSorter.sortedBy(input, SortField.PLAYTIME);

        assertEquals(List.of("Short", "Medium", "Long"),
                sorted.stream().map(Recording::getTitle).toList());
    }

    @Test
    void doesNotMutateTheInputList() {
        List<Recording> input = new ArrayList<>(List.of(
                r("Zeta", "A", 100),
                r("Alpha", "B", 200)));

        RecordingSorter.sortedBy(input, SortField.TITLE);

        assertEquals("Zeta", input.get(0).getTitle());
        assertEquals("Alpha", input.get(1).getTitle());
    }

    @Test
    void parsesSortFieldFromUserInput() {
        assertEquals(SortField.TITLE, SortField.fromInput("Recording Title"));
        assertEquals(SortField.ARTIST, SortField.fromInput("  recording artist "));
        assertEquals(SortField.PLAYTIME, SortField.fromInput("playtime"));
        assertThrows(IllegalArgumentException.class, () -> SortField.fromInput("genre"));
        assertThrows(IllegalArgumentException.class, () -> SortField.fromInput(null));
    }
}
