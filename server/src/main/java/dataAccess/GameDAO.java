package dataAccess;

import chess.ChessGame;
import model.GameData;
import exception.ServerException;

import java.util.List;

public interface GameDAO {
    List<GameData> listAll() throws DataAccessException;

//    GameData getOne(int gameID) throws DataAccessException;
    int createGame(String gameName, ChessGame game) throws DataAccessException;
    void joinGame(int gameID, String username, String color) throws ServerException, DataAccessException;
    void updateGame(int gameId, ChessGame newGame) throws DataAccessException, ServerException;
    void removeAll() throws DataAccessException;
}
