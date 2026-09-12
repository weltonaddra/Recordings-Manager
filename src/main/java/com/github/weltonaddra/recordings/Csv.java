package com.github.weltonaddra.recordings;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV encoding and decoding, used to store records as plain text.
 *
 * <p>Fields are quoted only when they contain a comma, a double quote, or
 * a newline, following the usual CSV conventions (RFC 4180 style).</p>
 */
public final class Csv {

    private Csv() {
        // utility class
    }

    /** Encodes one record as a single CSV line. */
    public static String encode(List<String> fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(encodeField(fields.get(i)));
        }
        return line.toString();
    }

    static String encodeField(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    /** Decodes a single CSV line into its fields, honoring quoted values. */
    public static List<String> decode(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"'); // escaped quote
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        if (inQuotes) {
            throw new IllegalArgumentException("Unterminated quoted field in line: " + line);
        }
        fields.add(field.toString());
        return fields;
    }
}
