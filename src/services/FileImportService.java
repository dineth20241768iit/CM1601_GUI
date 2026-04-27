package services;

import model.Book;
import model.Student;
import model.Transaction;

import java.io.*;
import java.util.*;

public class FileImportService {

    // Holds imported data
    private final List<Book> books               = new ArrayList<>();
    private final List<Student> students         = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    // Holds validation errors per file: each entry is {lineNumber, rawLine, errorMessage}
    private final List<String[]> bookErrors        = new ArrayList<>();
    private final List<String[]> studentErrors     = new ArrayList<>();
    private final List<String[]> transactionErrors = new ArrayList<>();

    // ── Getters ──────────────────────────────────────────────────────
    public List<Book>        getBooks()            { return books; }
    public List<Student>     getStudents()          { return students; }
    public List<Transaction> getTransactions()      { return transactions; }
    public List<String[]>    getBookErrors()        { return bookErrors; }
    public List<String[]>    getStudentErrors()     { return studentErrors; }
    public List<String[]>    getTransactionErrors() { return transactionErrors; }

    // ── Import all three files ────────────────────────────────────────

    public void importAll(String bookPath, String studentPath, String transactionPath) {
        books.clear();
        students.clear();
        transactions.clear();
        bookErrors.clear();
        studentErrors.clear();
        transactionErrors.clear();

        importBooks(bookPath);
        importStudents(studentPath);
        importTransactions(transactionPath);
    }

    // ── Book import ───────────────────────────────────────────────────

    private void importBooks(String path) {
        List<String[]> rows = readCsv(path);
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            String lineNum = String.valueOf(i + 1);
            String raw = String.join(",", row);

            if (row.length < 7) {
                bookErrors.add(new String[]{lineNum, raw, "Incomplete row — expected 7 columns."});
                continue;
            }

            List<String> errors = new ArrayList<>();

            // Validate each field
            String idErr = Book.validateBookId(row[0].trim());
            if (idErr != null) errors.add(idErr);

            String isbnErr = Book.validateIsbn(row[2].trim());
            if (isbnErr != null) errors.add(isbnErr);

            int copies = -1, availability = -1;
            try { copies = Integer.parseInt(row[4].trim()); } catch (NumberFormatException e) { errors.add("Copies is not a number."); }
            try { availability = Integer.parseInt(row[5].trim()); } catch (NumberFormatException e) { errors.add("Availability is not a number."); }

            if (copies != -1) {
                String copErr = Book.validateCopies(copies);
                if (copErr != null) errors.add(copErr);
            }
            if (copies != -1 && availability != -1) {
                String avErr = Book.validateAvailability(availability, copies);
                if (avErr != null) errors.add(avErr);
            }

            if (!errors.isEmpty()) {
                bookErrors.add(new String[]{lineNum, raw, String.join(" | ", errors)});
            } else {
                try {
                    books.add(Book.fromCsvRow(row));
                } catch (Exception e) {
                    bookErrors.add(new String[]{lineNum, raw, "Failed to parse row: " + e.getMessage()});
                }
            }
        }
    }

    // ── Student import ────────────────────────────────────────────────

    private void importStudents(String path) {
        List<String[]> rows = readCsv(path);
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            String lineNum = String.valueOf(i + 1);
            String raw = String.join(",", row);

            if (row.length < 2) {
                studentErrors.add(new String[]{lineNum, raw, "Incomplete row — expected 2 columns."});
                continue;
            }

            List<String> errors = new ArrayList<>();

            String idErr = Student.validateStudentId(row[0].trim());
            if (idErr != null) errors.add(idErr);

            String nameErr = Student.validateFirstName(row[1].trim());
            if (nameErr != null) errors.add(nameErr);

            if (!errors.isEmpty()) {
                studentErrors.add(new String[]{lineNum, raw, String.join(" | ", errors)});
            } else {
                try {
                    students.add(Student.fromCsvRow(row));
                } catch (Exception e) {
                    studentErrors.add(new String[]{lineNum, raw, "Failed to parse row: " + e.getMessage()});
                }
            }
        }
    }

    // ── Transaction import ────────────────────────────────────────────

    private void importTransactions(String path) {
        List<String[]> rows = readCsv(path);
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            String lineNum = String.valueOf(i + 1);
            String raw = String.join(",", row);

            if (row.length < 4) {
                transactionErrors.add(new String[]{lineNum, raw, "Incomplete row — expected 4 columns."});
                continue;
            }

            List<String> errors = new ArrayList<>();

            String dateErr = Transaction.validateDate(row[0].trim());
            if (dateErr != null) errors.add(dateErr);

            int type;
            try {
                type = Integer.parseInt(row[3].trim());
                String typeErr = Transaction.validateTransactionType(type);
                if (typeErr != null) errors.add(typeErr);
            } catch (NumberFormatException e) {
                errors.add("Transaction type must be 1 or 2.");
            }

            if (!errors.isEmpty()) {
                transactionErrors.add(new String[]{lineNum, raw, String.join(" | ", errors)});
            } else {
                try {
                    transactions.add(Transaction.fromCsvRow(row));
                } catch (Exception e) {
                    transactionErrors.add(new String[]{lineNum, raw, "Failed to parse row: " + e.getMessage()});
                }
            }
        }
    }

    // ── CSV reader ────────────────────────────────────────────────────

    private List<String[]> readCsv(String path) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return rows;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path + " — " + e.getMessage());
        }
        return rows;
    }

    // ── Correct an invalid row ────────────────────────────────────────

    public void correctBookError(int errorIndex, String correctedLine) {
        String[] row = correctedLine.split(",", -1);
        bookErrors.remove(errorIndex);
        try {
            books.add(Book.fromCsvRow(row));
        } catch (Exception e) {
            System.err.println("Could not parse corrected book row: " + e.getMessage());
        }
    }

    public void correctStudentError(int errorIndex, String correctedLine) {
        String[] row = correctedLine.split(",", -1);
        studentErrors.remove(errorIndex);
        try {
            students.add(Student.fromCsvRow(row));
        } catch (Exception e) {
            System.err.println("Could not parse corrected student row: " + e.getMessage());
        }
    }

    public void correctTransactionError(int errorIndex, String correctedLine) {
        String[] row = correctedLine.split(",", -1);
        transactionErrors.remove(errorIndex);
        try {
            transactions.add(Transaction.fromCsvRow(row));
        } catch (Exception e) {
            System.err.println("Could not parse corrected transaction row: " + e.getMessage());
        }
    }
}