package com.github.weltonaddra.recordings;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console front-end: collect five recordings, then sort them by title,
 * artist, or playtime on demand.
 *
 * <p>Run with:
 * {@code java -cp target/classes com.github.weltonaddra.recordings.RecordingsApp}</p>
 */
public class RecordingsApp {

    private static final int RECORDING_COUNT = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Recording> recordings = new ArrayList<>();

        System.out.println("===========================");
        System.out.println("  Recordings Manager");
        System.out.println("===========================");
        System.out.printf("Enter %d recordings.%n", RECORDING_COUNT);

        for (int i = 1; i <= RECORDING_COUNT; i++) {
            System.out.println();
            System.out.println("Recording " + i + " of " + RECORDING_COUNT);
            String artist = readNonBlank(scanner, "Artist");
            String title = readNonBlank(scanner, "Title");
            int seconds = readPositiveInt(scanner, "Playtime (seconds)");
            recordings.add(new Recording(title, artist, seconds));
        }

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("How would you like to sort these recordings?");
            System.out.println("  - recording title");
            System.out.println("  - recording artist");
            System.out.println("  - playtime");
            System.out.print("Sort by (or 'quit') >> ");
            String input = scanner.nextLine().strip();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q")) {
                running = false;
                continue;
            }
            try {
                SortField field = SortField.fromInput(input);
                printTable(RecordingSorter.sortedBy(recordings, field));
            } catch (IllegalArgumentException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("Goodbye.");
    }

    private static void printTable(List<Recording> sorted) {
        System.out.println();
        System.out.printf("%-32s | %-26s | %s%n", "TITLE", "ARTIST", "PLAYTIME");
        System.out.println("-".repeat(72));
        sorted.forEach(System.out::println);
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
}
