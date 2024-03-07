package dataAccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        String statement = "CREATE TABLE IF NOT EXISTS auth" +
                "( token varchar(36) not null, " +
                "username varchar(36) not null)";

        Connection conn = DatabaseManager.getConnection();
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auth (token, username) values (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.userName());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DataAccessException("SQL: Adding new auth affected " + rowsAffected + " rows");
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
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
    public void removeAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE token=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DataAccessException("SQL: removing auth affected " + rowsAffected + " rows");
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void removeAll() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
