package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.exception.Exception400;
import service.exception.Exception403;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public List<GameData> listAll() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        String query = "SELECT id, white_username, black_username, name, game FROM games";
        Gson gson = new Gson();

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                var rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String white = rs.getString(2);
                    String black = rs.getString(3);
                    String name = rs.getString(4);
                    ChessGame game = gson.fromJson(rs.getString(5), ChessGame.class);

                    gameList.add(new GameData(id, white, black, name, game));
                }

            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return gameList;
    }

    @Override
    public int createGame(String gameName, ChessGame game) throws DataAccessException {
        String statement = "INSERT INTO games (name, game) values (?, ?)";
        String query = "SELECT id FROM games WHERE name=?";
        Gson gson = new Gson();

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, gson.toJson(game));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 1) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        generatedKeys.next();
                        return generatedKeys.getInt(1);
                    }
                }
                else {
                    throw new DataAccessException("SQL: creating game affected " + rowsAffected + " rows");
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, String username, String color) throws DataAccessException, Exception403, Exception400 {
        String field = color.toLowerCase() + "_username";
        String query = String.format("SELECT %s FROM games WHERE id = ?", field);
        String statement = String.format("UPDATE games SET %s = ? WHERE id = ?", field);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, gameID);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    if (rs.getString(1) != null) {
                        throw new Exception403();
                    }
                }
                else {
                    throw new Exception400();
                }
            }
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DataAccessException("SQL: joining game affected " + rowsAffected + " rows");
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
            try (var preparedStatement = conn.prepareStatement("DELETE FROM games")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
