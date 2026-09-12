package com.github.weltonaddra.recordings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven console front-end: add, list, sort, search, and remove
 * recordings, view statistics, and save/load the library to CSV.
 *
 * <p>Run with:
 * {@code java -cp target/classes com.github.weltonaddra.recordings.RecordingsApp [data-file]}</p>
 */
public class RecordingsApp {

    private static final Path DEFAULT_DATA_FILE = Path.of("data", "library.csv");

    public static void main(String[] args) {
        Path dataFile = args.length > 0 ? Path.of(args[0]) : DEFAULT_DATA_FILE;

        Scanner scanner = new Scanner(System.in);
        RecordingLibrary library = loadLibrary(dataFile);

        System.out.println("===========================");
        System.out.println("  Recordings Manager");
        System.out.println("===========================");
        if (library.size() > 0) {
            System.out.printf("Loaded %d recording(s) from %s%n", library.size(), dataFile);
        }

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Add recording");
            System.out.println("2) List all recordings");
            System.out.println("3) Sort and display");
            System.out.println("4) Search by artist");
            System.out.println("5) Statistics");
            System.out.println("6) Remove a recording");
            System.out.println("7) Save to file");
            System.out.println("0) Quit");

            int choice = readInt(scanner, "Choose an option", 0, 7);
            switch (choice) {
                case 1 -> addRecording(scanner, library);
                case 2 -> listAll(library);
                case 3 -> sortAndDisplay(scanner, library);
                case 4 -> searchByArtist(scanner, library);
                case 5 -> showStatistics(library);
                case 6 -> removeRecording(scanner, library);
                case 7 -> saveLibrary(library, dataFile);
                case 0 -> running = false;
                default -> throw new IllegalStateException("Unexpected option: " + choice);
            }
        }
        saveLibrary(library, dataFile);
        System.out.println("Goodbye.");
    }

    // ---- actions ---------------------------------------------------------

    private static void addRecording(Scanner scanner, RecordingLibrary library) {
        System.out.println();
        String artist = readNonBlank(scanner, "Artist");
        String title = readNonBlank(scanner, "Title");
        int seconds = readPositiveInt(scanner, "Playtime (seconds)");
        library.add(new Recording(title, artist, seconds));
        System.out.println("Added '" + title + "' by " + artist + ".");
    }

    private static void listAll(RecordingLibrary library) {
        System.out.println();
        if (library.size() == 0) {
            System.out.println("The library is empty. Add a recording first.");
            return;
        }
        List<Recording> all = library.all();
        for (int i = 0; i < all.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, all.get(i));
        }
    }

    private static void sortAndDisplay(Scanner scanner, RecordingLibrary library) {
        System.out.println();
        if (library.size() == 0) {
            System.out.println("The library is empty. Add a recording first.");
            return;
        }
        System.out.println("Sort by:");
        System.out.println("  - recording title");
        System.out.println("  - recording artist");
        System.out.println("  - playtime");
        while (true) {
            System.out.print("Sort field (or 'back') >> ");
            String input = scanner.nextLine().strip();
            if (input.equalsIgnoreCase("back")) {
                return;
            }
            try {
                SortField field = SortField.fromInput(input);
                printTable(RecordingSorter.sortedBy(library.all(), field));
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
    }

    private static void searchByArtist(Scanner scanner, RecordingLibrary library) {
        System.out.println();
        if (library.size() == 0) {
            System.out.println("The library is empty. Add a recording first.");
            return;
        }
        String term = readNonBlank(scanner, "Search (part of an artist name)");
        List<Recording> matches = library.findByArtistContains(term);
        System.out.println();
        if (matches.isEmpty()) {
            System.out.println("No matches for '" + term + "'.");
        } else {
            System.out.println(matches.size() + " match(es):");
            matches.forEach(recording -> System.out.println("  - " + recording));
        }
    }

    private static void showStatistics(RecordingLibrary library) {
        System.out.println();
        if (library.size() == 0) {
            System.out.println("The library is empty. Add a recording first.");
            return;
        }
        System.out.println("Statistics");
        System.out.printf("  Recordings        %d%n", library.size());
        System.out.printf("  Total playtime    %s%n", formatDuration(library.totalPlayTimeSeconds()));
        System.out.printf("  Average playtime  %s%n",
                formatDuration((int) Math.round(library.averagePlayTimeSeconds())));
        library.longest().ifPresent(longest ->
                System.out.printf("  Longest           %s (%s)%n", longest.getTitle(), longest.formatPlayTime()));
        library.shortest().ifPresent(shortest ->
                System.out.printf("  Shortest          %s (%s)%n", shortest.getTitle(), shortest.formatPlayTime()));
    }

    private static void removeRecording(Scanner scanner, RecordingLibrary library) {
        System.out.println();
        if (library.size() == 0) {
            System.out.println("The library is empty. Add a recording first.");
            return;
        }
        listAll(library);
        int index = readInt(scanner, "Enter the number shown for the recording to remove",
                1, library.size()) - 1;
        Recording recording = library.all().get(index);
        if (readYesNo(scanner, "Remove '" + recording.getTitle() + "' by " + recording.getArtist() + "?")) {
            library.removeAt(index);
            System.out.println("Removed '" + recording.getTitle() + "'.");
        } else {
            System.out.println("Nothing was removed.");
        }
    }

    private static void printTable(List<Recording> sorted) {
        System.out.println();
        System.out.printf("%-32s | %-26s | %s%n", "TITLE", "ARTIST", "PLAYTIME");
        System.out.println("-".repeat(72));
        sorted.forEach(System.out::println);
    }

    // ---- persistence -----------------------------------------------------

    private static RecordingLibrary loadLibrary(Path dataFile) {
        RecordingLibrary library = new RecordingLibrary();
        if (!Files.exists(dataFile)) {
            return library;
        }
        try {
            RecordingStore.load(dataFile).forEach(library::add);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not load " + dataFile + ": " + e.getMessage());
        }
        return library;
    }

    private static void saveLibrary(RecordingLibrary library, Path dataFile) {
        try {
            RecordingStore.save(dataFile, library.all());
            System.out.printf("%nSaved %d recording(s) to %s%n", library.size(), dataFile);
        } catch (IOException e) {
            System.out.println("Could not save to " + dataFile + ": " + e.getMessage());
        }
    }

    // ---- input helpers ---------------------------------------------------

    private static String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%d:%02d", minutes, secs);
    }

    private static String readNonBlank(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " >> ");
            String value = scanner.nextLine().strip();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("  ! Please enter a value.");
        }
    }

    private static int readInt(Scanner scanner, String label, int min, int max) {
        while (true) {
            System.out.print(label + " >> ");
            String line = scanner.nextLine().strip();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("  ! Enter a whole number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  ! That is not a whole number.");
            }
        }
    }

    private static int readPositiveInt(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " >> ");
            String line = scanner.nextLine().strip();
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    System.out.println("  ! Playtime must be greater than zero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  ! That is not a whole number.");
            }
        }
    }

    private static boolean readYesNo(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + " (yes/no) >> ");
            String value = scanner.nextLine().strip();
            if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y")) {
                return true;
            }
            if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("  ! Please answer yes or no.");
        }
    }
}
