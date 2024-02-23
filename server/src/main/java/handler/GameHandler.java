package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import requestResponse.CreateRequest;
import requestResponse.CreateResponse;
import requestResponse.JoinRequest;
import requestResponse.ListGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String listGames(Request req, Response res) {
        String auth = req.headers("authorization");
        ListGameResponse response = GameService.listGames(auth);

        res.status(200);
        return gson.toJson(response);
    }

    public static String createGame(Request req, Response res) {
        String auth = req.headers("authorization");
        CreateRequest request = gson.fromJson(req.body(), CreateRequest.class);
        CreateResponse response = GameService.createGame(request, auth);

        res.status(200);
        return gson.toJson(response);
    }

    public static String joinGame(Request req, Response res) {
        String auth = req.headers("authorization");
        JoinRequest request = gson.fromJson(req.body(), JoinRequest.class);
        GameService.joinGame(request, auth);

        res.status(200);
        return "{}";
    }
}
