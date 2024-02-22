package service;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import requestResponse.ListGameResponse;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    public static ListGameResponse listGames(String authToken) {
        // check that the user exists and is logged in
        AuthData user = UserService.verifyAuth(authToken);
        // throw exception if user not found (not logged in)
        // get information from games database
        List<GameData> gameList = new ArrayList<>();
        gameList.add(new GameData(1, "white", "black", "name1", new ChessGame()));
        gameList.add(new GameData(2, "whiteUser", "blackUser", "name2", new ChessGame()));

        return new ListGameResponse(gameList);
    }
}
