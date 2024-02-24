package dataAccess;

import model.GameData;
import service.exception.Exception400;
import service.exception.Exception403;

import java.util.List;

public interface GameDAO {
    List<GameData> listAll();
    void createGame(GameData gameData);
    void joinGame(int gameID, String username, String color) throws Exception403, Exception400;
    void removeAll();
}
