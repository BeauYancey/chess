package service;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    private static int nextID = 0;
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

    public static CreateResponse createGame(CreateRequest request, String authToken) {
        // check that the user exists and is logged in
        AuthData user = UserService.verifyAuth(authToken);
        // throw exception if user not found (not logged in)


        // add information to the database;
        nextID++;

        return new CreateResponse(nextID);
    }

    public static void joinGame(JoinRequest request, String authToken) {
        // check that the user exists and is logged in
        AuthData user = UserService.verifyAuth(authToken);
        // throw exception if user not found (not logged in)

        if (request.playerColor() == null) {
            // join as spectator
        }
        else {
            // join as request.playerColor()
        }
    }
}
