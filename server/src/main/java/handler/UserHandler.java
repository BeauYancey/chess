package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import requestResponse.*;
import service.UserService;
import service.exception.*;
import spark.Request;
import spark.Response;

public class UserHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String register(Request req, Response res, AuthDAO authDAO, UserDAO userDAO) {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResponse response = UserService.register(request, authDAO, userDAO);
            res.status(200);
            return gson.toJson(response);
        }
        catch (Exception400 e) {
            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
        catch (Exception403 e) {
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }

    public static String login(Request req, Response res, AuthDAO authDAO, UserDAO userDAO) {
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResponse response = UserService.login(request, authDAO, userDAO);

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

    public static String logout(Request req, Response res, AuthDAO authDAO) {
        // delegate functionality to the service
        String auth = req.headers("authorization");
        try {
            UserService.logout(auth, authDAO);

            res.status(200);
            return "{}";
        }
        catch (Exception401 e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }
}
