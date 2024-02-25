package dataAccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MemoryDAOTest {
    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;

    @BeforeEach
    public void reset() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
    }

    @Test
    public void addOneAuthTest() {
        AuthData auth = new AuthData("test-token", "test-user");
        authDAO.addAuth(auth);

        ArrayList<AuthData> expecetd = new ArrayList<>();
        expecetd.add(auth);

        Assertions.assertEquals(expecetd, authDAO.listAll());
    }

    @Test
    public void addOneUserTest() {
        UserData user = new UserData("test-user", "test-password", "test-email");
        userDAO.addUser(user);

        ArrayList<UserData> expected = new ArrayList<>();
        expected.add(user);

        Assertions.assertEquals(expected, userDAO.listAll());
    }

    @Test
    public void addOneGameTest() {
        GameData game = new GameData(1, "white", "black", "name",
                new ChessGame());
        gameDAO.createGame(game);

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(game);

        Assertions.assertEquals(expected, gameDAO.listAll());
    }

    @Test
    public void addManyUsersTest() {
        UserData user1 = new UserData("user1", "pass1", "email1");
        UserData user2 = new UserData("user2", "pass2", "email2");
        UserData user3 = new UserData("user3", "pass3", "email3");

        userDAO.addUser(user1);
        userDAO.addUser(user2);
        userDAO.addUser(user3);

        ArrayList<UserData> expected = new ArrayList<>();
        expected.add(user1);
        expected.add(user2);
        expected.add(user3);

        Assertions.assertEquals(expected, userDAO.listAll());
    }

    @Test
    public void removeAuthTest() {
        AuthData auth1 = new AuthData("token1", "user1");
        AuthData auth2 = new AuthData("token2", "user2");

        authDAO.addAuth(auth1);
        authDAO.addAuth(auth2);
        authDAO.removeAuth("token1");

        ArrayList<AuthData> expected = new ArrayList<>();
        expected.add(auth2);

        Assertions.assertEquals(expected, authDAO.listAll());
    }

    @Test
    public void getUserTest() {
        UserData user = new UserData("test-user", "test-password", "test-email");
        userDAO.addUser(user);

        Assertions.assertEquals(user, userDAO.getUser("test-user"));
        Assertions.assertNull(userDAO.getUser("no-user-with-this-username"));
    }

}
