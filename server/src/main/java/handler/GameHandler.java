package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import exception.ServerException;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String listGames(Request req, Response res, AuthDAO authDAO, GameDAO gameDAO) {
        String authToken = req.headers("authorization");
        try {
            ListGameResponse response = GameService.listGames(authToken, authDAO, gameDAO);

            res.status(200);
            return gson.toJson(response);
        }
        catch (ServerException ex) {
            res.status(ex.getStatus());
            return "{ \"message\": \"" + ex.getMessage() + "\" }";
        }
        catch (DataAccessException e) {
            res.status(500);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }

    public static String createGame(Request req, Response res, AuthDAO authDAO, GameDAO gameDAO) {
        String auth = req.headers("authorization");
        CreateRequest request = gson.fromJson(req.body(), CreateRequest.class);

        try {
            CreateResponse response = GameService.createGame(request, auth, authDAO, gameDAO);

            res.status(200);
            return gson.toJson(response);
        }
        catch (ServerException ex) {
            res.status(ex.getStatus());
            return "{ \"message\": \"" + ex.getMessage() + "\" }";
        }
        catch (DataAccessException e) {
            res.status(500);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }

    public static String joinGame(Request req, Response res, AuthDAO authDAO, GameDAO gameDAO) {
        String auth = req.headers("authorization");
        JoinRequest request = gson.fromJson(req.body(), JoinRequest.class);
        try {
            GameService.joinGame(request, auth, authDAO, gameDAO);

            res.status(200);
            return "{}";
        }
        catch (ServerException ex) {
            res.status(ex.getStatus());
            return "{ \"message\": \"" + ex.getMessage() + "\" }";
        }
        catch (DataAccessException e) {
            res.status(500);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }
}
