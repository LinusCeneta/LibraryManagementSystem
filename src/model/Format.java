package model;

public enum Format {
    HARDCOVER,
    PAPERBACK,
    EBOOK,
    AUDIOBOOK;

    public static Format fromString(String formatStr) {
        if (formatStr == null) return null;
        try {
            return Format.valueOf(formatStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}