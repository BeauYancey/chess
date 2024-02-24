package dataAccess;

import model.GameData;
import service.exception.Exception400;
import service.exception.Exception403;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private ArrayList<GameData> gameDatabase;
    private int size;

    public MemoryGameDAO() {
        gameDatabase = new ArrayList<>();
        size = 0;
    }

    @Override
    public List<GameData> listAll() {
        return gameDatabase;
    }

    @Override
    public void createGame(GameData gameData) {
        gameDatabase.add(gameData);
        size++;
    }

    @Override
    public void joinGame(int gameID, String username, String color) throws Exception400, Exception403 {
        for (int i = 0; i < size; i++) {
            if (gameDatabase.get(i).gameID() == gameID) {
                GameData game = gameDatabase.get(i);
                if (color.equals("WHITE")) {
                    if (!(game.whiteUsername() == null)) {
                        throw new Exception403();
                    }
                    game = game.setWhite(username);
                }
                else if (color.equals("BLACK")) {
                    if (!(game.blackUsername() == null)) {
                        throw new Exception403();
                    }
                    game = game.setBlack(username);
                }
                gameDatabase.remove(i);
                gameDatabase.add(game);
                return;
            }
        }
        throw new Exception400();
    }

    @Override
    public void removeAll() {
        gameDatabase.clear();
        size = 0;
    }
}
