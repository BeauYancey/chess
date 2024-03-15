package service;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import service.exception.*;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import java.util.List;

public class GameService {

    public static ListGameResponse listGames(String authToken, AuthDAO authDAO, GameDAO gameDAO)
            throws ServerException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }

        List<GameData> gameList = gameDAO.listAll();
        return new ListGameResponse(gameList);
    }

    public static CreateResponse createGame(CreateRequest request, String authToken, AuthDAO authDAO, GameDAO gameDAO)
            throws ServerException, DataAccessException {
        if (request.gameName() == null) {
            throw new ServerException(400, "Error: bad request");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }

        int gameID = gameDAO.createGame(request.gameName(), new ChessGame());

        return new CreateResponse(gameID);
    }

    public static void joinGame(JoinRequest request, String authToken, AuthDAO authDAO, GameDAO gameDAO)
            throws ServerException, DataAccessException {
        if (request.gameID() == 0) {
            throw new ServerException(400, "Error: bad request");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        if (request.playerColor() != null) {
            String username = authDAO.getAuth(authToken).userName();
            gameDAO.joinGame(request.gameID(), username, request.playerColor());
        }
    }
}
