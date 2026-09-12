package com.github.weltonaddra.recordings;

import java.util.Comparator;

/**
 * The fields a collection of recordings can be sorted by, each with its
 * own comparator and the label shown in the console prompt.
 */
public enum SortField {

    TITLE("recording title", Recording.byTitle()),
    ARTIST("recording artist", Recording.byArtist()),
    PLAYTIME("playtime", Recording.byPlaytime());

    private final String label;
    private final Comparator<Recording> comparator;

    SortField(String label, Comparator<Recording> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String label() {
        return label;
    }

    public Comparator<Recording> comparator() {
        return comparator;
    }

    /** Parses user input such as "Recording Artist" (case-insensitive). */
    public static SortField fromInput(String input) {
        if (input != null) {
            String normalized = input.strip().toLowerCase();
            for (SortField field : values()) {
                if (field.label.equals(normalized)) {
                    return field;
                }
            }
        }
        throw new IllegalArgumentException(
                "Unknown sort field. Enter: recording title, recording artist, or playtime.");
    }
}
