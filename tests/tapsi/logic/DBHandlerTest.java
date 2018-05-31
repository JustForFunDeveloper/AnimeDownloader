package tapsi.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DBHandlerTest {

    @Test
    void checkAnimeByName() {
        DBHandler dbHandler = new DBHandler();
        assertTrue(dbHandler.checkAnimeByName("One Piece!"));

    }

    @Test
    void checkPathByID() {
    }
}