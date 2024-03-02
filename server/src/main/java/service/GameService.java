package service;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import service.exception.*;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    public static ListGameResponse listGames(String authToken, AuthDAO authDAO, GameDAO gameDAO) throws Exception401 {
        if (authDAO.getAuth(authToken) == null) {
            throw new Exception401();
        }

        List<GameData> gameList = gameDAO.listAll();
        return new ListGameResponse(gameList);
    }

    public static CreateResponse createGame(CreateRequest request, String authToken, AuthDAO authDAO, GameDAO gameDAO)
            throws Exception400, Exception401 {
        if (request.gameName() == null) {
            throw new Exception400();
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new Exception401();
        }

        int gameID = gameDAO.createGame(request.gameName(), new ChessGame());

        return new CreateResponse(gameID);
    }

    public static void joinGame(JoinRequest request, String authToken, AuthDAO authDAO, GameDAO gameDAO)
            throws Exception400, Exception401, Exception403 {
        if (request.gameID() == 0) {
            throw new Exception400();
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new Exception401();
        }
        if (request.playerColor() != null) {
            String username = authDAO.getAuth(authToken).userName();
            gameDAO.joinGame(request.gameID(), username, request.playerColor());
        }
    }
}
