# Recordings-Manager

![CI](https://github.com/weltonaddra/Recordings-Manager/actions/workflows/ci.yml/badge.svg)

A small Java console application that collects recordings (title, artist, playtime) and sorts them by title, artist, or playtime.

Originally written in January 2022 as coursework for an intro Java course (instructor: Udeme Aaron). Rebuilt in 2026 with a proper Maven structure, `Comparator`-based sorting instead of hand-rolled loops, and a JUnit 5 test suite.

## Features

- Enter five recordings with validated input: no blank titles or artists, playtime must be a positive number of seconds
- Sort on demand by recording title, recording artist, or playtime, and re-sort as many times as you like without restarting
- Alphabetical sorting is case-insensitive; artist sorting breaks ties by title
- Playtime is displayed as minutes:seconds (337 seconds shows as `5:37`)
- 9 JUnit 5 tests covering the model, sorting, and input parsing

## Requirements

- JDK 17 or newer
- Maven 3.8+

## Build, test, run

```bash
mvn clean verify    # compile and run all tests

java -cp target/classes com.github.weltonaddra.recordings.RecordingsApp
```

## Sample session (abridged from a real run)

```text
===========================
  Recordings Manager
===========================
Enter 5 recordings.

Recording 1 of 5
Artist >> Miles Davis
Title >> Blue in Green
Playtime (seconds) >> 337

Recording 2 of 5
Artist >> nirvana
Title >> Come As You Are
Playtime (seconds) >> 219

... (three more recordings entered the same way) ...

How would you like to sort these recordings?
  - recording title
  - recording artist
  - playtime
Sort by (or 'quit') >> recording artist

TITLE                            | ARTIST                     | PLAYTIME
------------------------------------------------------------------------
Hello                            | Adele                      | 4:55
get lucky                        | daft punk                  | 6:09
Blue in Green                    | Miles Davis                | 5:37
So What                          | Miles Davis                | 9:05
Come As You Are                  | nirvana                    | 3:39

How would you like to sort these recordings?
  - recording title
  - recording artist
  - playtime
Sort by (or 'quit') >> quit

Goodbye.
```

An unrecognized sort field is reported without crashing:

```text
Sort by (or 'quit') >> music
  ! Unknown sort field. Enter: recording title, recording artist, or playtime.
```

## Project structure

```text
src
├── main/java/com/github/weltonaddra/recordings
│   ├── Recording.java         immutable recording type, validation, comparators
│   ├── RecordingsApp.java     console entry point
│   ├── RecordingSorter.java   sorting that never mutates the input list
│   └── SortField.java         enum tying user-facing labels to comparators
└── test/java/com/github/weltonaddra/recordings
    ├── RecordingSorterTest.java
    └── RecordingTest.java
```

## Design notes

- Sorting is delegated to `java.util.Comparator` factory methods (`Recording.byTitle()`, `byArtist()`, `byPlaytime()`) instead of hand-written sort loops.
- `RecordingSorter.sortedBy` returns a sorted copy; the caller's list is never mutated.
- `SortField` keeps the user-facing labels and the comparator for each option in one place, and parses input case-insensitively.
- Invalid data cannot be constructed: blank fields and non-positive playtimes are rejected in the constructor with clear messages.

## Changes from the original coursework version

- Replaced three parallel arrays (`titles[]`, `artists[]`, `playTimes[]`) plus a copy-pasted bubble sort per field with a `Recording` class, an `ArrayList`, and comparators.
- The sort choice is now case-insensitive, invalid input explains what is accepted, and you can keep re-sorting until you quit (the original exited after a single sort).
- Playtime prints as minutes:seconds instead of raw seconds.
- Added Maven build, JUnit 5 tests, and this README.

## License

No license specified.
