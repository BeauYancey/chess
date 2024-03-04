package dataAccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
