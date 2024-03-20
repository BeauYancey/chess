package clientTests;

import exception.ServerException;
import model.GameData;
import org.junit.jupiter.api.*;
import requestResponse.CreateResponse;
import requestResponse.ListGameResponse;
import requestResponse.LoginResponse;
import requestResponse.RegisterResponse;
import server.Server;
import server.ServerFacade;

import java.io.IOException;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void reset() throws ServerException, IOException {
        facade.clearDBs();
        String auth = facade.register("test-user", "test-pass", "test-email").authToken();
        facade.logout(auth);
    }

    @Test
    public void testRegister() throws ServerException, IOException {
        RegisterResponse res = facade.register("test-user-2", "test-pass-2", "test-email-2");
        Assertions.assertEquals("test-user-2", res.username());
    }

    @Test
    public void testRegister400() throws IOException {
        try {
            facade.register("test-user-2", "test-pass-2", null);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(400, ex.getStatus());
        }
    }

    @Test
    public void testRegister403() throws IOException {
        try {
            facade.register("test-user", "test-pass", "test-email");
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(403, ex.getStatus());
        }
    }

    @Test
    public void testLogin() throws ServerException, IOException {
        LoginResponse res = facade.login("test-user", "test-pass");
        Assertions.assertEquals("test-user", res.username());
    }

    @Test
    public void testLogin401() throws IOException {
        try {
            LoginResponse res = facade.login("test-user", "bad-pass");
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
        }
    }

    @Test
    public void testLogout() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        facade.logout(auth);

        try {
            facade.listGames(auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
        }
    }

    @Test
    public void testLogout401() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        facade.logout(auth);

        try {
            facade.logout(auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
        }
    }

    @Test
    public void testListGames() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        List<GameData> gameList = facade.listGames(auth).games();
        Assertions.assertEquals(0, gameList.size());
    }

    @Test
    public void testListGames401() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        facade.logout(auth);

        try {
            facade.listGames(auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
        }
    }

    @Test
    public void testCreateGame() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        facade.createGame("test-game", auth);
        List<GameData> gameList = facade.listGames(auth).games();
        Assertions.assertEquals(1, gameList.size());
    }

    @Test
    public void testCreateGame400() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        try {
            facade.createGame(null, auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(400, ex.getStatus());
            List<GameData> gameList = facade.listGames(auth).games();
            Assertions.assertEquals(0, gameList.size());
        }
    }

    @Test
    public void testCreateGame401() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        facade.logout(auth);
        try {
            facade.createGame("test-game", auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
            auth = facade.login("test-user", "test-pass").authToken();
            List<GameData> gameList = facade.listGames(auth).games();
            Assertions.assertEquals(0, gameList.size());
        }
    }

    @Test
    public void testJoinGame() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        int gameID = facade.createGame("test-game", auth).gameID();

        facade.joinGame("white", gameID, auth);
        List<GameData> gameList = facade.listGames(auth).games();
        Assertions.assertEquals(1, gameList.size());
        Assertions.assertEquals("test-user", gameList.get(0).whiteUsername());
    }

    @Test
    public void testJoinGame400() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        int gameID = facade.createGame("test-game", auth).gameID();

        try {
            facade.joinGame("white", 0, auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(400, ex.getStatus());
            List<GameData> gameList = facade.listGames(auth).games();
            Assertions.assertEquals(1, gameList.size());
            Assertions.assertNull(gameList.get(0).whiteUsername());
        }
    }

    @Test
    public void testJoinGame401() throws ServerException, IOException {
        String auth = facade.login("test-user", "test-pass").authToken();
        int gameID = facade.createGame("test-game", auth).gameID();
        facade.logout(auth);

        try {
            facade.joinGame("white", gameID, auth);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(401, ex.getStatus());
            auth = facade.login("test-user", "test-pass").authToken();
            List<GameData> gameList = facade.listGames(auth).games();
            Assertions.assertEquals(1, gameList.size());
            Assertions.assertNull(gameList.get(0).whiteUsername());
        }
    }

    @Test
    public void testJoinGame403() throws ServerException, IOException {
        String auth1 = facade.login("test-user", "test-pass").authToken();
        String auth2 = facade.register("test-user-2", "test-pass-2", "test-email-2").authToken();
        int gameID = facade.createGame("test-game", auth1).gameID();


        try {
            facade.joinGame("white", gameID, auth1);
            facade.joinGame("white", gameID, auth2);
            Assertions.fail();
        }
        catch (ServerException ex) {
            Assertions.assertEquals(403, ex.getStatus());
            List<GameData> gameList = facade.listGames(auth1).games();
            Assertions.assertEquals(1, gameList.size());
            Assertions.assertEquals("test-user", gameList.get(0).whiteUsername());
        }
    }
}
