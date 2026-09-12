package com.github.weltonaddra.recordings;

import java.util.Comparator;

/**
 * A music recording: title, artist, and playtime.
 *
 * <p>Instances are immutable and validated at construction time.</p>
 */
public class Recording {

    private final String title;
    private final String artist;
    private final int playTimeSeconds;

    public Recording(String title, String artist, int playTimeSeconds) {
        this.title = requireNonBlank(title, "Title");
        this.artist = requireNonBlank(artist, "Artist");
        if (playTimeSeconds <= 0) {
            throw new IllegalArgumentException("Playtime must be greater than zero seconds.");
        }
        this.playTimeSeconds = playTimeSeconds;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getPlayTimeSeconds() {
        return playTimeSeconds;
    }

    /** @return playtime formatted as minutes:seconds, e.g. {@code "3:05"} */
    public String formatPlayTime() {
        return String.format("%d:%02d", playTimeSeconds / 60, playTimeSeconds % 60);
    }

    // ---- ready-made comparators ----------------------------------------

    /** Alphabetical by title, case-insensitive. */
    public static Comparator<Recording> byTitle() {
        return Comparator.comparing(Recording::getTitle, String.CASE_INSENSITIVE_ORDER);
    }

    /** Alphabetical by artist, then by title to break ties. */
    public static Comparator<Recording> byArtist() {
        return Comparator.comparing(Recording::getArtist, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Recording::getTitle, String.CASE_INSENSITIVE_ORDER);
    }

    /** Shortest playtime first. */
    public static Comparator<Recording> byPlaytime() {
        return Comparator.comparingInt(Recording::getPlayTimeSeconds);
    }

    @Override
    public String toString() {
        return String.format("%-32s | %-26s | %s", title, artist, formatPlayTime());
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.strip();
    }
}
