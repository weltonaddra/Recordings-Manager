package com.github.weltonaddra.recordings;

import java.util.ArrayList;
import java.util.List;

/**
 * Sorting operations on collections of recordings.
 */
public final class RecordingSorter {

    private RecordingSorter() {
        // utility class
    }

    /**
     * @return a new list containing the given recordings, sorted by the
     *         chosen field. The input list is never modified.
     */
    public static List<Recording> sortedBy(List<Recording> recordings, SortField field) {
        List<Recording> copy = new ArrayList<>(recordings);
        copy.sort(field.comparator());
        return copy;
    }
}
