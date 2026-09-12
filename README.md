# Recordings-Manager

![CI](https://github.com/weltonaddra/Recordings-Manager/actions/workflows/ci.yml/badge.svg)

A small Java console application that manages a library of recordings (title, artist, playtime) with sorting, search, statistics, and CSV persistence.

Originally written in January 2022 as coursework for an intro Java course (instructor: Udeme Aaron). Rebuilt in 2026 with a proper Maven structure, `Comparator`-based sorting instead of hand-rolled loops, and a JUnit 5 test suite.

## Features

- Add, list, and remove recordings; the library persists between runs as CSV (`data/library.csv` by default)
- Sort by recording title, recording artist, or playtime; alphabetical sorting is case-insensitive and artist ties are broken by title
- Search by artist (case-insensitive, partial matches)
- Statistics: total playtime, average playtime, longest and shortest recordings
- Playtime is displayed as minutes:seconds (337 seconds shows as `5:37`)
- 16 JUnit 5 tests covering the model, sorting, search, statistics, and persistence

## Requirements

- JDK 17 or newer
- Maven 3.8+

## Build, test, run

```bash
mvn clean verify    # compile and run all tests

# optionally pass a data file path
java -cp target/classes com.github.weltonaddra.recordings.RecordingsApp [data-file]
```

## Sample session (abridged from a real run)

```text
===========================
  Recordings Manager
===========================

1) Add recording
2) List all recordings
3) Sort and display
4) Search by artist
5) Statistics
6) Remove a recording
7) Save to file
0) Quit

Choose an option >> 1
Artist >> Miles Davis
Title >> Blue in Green
Playtime (seconds) >> 337
Added 'Blue in Green' by Miles Davis.

... (three more recordings added the same way) ...

Choose an option >> 2

  [1] Blue in Green                    | Miles Davis                | 5:37
  [2] Come As You Are                  | nirvana                    | 3:39
  [3] Hello                            | Adele                      | 4:55
  [4] get lucky                        | Daft Punk                  | 6:09

Choose an option >> 4
Search (part of an artist name) >> miles

1 match(es):
  - Blue in Green                    | Miles Davis                | 5:37

Choose an option >> 5

Statistics
  Recordings        4
  Total playtime    20:20
  Average playtime  5:05
  Longest           get lucky (6:09)
  Shortest          Come As You Are (3:39)

Choose an option >> 3
Sort by:
  - recording title
  - recording artist
  - playtime
Sort field (or 'back') >> playtime

TITLE                            | ARTIST                     | PLAYTIME
------------------------------------------------------------------------
Come As You Are                  | nirvana                    | 3:39
Hello                            | Adele                      | 4:55
Blue in Green                    | Miles Davis                | 5:37
get lucky                        | Daft Punk                  | 6:09

Choose an option >> 0
Saved 4 recording(s) to data/library.csv
Goodbye.
```

Restarting the app picks up where you left off:

```text
Loaded 4 recording(s) from data/library.csv
```

An unrecognized sort field is reported without crashing:

```text
Sort field (or 'back') >> music
  ! Unknown sort field. Enter: recording title, recording artist, or playtime.
```

## Project structure

```text
src
├── main/java/com/github/weltonaddra/recordings
│   ├── Csv.java                CSV encode/decode (quotes, commas)
│   ├── Recording.java          immutable recording type, validation, comparators
│   ├── RecordingLibrary.java   collection with search and statistics
│   ├── RecordingSorter.java    sorting that never mutates the input list
│   ├── RecordingStore.java     save/load the library as CSV
│   ├── RecordingsApp.java      menu-driven entry point
│   └── SortField.java          enum tying user-facing labels to comparators
└── test/java/com/github/weltonaddra/recordings
    ├── RecordingLibraryTest.java
    ├── RecordingSorterTest.java
    ├── RecordingStoreTest.java
    └── RecordingTest.java
```

## Design notes

- Sorting is delegated to `java.util.Comparator` factory methods (`Recording.byTitle()`, `byArtist()`, `byPlaytime()`) instead of hand-written sort loops.
- `RecordingSorter.sortedBy` returns a sorted copy; the caller's list is never mutated.
- `SortField` keeps the user-facing labels and the comparator for each option in one place, and parses input case-insensitively.
- Invalid data cannot be constructed: blank fields and non-positive playtimes are rejected in the constructor with clear messages.
- Statistics and search use streams; an empty library is safe (totals of 0, `Optional.empty()` for longest/shortest).
- Persistence is plain CSV with careful quoting, so titles like `Hello, Goodbye` survive a round trip; load errors report the offending line number.

## Changes from the original coursework version

- Replaced three parallel arrays (`titles[]`, `artists[]`, `playTimes[]`) plus a copy-pasted bubble sort per field with a `Recording` class, a library, and comparators.
- The sort choice is now case-insensitive, invalid input explains what is accepted, and you can keep working until you quit (the original exited after a single sort).
- Playtime prints as minutes:seconds instead of raw seconds.
- Added in v1.1: library management (add/list/remove beyond a fixed five), artist search, statistics, and CSV persistence.
- Added Maven build, JUnit 5 tests, and this README.

## License

No license specified.
