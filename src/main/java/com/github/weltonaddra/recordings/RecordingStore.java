package com.github.weltonaddra.recordings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads a recording library as a CSV file with the columns
 * {@code title, artist, playTimeSeconds}.
 */
public final class RecordingStore {

    public static final String HEADER = "title,artist,playTimeSeconds";

    private RecordingStore() {
        // utility class
    }

    /** Writes every recording to {@code file}, creating parent directories as needed. */
    public static void save(Path file, List<Recording> recordings) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Recording recording : recordings) {
            lines.add(Csv.encode(List.of(
                    recording.getTitle(),
                    recording.getArtist(),
                    String.valueOf(recording.getPlayTimeSeconds()))));
        }
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, lines);
    }

    /**
     * Reads recordings from {@code file}. Missing file returns an empty list.
     *
     * @throws IllegalArgumentException if any line is malformed (with the line number)
     */
    public static List<Recording> load(Path file) throws IOException {
        List<Recording> recordings = new ArrayList<>();
        if (!Files.exists(file)) {
            return recordings;
        }
        List<String> lines = Files.readAllLines(file);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty() || (i == 0 && line.equals(HEADER))) {
                continue;
            }
            recordings.add(recordingFrom(Csv.decode(line), i + 1));
        }
        return recordings;
    }

    private static Recording recordingFrom(List<String> fields, int lineNumber) {
        if (fields.size() != 3) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": expected 3 fields, found " + fields.size());
        }
        try {
            int seconds = Integer.parseInt(fields.get(2).strip());
            return new Recording(fields.get(0), fields.get(1), seconds);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": invalid playtime '" + fields.get(2) + "'");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }
}
