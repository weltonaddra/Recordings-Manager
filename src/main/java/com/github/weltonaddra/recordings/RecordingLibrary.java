package com.github.weltonaddra.recordings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory collection of recordings with search and summary queries.
 *
 * <p>Insertion order is preserved, which keeps the numbered listing shown
 * by the console app stable for remove operations.</p>
 */
public class RecordingLibrary {

    private final List<Recording> recordings = new ArrayList<>();

    public void add(Recording recording) {
        recordings.add(Objects.requireNonNull(recording, "recording"));
    }

    /** @return an immutable snapshot of every recording */
    public List<Recording> all() {
        return List.copyOf(recordings);
    }

    /** @return every recording whose artist contains {@code text}, case-insensitive */
    public List<Recording> findByArtistContains(String text) {
        String needle = text.strip().toLowerCase();
        return recordings.stream()
                .filter(recording -> recording.getArtist().toLowerCase().contains(needle))
                .toList();
    }

    /** Removes the recording at {@code index} and returns it. */
    public Recording removeAt(int index) {
        if (index < 0 || index >= recordings.size()) {
            throw new IndexOutOfBoundsException("No recording at position " + (index + 1));
        }
        return recordings.remove(index);
    }

    /** @return combined playtime of every recording, in seconds */
    public int totalPlayTimeSeconds() {
        return recordings.stream().mapToInt(Recording::getPlayTimeSeconds).sum();
    }

    /** @return average playtime in seconds, or 0 when the library is empty */
    public double averagePlayTimeSeconds() {
        return recordings.stream().mapToInt(Recording::getPlayTimeSeconds).average().orElse(0.0);
    }

    /** @return the recording with the longest playtime, if any */
    public Optional<Recording> longest() {
        return recordings.stream().max(Comparator.comparingInt(Recording::getPlayTimeSeconds));
    }

    /** @return the recording with the shortest playtime, if any */
    public Optional<Recording> shortest() {
        return recordings.stream().min(Comparator.comparingInt(Recording::getPlayTimeSeconds));
    }

    public int size() {
        return recordings.size();
    }
}
