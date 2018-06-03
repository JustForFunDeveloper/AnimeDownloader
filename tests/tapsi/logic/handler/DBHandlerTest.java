package tapsi.logic.handler;

import org.junit.jupiter.api.*;
import tapsi.exception.MyException;

import javax.xml.crypto.Data;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Table: Entries tests")
class DBHandlerTest {

    static DBHandler dbHandler;
    static Locale locale = Locale.getDefault();
    static TimeZone timeZone = TimeZone.getDefault();
    static Calendar calendar = Calendar.getInstance(timeZone, locale);
    static String testedTime;

    @BeforeEach
    @DisplayName("Before every test creates a new DBHandler")
    void setUp() throws MyException {
        dbHandler = new DBHandler();
    }

    @Test
    @DisplayName("Inserts an entry, reads via readAllEntries and asserts it")
    void insertEntry() throws MyException {
        dbHandler.deleteAllEntriesFromTable("Entries");
        testedTime = calendar.getTime().toString();
        dbHandler.insertEntry("name", 1, testedTime);

        List<List<String>> entries = dbHandler.readAllEntries();
        for (List<String> entry : entries) {
            List<String> compare = new ArrayList<>();
            compare.add("name");
            compare.add(Integer.toString(1));
            compare.add(testedTime);
            assertArrayEquals(new List[]{compare}, new List[]{entry});
        }
    }

    @Test
    @DisplayName("Update an entry, reads via readAllEntries and asserts it")
    void updateEntry() throws MyException {
        dbHandler.deleteAllEntriesFromTable("Entries");
        dbHandler.insertEntry("name", 1, testedTime);
        testedTime = calendar.getTime().toString();
        dbHandler.updateEntry("name", 1, testedTime);

        List<List<String>> entries = dbHandler.readAllEntries();
        for (List<String> entry : entries) {
            List<String> compare = new ArrayList<>();
            compare.add("name");
            compare.add(Integer.toString(1));
            compare.add(testedTime);
            assertArrayEquals(new List[]{compare}, new List[]{entry});
        }
        assertEquals(1,entries.size());
    }

    @Test
    @DisplayName("Deletes the table")
    void deleteTable() throws MyException{
        dbHandler.insertEntry("name", 1, testedTime);
        dbHandler.deleteTable("Entries");
    }

    @Test
    @DisplayName("Deletes all Entries from the given table")
    void deleteAllEntriesFromTable() throws MyException{
        dbHandler.insertEntry("name", 1, testedTime);
        dbHandler.insertEntry("name", 1, testedTime);
        dbHandler.deleteAllEntriesFromTable("Entries");
    }

    @AfterAll
    static void closeDB() throws MyException{
        dbHandler.closeDB();
    }
}