package dataAccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataAccessTests {

    UserDAO userDAO = new SQLUserDAO();

    @BeforeEach
    public void reset() throws DataAccessException{
        userDAO.removeAll();
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
    public void removeAllTest() {
        try {
            userDAO.removeAll();
            UserData testUser = userDAO.getUser("test-user");
            Assertions.assertNull(testUser);
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
    }
}
