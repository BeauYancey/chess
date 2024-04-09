package dataAccessTests;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataAccess.*;
import exception.ServerException;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DataAccessTests {

    UserDAO userDAO = new SQLUserDAO();
    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO gameDAO = new SQLGameDAO();

    public DataAccessTests() throws DataAccessException {
    }

    @BeforeEach
    public void reset() throws DataAccessException {
        userDAO.removeAll();
        authDAO.removeAll();
        gameDAO.removeAll();

        // Set up databases with data used for the tests
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password, email) " +
                    "values ('test-user', 'test-pass', 'test-email')")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auth (username, token) " +
                    "values ('test-user', 'test-token')")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("DELETE FROM users")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("DELETE FROM games")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Test
    public void getUserTest() throws DataAccessException {
        UserData user = userDAO.getUser("test-user");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.password(), "test-pass");
        Assertions.assertEquals(user.email(), "test-email");
    }

    @Test
    public void getUserFail() throws DataAccessException {
        UserData user = userDAO.getUser("no-user");
        Assertions.assertNull(user);
    }

    @Test
    public void addUserTest() throws DataAccessException {
        UserData newUser = new UserData("test-user-2", "test-pass-2", "test-email-2");
        userDAO.addUser(newUser);

        UserData result = userDAO.getUser("test-user-2");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.password(), "test-pass-2");
        Assertions.assertEquals(result.email(), "test-email-2");
    }

    @Test
    public void addUserFail() {
        try {
            UserData newUser = new UserData(null, null, null);
            userDAO.addUser(newUser);
            Assertions.fail();
        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("null"));
        }
    }

    @Test
    public void removeAllUsersTest() throws DataAccessException {
        userDAO.removeAll();
        UserData testUser = userDAO.getUser("test-user");
        Assertions.assertNull(testUser);
    }

    @Test
    public void removeAllUsersEmpty() throws DataAccessException {
        userDAO.removeAll();
        UserData testUser = userDAO.getUser("test-user");
        Assertions.assertNull(testUser);
        
        userDAO.removeAll();
        testUser = userDAO.getUser("test-user");
        Assertions.assertNull(testUser);
    }

    @Test
    public void getAuthTest() throws DataAccessException {
        AuthData auth = authDAO.getAuth("test-token");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(auth.userName(), "test-user");
        Assertions.assertEquals(auth.authToken(), "test-token");
    }

    @Test
    public void getAuthFail() throws DataAccessException {
        AuthData auth = authDAO.getAuth("no-token");
        Assertions.assertNull(auth);
    }

    @Test
    public void addAuthTest() throws DataAccessException {
        AuthData auth = new AuthData("test-token-2", "test-user-2");
        authDAO.addAuth(auth);

        AuthData result = authDAO.getAuth("test-token-2");
        Assertions.assertEquals(auth, result);
    }

    @Test
    public void addAuthFail() {
        try {
            AuthData auth = new AuthData(null, null);
            authDAO.addAuth(auth);
            Assertions.fail();
        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("null"));
        }
    }

    @Test
    public void removeAuthTest() throws DataAccessException {
        authDAO.removeAuth("test-token");
        AuthData auth = authDAO.getAuth("test-token");
        Assertions.assertNull(auth);
    }

    @Test
    public void removeAuthFail() {
        try {
            authDAO.removeAuth("no-token");
            Assertions.fail();
        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("affected 0 rows"));
        }
    }

    @Test
    public void removeAllAuthsTest() throws DataAccessException {
        authDAO.addAuth(new AuthData("token2", "user2"));
        authDAO.addAuth(new AuthData("token3", "user3"));
        authDAO.addAuth(new AuthData("token4", "user4"));

        authDAO.removeAll();

        AuthData auth = authDAO.getAuth("test-token");
        Assertions.assertNull(auth);
        auth = authDAO.getAuth("token2");
        Assertions.assertNull(auth);
        auth = authDAO.getAuth("token3");
        Assertions.assertNull(auth);
        auth = authDAO.getAuth("token4");
        Assertions.assertNull(auth);
    }

    @Test
    public void createGameTest() throws DataAccessException {
        int gameID = gameDAO.createGame("test-game-name", new ChessGame());
        int gameID2 = gameDAO.createGame("test-game-name", new ChessGame());
        Assertions.assertNotEquals(0, gameID);
        Assertions.assertNotEquals(0, gameID2);
        Assertions.assertNotEquals(gameID2, gameID, "games created with the same name return the same ID");
    }

    @Test
    public void createGameFail() {
        try {
            int gameID = gameDAO.createGame(null, null);
            Assertions.fail();
        }
        catch (DataAccessException ex) {
            Assertions.assertTrue(ex.getMessage().contains("null"));
        }
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        gameDAO.createGame("test-game-1", new ChessGame());
        gameDAO.createGame("test-game-2", new ChessGame());
        gameDAO.createGame("test-game-3", new ChessGame());

        Assertions.assertEquals(3, gameDAO.listAll().size());
    }

    @Test
    public void listNoGamesTest() throws DataAccessException {
        gameDAO.removeAll();
        Assertions.assertEquals(0, gameDAO.listAll().size());
    }

    @Test
    public void joinGameTest() throws DataAccessException, ServerException {
        int id = gameDAO.createGame("test-game", new ChessGame());
        gameDAO.joinGame(id, "test-user", "white");

        Assertions.assertEquals(1, gameDAO.listAll().size());
        GameData gameData = gameDAO.listAll().getFirst();
        Assertions.assertEquals("test-user", gameData.whiteUsername());
        Assertions.assertNull(gameData.blackUsername());
    }

    @Test
    public void joinGame403() throws DataAccessException {
        try {
            int id = gameDAO.createGame("test-game", new ChessGame());
            gameDAO.joinGame(id, "test-user", "white");
            gameDAO.joinGame(id, "test-user-2", "white");
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(ex.getStatus(), 403);
            Assertions.assertEquals(1, gameDAO.listAll().size());
            GameData gameData = gameDAO.listAll().getFirst();
            Assertions.assertEquals("test-user", gameData.whiteUsername());
            Assertions.assertNull(gameData.blackUsername());
        }
    }
    @Test
    public void joinGame400() throws DataAccessException {
        try {
            int id = gameDAO.createGame("test-game", new ChessGame());
            gameDAO.joinGame(id-1, "test-user", "white");
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(ex.getStatus(), 400);
            Assertions.assertEquals(1, gameDAO.listAll().size());
            GameData gameData = gameDAO.listAll().getFirst();
            Assertions.assertNull(gameData.whiteUsername());
            Assertions.assertNull(gameData.blackUsername());
        }
    }
}
