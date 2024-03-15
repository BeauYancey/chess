package dataAccess;

import chess.ChessGame;
import model.GameData;
import service.exception.ServerException;

import java.util.List;

public interface GameDAO {
    List<GameData> listAll() throws DataAccessException;
    int createGame(String gameName, ChessGame game) throws DataAccessException;
    void joinGame(int gameID, String username, String color) throws ServerException, DataAccessException;
    void removeAll() throws DataAccessException;
}
