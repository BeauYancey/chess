package dataAccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataAccessTests {

    SQLUserDAO userDAO = new SQLUserDAO();

    @Test
    public void getTestUser() {
        UserData user =  userDAO.getUser("test-user");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.username(), "test-user");
    }
}
