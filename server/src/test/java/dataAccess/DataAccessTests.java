package dataAccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class DataAccessTests {

    UserDAO userDAO = new SQLUserDAO();
    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO gameDAO = new SQLGameDAO();

    @BeforeEach
    public void reset() throws DataAccessException{
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

    @Test
    public void getUserTest() {
        try {
            UserData user = userDAO.getUser("test-user");
            Assertions.assertNotNull(user);
            Assertions.assertEquals(user.password(), "test-pass");
            Assertions.assertEquals(user.email(), "test-email");
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void addUserTest() {
        try {
            UserData newUser = new UserData("test-user-2", "test-pass-2", "test-email-2");
            userDAO.addUser(newUser);

            UserData result = userDAO.getUser("test-user-2");
            Assertions.assertNotNull(result);
            Assertions.assertEquals(result.password(), "test-pass-2");
            Assertions.assertEquals(result.email(), "test-email-2");
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void removeAllUsersTest() {
        try {
            userDAO.removeAll();
            UserData testUser = userDAO.getUser("test-user");
            Assertions.assertNull(testUser);
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void getAuthTest() {
        try {
            AuthData auth = authDAO.getAuth("test-token");
            Assertions.assertNotNull(auth);
            Assertions.assertEquals(auth.userName(), "test-user");
            Assertions.assertEquals(auth.authToken(), "test-token");
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void addAuthTest() {
        try {
            AuthData auth = new AuthData("test-token-2", "test-user-2");
            authDAO.addAuth(auth);

            AuthData result = authDAO.getAuth("test-token-2");
            Assertions.assertEquals(auth, result);
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void removeAuthTest() {
        try {
            authDAO.removeAuth("test-token");
            AuthData auth = authDAO.getAuth("test-token");
            Assertions.assertNull(auth);
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }

    @Test
    public void removeAllAuthsTest() {
        try {
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
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }
}
