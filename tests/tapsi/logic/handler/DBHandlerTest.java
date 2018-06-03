package tapsi.logic.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tapsi.exception.MyException;

import static org.junit.jupiter.api.Assertions.*;

class DBHandlerTest {

    DBHandler dbHandler;

    @BeforeEach
    void setUp() {
        try {
            DBHandler dbHandler = new DBHandler();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertEntry() {
        dbHandler.insertEntry();
    }

    @Test
    void updateEntry() {
    }

    @Test
    void readAllEntries() {
    }
}