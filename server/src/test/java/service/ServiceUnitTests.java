package service;

import chess.ChessGame;
import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestResponse.*;
import service.exception.*;

import java.util.ArrayList;

public class ServiceUnitTests {
    MemoryAuthDAO authDAO;
    MemoryUserDAO userDAO;
    MemoryGameDAO gameDAO;

    @BeforeEach
    public void setup() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("test-user", "test-pass", "test@mail.com");
        RegisterResponse res = null;
        try {
            res = UserService.register(req, authDAO, userDAO);
        }
        catch (Exception e) {}

        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.username(), "test-user");
        Assertions.assertNotNull(res.authToken());
    }

    @Test
    public void testRegister400() {
        RegisterRequest req = new RegisterRequest("test-user", null, null);
        RegisterResponse res = null;
        try {
            res = UserService.register(req, authDAO, userDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception400.class);
        }
        Assertions.assertNull(res);
    }

    @Test
    public void testRegister403() {
        RegisterRequest req = new RegisterRequest("test-user", "test-pass", "test@mail.com");
        try {
            UserService.register(req, authDAO, userDAO);
        }
        catch (Exception e) {}

        RegisterRequest req2 = new RegisterRequest("test-user", "test-pass", "test@mail.com");
        RegisterResponse res = null;
        try {
            res = UserService.register(req2, authDAO, userDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception403.class);
        }
        Assertions.assertNull(res);
    }

    @Test
    public void testLoginSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("test-user", "test-pass",
                "test@mail.com");
        try {
            UserService.register(registerRequest, authDAO, userDAO);
        }
        catch (Exception e) {}

        LoginRequest req = new LoginRequest("test-user", "test-pass");
        LoginResponse res = null;
        try {
            res = UserService.login(req, authDAO, userDAO);
        }
        catch (Exception e) {}

        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.username(), "test-user");
        Assertions.assertNotNull(res.authToken());
    }

    @Test
    public void testLogin401() {
        RegisterRequest registerRequest = new RegisterRequest("test-user", "test-pass",
                "test@mail.com");
        try {
            UserService.register(registerRequest, authDAO, userDAO);
        }
        catch (Exception e) {}

        LoginRequest req = new LoginRequest("test-user", "pass-test");
        LoginResponse res = null;
        try {
            res = UserService.login(req, authDAO, userDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception401.class);
        }
        Assertions.assertNull(res);
    }

    @Test
    public void testLogoutSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("test-user", "test-pass",
                "test@mail.com");
        RegisterResponse registerResponse = null;
        try {
            registerResponse = UserService.register(registerRequest, authDAO, userDAO);
            String auth = registerResponse.authToken();
            UserService.logout(auth, authDAO);
        }
        catch (Exception e) {
            Assertions.assertNull(e);
        }
    }

    @Test
    public void testLogout401() {
        RegisterRequest registerRequest = new RegisterRequest("test-user", "test-pass",
                "test@mail.com");
        RegisterResponse registerResponse = null;
        try {
            registerResponse = UserService.register(registerRequest, authDAO, userDAO);
            String auth = registerResponse.authToken();
            UserService.logout("bad-auth", authDAO);
            Assertions.fail();
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception401.class);
        }
    }

    @Test
    public void testListGamesSuccess() {
        gameDAO.createGame("name1", new ChessGame());
        gameDAO.createGame("name2", new ChessGame());
        gameDAO.createGame("name10", new ChessGame());

        String authToken = "test-token";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        ListGameResponse res = null;
        try {
            res = GameService.listGames(authToken, authDAO, gameDAO);
        }
        catch (Exception e) {}

        Assertions.assertNotNull(res);
        Assertions.assertEquals(3, res.games().size());
    }

    @Test
    public void testListGame401() {
        gameDAO.createGame("name1", new ChessGame());
        gameDAO.createGame("name2", new ChessGame());
        gameDAO.createGame("name10", new ChessGame());

        String authToken = "test-token";
        ListGameResponse res = null;
        try {
            res = GameService.listGames(authToken, authDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception401.class);
        }

        Assertions.assertNull(res);
    }

    @Test
    public void testCreateGameSuccess() {
        String authToken = "test-token";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        CreateRequest req1 = new CreateRequest("test-game");
        CreateRequest req2 = new CreateRequest("game-test");

        CreateResponse res1 = null;
        CreateResponse res2 = null;
        try {
            res1 = GameService.createGame(req1, authToken, authDAO, gameDAO);
            res2 = GameService.createGame(req2, authToken, authDAO, gameDAO);
        }
        catch (Exception e) {}

        Assertions.assertNotNull(res1);
        Assertions.assertNotNull(res2);
        Assertions.assertEquals(gameDAO.listAll().size(), 2);

    }

    @Test
    public void testCreateGame400() {
        String authToken = "test-token";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        CreateRequest req = new CreateRequest(null);

        CreateResponse res = null;
        try {
            res = GameService.createGame(req, authToken, authDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception400.class);
        }
        Assertions.assertNull(res);
    }

    @Test
    public void testCreateGame401() {
        String authToken = "test-token";
        CreateRequest req = new CreateRequest("test-game");

        CreateResponse res = null;
        try {
            res = GameService.createGame(req, authToken, authDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception401.class);
        }
        Assertions.assertNull(res);
    }

    @Test
    public void testJoinGameSuccess() {
        gameDAO.createGame("test-game", new ChessGame());

        String authToken = "test-auth";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        JoinRequest req = new JoinRequest("WHITE", 1);

        try {
            GameService.joinGame(req, authToken, authDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertNull(e);
        }

    }

    @Test
    public void testWatchGameSuccess() {
        gameDAO.createGame("test-game", new ChessGame());

        String authToken = "test-auth";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        JoinRequest req = new JoinRequest(null, 1);

        try {
            GameService.joinGame(req, authToken, authDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertNull(e);
        }

    }

    @Test
    public void testJoinGame400() {
        gameDAO.createGame("test-game", new ChessGame());

        String authToken = "test-auth";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        JoinRequest req = new JoinRequest("WHITE", 0);

        try {
            GameService.joinGame(req, authToken, authDAO, gameDAO);
            Assertions.fail();
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception400.class);
        }
    }

    @Test
    public void testJoinGame401() {
        gameDAO.createGame("test-game", new ChessGame());

        String authToken = "test-auth";

        JoinRequest req = new JoinRequest("WHITE", 1);

        try {
            GameService.joinGame(req, authToken, authDAO, gameDAO);
            Assertions.fail();
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception401.class);
        }
    }

    @Test
    public void testJoinGame403() {
        gameDAO.createGame("test-game", new ChessGame());

        String authToken = "test-auth";
        authDAO.addAuth(new AuthData(authToken, "test-user"));

        JoinRequest req = new JoinRequest("WHITE", 1);

        try {
            GameService.joinGame(req, authToken, authDAO, gameDAO);
            GameService.joinGame(req, authToken, authDAO, gameDAO);
            Assertions.fail();
        }
        catch (Exception e) {
            Assertions.assertEquals(e.getClass(), Exception403.class);
        }
    }

    @Test
    public void testClear() {
        userDAO.addUser(new UserData("user1", "pass1", "1@mail.com"));
        userDAO.addUser(new UserData("user2", "pass2", "2@mail.com"));
        userDAO.addUser(new UserData("user3", "pass3", "3@mail.com"));

        authDAO.addAuth(new AuthData("token1", "user1"));
        authDAO.addAuth(new AuthData("token2", "user2"));

        gameDAO.createGame("game1", new ChessGame());
        gameDAO.createGame("game2", new ChessGame());

        try {
            ClearService.clear(authDAO, userDAO, gameDAO);
        }
        catch (Exception e) {
            Assertions.assertNull(e);
        }

        Assertions.assertEquals(gameDAO.listAll(), new ArrayList<GameData>());
        Assertions.assertEquals(userDAO.listAll(), new ArrayList<UserData>());
        Assertions.assertEquals(authDAO.listAll(), new ArrayList<AuthData>());
    }
}