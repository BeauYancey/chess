package dataAccess.memory;

import chess.ChessGame;
import dataAccess.GameDAO;
import model.GameData;
import exception.ServerException;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private ArrayList<GameData> gameDatabase;
    private int size;
    private int nextID = 0;

    public MemoryGameDAO() {
        gameDatabase = new ArrayList<>();
        size = 0;
    }

    @Override
    public List<GameData> listAll() {
        return gameDatabase;
    }

    @Override
    public void leaveGame(int gameID, String color) {
        GameData gameData = null;
        for (GameData game : gameDatabase) {
            if (game.gameID() == gameID) {
                gameData = game;
            }
        }
        if (gameData == null) {
            return;
        }
        if (color.equals("white")) {
            gameData = new GameData(gameID, null, gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }
        else if (color.equals("black")) {
            gameData = new GameData(gameID, gameData.whiteUsername(), null,
                    gameData.gameName(), gameData.game());
        }
    }

    @Override
    public int createGame(String gameName, ChessGame game) {
        gameDatabase.add(new GameData(++nextID, null, null, gameName, game));
        size++;
        return nextID;
    }

    @Override
    public void joinGame(int gameID, String username, String color) throws ServerException {
        for (int i = 0; i < size; i++) {
            if (gameDatabase.get(i).gameID() == gameID) {
                GameData game = gameDatabase.get(i);
                if (color.equals("WHITE")) {
                    if (!(game.whiteUsername() == null)) {
                        throw new ServerException(403, "Error: already taken");
                    }
                    game = game.setWhite(username);
                }
                else if (color.equals("BLACK")) {
                    if (!(game.blackUsername() == null)) {
                        throw new ServerException(403, "Error: already taken");
                    }
                    game = game.setBlack(username);
                }
                gameDatabase.remove(i);
                gameDatabase.add(game);
                return;
            }
        }
        throw new ServerException(400, "Error: bad request");
    }

    @Override
    public void updateGame(int gameId, ChessGame newGame) {}

    @Override
    public void removeAll() {
        gameDatabase.clear();
        size = 0;
    }
}
