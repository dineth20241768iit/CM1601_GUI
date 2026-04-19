package model;

public class Student {
    private String studentId;
    private String firstName;

    public Student(String studentId, String firstName) {
        this.studentId = studentId;
        this.firstName = firstName;
    }

    // ── Getters ──────────────────────────────────────────────────────
    public String getStudentId()  { return studentId; }
    public String getFirstName()  { return firstName; }

    // ── Validation ───────────────────────────────────────────────────

    public static String validateStudentId(String studentId) {
        if (studentId == null || !studentId.matches("\\d{8}"))
            return "Student ID must be exactly 8 digits.";
        return null;
    }

    public static String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty())
            return "First name cannot be empty.";
        if (!firstName.matches("[A-Za-z]{1,10}"))
            return "First name must be letters only, max 10 characters.";
        return null;
    }

    // ── Serialisation ────────────────────────────────────────────────

    public static Student fromCsvRow(String[] row) {
        return new Student(row[0].trim(), row[1].trim());
    }

    @Override
    public String toString() {
        return studentId + " — " + firstName;
    }
}