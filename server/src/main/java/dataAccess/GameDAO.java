package dataAccess;

import chess.ChessGame;
import model.GameData;
import service.exception.Exception400;
import service.exception.Exception403;

import java.util.List;

public interface GameDAO {
    List<GameData> listAll() throws DataAccessException;
    int createGame(String gameName, ChessGame game) throws DataAccessException;
    void joinGame(int gameID, String username, String color) throws Exception403, Exception400, DataAccessException;
    void removeAll() throws DataAccessException;
}
