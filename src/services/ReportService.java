package services;

import model.Book;
import model.Student;
import model.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private List<Book>        books;
    private List<Student>     students;
    private List<Transaction> transactions;

    public ReportService(List<Book> books, List<Student> students, List<Transaction> transactions) {
        this.books        = books;
        this.students     = students;
        this.transactions = transactions;
    }

    // ── Books issued report ───────────────────────────────────────────

    // Returns all transactions of type ISSUE for a given date string (DD/MM/YYYY)
    public List<Transaction> getIssuedOnDate(String date) {
        return transactions.stream()
                .filter(t -> t.isIssue() && t.getDate().equals(date))
                .collect(Collectors.toList());
    }

    // Enriches issued transactions with book title and student name for display
    public List<String[]> getIssuedReportRows(String date) {
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : getIssuedOnDate(date)) {
            String title = books.stream()
                    .filter(b -> b.getBookId().equalsIgnoreCase(t.getBookId()))
                    .map(Book::getTitle)
                    .findFirst().orElse("Unknown");

            String name = students.stream()
                    .filter(s -> s.getStudentId().equals(t.getStudentId()))
                    .map(Student::getFirstName)
                    .findFirst().orElse("Unknown");

            rows.add(new String[]{t.getDate(), t.getBookId(), title, t.getStudentId(), name});
        }
        return rows;
    }

    // ── Average cost ──────────────────────────────────────────────────

    public double getAverageCost() {
        if (books.isEmpty()) return 0.0;
        double total = books.stream().mapToDouble(Book::getPrice).sum();
        return total / books.size();
    }

    // ── Availability search ───────────────────────────────────────────

    // Supports wildcard * at the end of the query (e.g. "Jav*" matches "Java", "JavaScript")
    public List<Book> searchByTitle(String query) {
        if (query == null || query.isEmpty()) return new ArrayList<>(books);

        if (query.endsWith("*")) {
            String prefix = query.substring(0, query.length() - 1).toLowerCase();
            return books.stream()
                    .filter(b -> b.getTitle().toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }

        return books.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(query))
                .collect(Collectors.toList());
    }

    // ── Export search results ─────────────────────────────────────────

    public boolean exportSearchResults(List<Book> results, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("Book ID, Title, ISBN, Author, Copies, Availability, Price");
            writer.newLine();
            for (Book b : results) {
                writer.write(b.toCsvRow());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            return false;
        }
    }
}