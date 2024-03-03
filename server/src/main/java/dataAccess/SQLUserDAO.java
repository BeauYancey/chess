package dataAccess;

import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        String query = String.format("SELECT username, password, email FROM users WHERE username='%s'", username);
        String password;
        String email;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                var rs = preparedStatement.executeQuery();
                rs.next();

                password = rs.getString(2);
                email = rs.getString(3);
            }
        }
        catch (Exception ex) {
            return null;
        }
        return new UserData(username, password, email);
    }

    @Override
    public void addUser(UserData userData) {

    }

    @Override
    public void removeAll() {

    }
}
