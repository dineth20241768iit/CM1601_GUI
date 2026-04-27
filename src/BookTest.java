import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import models.Book;
import models.Student;
import models.Transaction;

public class BookTest {

    @Test
    public void testValidBookId() {
        assertNull(Book.validateBookId("AB12"));
    }

    @Test
    public void testInvalidBookId() {
        assertNotNull(Book.validateBookId("1234"));
    }

    @Test
    public void testValidIsbn() {
        assertNull(Book.validateIsbn("9780306406157"));
    }

    @Test
    public void testInvalidIsbn() {
        assertNotNull(Book.validateIsbn("123"));
    }

    @Test
    public void testValidStudentId() {
        assertNull(Student.validateStudentId("12345678"));
    }

    @Test
    public void testInvalidStudentId() {
        assertNotNull(Student.validateStudentId("123"));
    }

    @Test
    public void testValidDate() {
        assertNull(Transaction.validateDate("18/04/2026"));
    }

    @Test
    public void testInvalidDate() {
        assertNotNull(Transaction.validateDate("31/02/2026"));
    }
}