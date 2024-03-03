package dataAccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {

    SQLUserDAO userDAO = new SQLUserDAO();

    @Test
    public void getTestUser() {
        UserData user = null;
        try {
            user = userDAO.getUser("test-user");
        }
        catch (DataAccessException ex) {
            Assertions.fail();
        }
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.password(), "test-pass");
        Assertions.assertEquals(user.email(), "test-email");
    }
}
