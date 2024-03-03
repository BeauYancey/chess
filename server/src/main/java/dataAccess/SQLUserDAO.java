package dataAccess;

import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = String.format("SELECT username, password, email FROM users WHERE username='%s'", username);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String password = rs.getString(2);
                    String email = rs.getString(3);
                    return new UserData(username, password, email);
                }
                else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void addUser(UserData userData) {

    }

    @Override
    public void removeAll() {

    }
}
