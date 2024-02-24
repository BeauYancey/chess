package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import service.GameService;
import service.exception.*;
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
        catch (Exception401 e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
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
        catch (Exception400 e) {
            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
        catch (Exception401 e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
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
        catch (Exception400 e) {
            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
        catch (Exception401 e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (Exception403 e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }
}