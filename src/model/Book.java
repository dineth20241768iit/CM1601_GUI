package model;

public record Book(String bookId, String title, String isbn, String author, int copies, int availability,
                   double price) {

    // ── Validation ───────────────────────────────────────────────────

    public static String validateBookId(String bookId) {
        if (bookId == null || !bookId.matches("[A-Za-z]{2}\\d{2}"))
            return "Book ID must be 2 letters followed by 2 digits (e.g. AB12).";
        return null;
    }

    public static String validateIsbn(String isbn) {
        if (isbn == null) return "ISBN cannot be empty.";
        String cleaned = isbn.replace("-", "").replace(" ", "");
        if (!cleaned.matches("\\d{13}"))
            return "ISBN-13 must be exactly 13 digits.";

        int total = 0;
        for (int i = 0; i < 13; i++) {
            int digit = Character.getNumericValue(cleaned.charAt(i));
            total += (i % 2 == 0) ? digit : digit * 3;
        }
        if (total % 10 != 0)
            return "ISBN-13 check digit is invalid.";
        return null;
    }

    public static String validateCopies(int copies) {
        if (copies < 1 || copies > 2)
            return "Copies must be 1 or 2.";
        return null;
    }

    public static String validateAvailability(int availability, int copies) {
        if (availability < 0 || availability > copies)
            return "Availability must be between 0 and " + copies + ".";
        return null;
    }

    // ── Serialisation ────────────────────────────────────────────────

    public String toCsvRow() {
        return String.join(",", bookId, title, isbn, author,
                String.valueOf(copies), String.valueOf(availability),
                String.format("%.2f", price));
    }

    public static Book fromCsvRow(String[] row) {
        return new Book(
                row[0].trim(),
                row[1].trim(),
                row[2].trim(),
                row[3].trim(),
                Integer.parseInt(row[4].trim()),
                Integer.parseInt(row[5].trim()),
                Double.parseDouble(row[6].trim())
        );
    }

    @Override
    public String toString() {
        return bookId + " — " + title;
    }
}