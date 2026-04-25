package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Transaction {
    public static final int TYPE_ISSUE  = 1;
    public static final int TYPE_RETURN = 2;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String date;
    private final String bookId;
    private final String studentId;
    private final int transactionType;

    public Transaction(String date, String bookId, String studentId, int transactionType) {
        this.date            = date;
        this.bookId          = bookId;
        this.studentId       = studentId;
        this.transactionType = transactionType;
    }

    // ── Getters ──────────────────────────────────────────────────────
    public String getDate()           { return date; }
    public String getBookId()         { return bookId; }
    public String getStudentId()      { return studentId; }

    // ── Helpers ──────────────────────────────────────────────────────
    public boolean isIssue()  { return transactionType == TYPE_ISSUE; }

    public String getTypeLabel() { return isIssue() ? "Issue" : "Return"; }

    // ── Validation ───────────────────────────────────────────────────

    public static String validateDate(String date) {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}"))
            return "Date must be in format DD/MM/YYYY.";
        try {
            LocalDate.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            return "Date is not a valid calendar date.";
        }
        return null;
    }

    public static String validateTransactionType(int type) {
        if (type != TYPE_ISSUE && type != TYPE_RETURN)
            return "Transaction type must be 1 (issue) or 2 (return).";
        return null;
    }

    // ── Serialisation ────────────────────────────────────────────────

    public static Transaction fromCsvRow(String[] row) {
        return new Transaction(
                row[0].trim(),
                row[1].trim(),
                row[2].trim(),
                Integer.parseInt(row[3].trim())
        );
    }

    @Override
    public String toString() {
        return date + " | " + bookId + " | " + studentId + " | " + getTypeLabel();
    }
}