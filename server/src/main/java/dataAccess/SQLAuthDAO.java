package dataAccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String query = "SELECT token, username FROM auth WHERE token=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authToken);

                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String token = rs.getString(1);
                    String username = rs.getString(2);
                    return new AuthData(token, username);
                }
                else {
                    return null;
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void removeAuth(String authToken) {

    }

    @Override
    public void removeAll() {

    }
}
