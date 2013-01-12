package edu.depaul.se.jpa.basic;

import edu.depaul.se.jdbc.basic.BookDAO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Demo to show JUnit
 */
public class BookTest {

    public BookTest() {
    }
    // ======================================
    // =             Attributes             =
    // ======================================
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static EntityTransaction tx;

    // ======================================
    // =          Lifecycle Methods         =
    // ======================================
    @BeforeClass
    public static void setUpClass() throws Exception {
        emf = Persistence.createEntityManagerFactory("jpa-demoPU");
        em = emf.createEntityManager();
    }

    @BeforeClass
    public static void beforeClass() {
        String connectionUrl = "jdbc:hsqldb:mem:.";
        String userName = "";
        String password = "";

        Connection con;
        try {
            con = DriverManager.getConnection(connectionUrl, userName, password);
            // Step 3:  Create a statement where the SQL statement will be provided
            Statement stmt = con.createStatement();
            stmt.executeUpdate("create table BOOK (ID integer GENERATED BY DEFAULT AS IDENTITY(START WITH 100) PRIMARY KEY, NAME VARCHAR(50))");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        em.close();
        emf.close();
    }

    @Before
    public void initTransaction() {
        tx = em.getTransaction();
    }

    @Test
    public void testBookCRUD() throws SQLException {
        Book book = new Book();
        book.setTitle("Beginning Java Persistence");
        book.setPrice((float) 49.99);
        book.setIllustrations(false);

        tx.begin();
        em.persist(book);
        tx.commit();
        assertNotNull("ID should have been generated and populated after persist",
                book.getId());

        List<Book> books =
                em.createNamedQuery("findAllBooks").getResultList();
        assertEquals(1, books.size());

        tx = em.getTransaction();
        tx.begin();
        book = em.find(Book.class, new Long(1));
        book.setTitle(book.getTitle() + " 2nd edition");
        tx.commit();

        Book updatedBook =
                em.createNamedQuery("findAllBooks", Book.class).getSingleResult();
        assertEquals(book, updatedBook);

        int priorCount = em.createNamedQuery("findAllBooks").getResultList().size();
        tx = em.getTransaction();
        tx.begin();
        book = em.find(Book.class, new Long(1));
        em.remove(book);
        tx.commit();

        int afterCount = em.createNamedQuery("findAllBooks").getResultList().size();

        assertEquals(afterCount + 1, priorCount);
        
        BookDAO dao = new BookDAO();
        int countFromJDBC = dao.getAllBooks().size();
        assertEquals(afterCount, countFromJDBC);
    }
}
