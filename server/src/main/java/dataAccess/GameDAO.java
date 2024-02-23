package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> listAll();
    void createGame(GameData gameData);
    void joinGame(int gameID, String username, String color);

    void removeAll();
}
