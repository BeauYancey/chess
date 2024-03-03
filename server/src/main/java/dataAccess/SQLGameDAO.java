package dataAccess;

import chess.ChessGame;
import dataAccess.GameDAO;
import model.GameData;
import service.exception.Exception400;
import service.exception.Exception403;

import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public List<GameData> listAll() {
        return null;
    }

    @Override
    public int createGame(String gameName, ChessGame game) {
        return 0;
    }

    @Override
    public void joinGame(int gameID, String username, String color) throws Exception403, Exception400 {

    }

    @Override
    public void removeAll() {

    }
}
