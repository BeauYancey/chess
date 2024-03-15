package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import exception.ServerException;
import requestResponse.*;
import service.UserService;
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
        catch (ServerException ex) {
            res.status(ex.getStatus());
            return "{ \"message\": \"" + ex.getMessage() + "\" }";
        }
        catch (DataAccessException e) {
            res.status(500);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }
    }

    public static String login(Request req, Response res, AuthDAO authDAO, UserDAO userDAO) {
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResponse response = UserService.login(request, authDAO, userDAO);

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

    public static String logout(Request req, Response res, AuthDAO authDAO) {
        // delegate functionality to the service
        String auth = req.headers("authorization");
        try {
            UserService.logout(auth, authDAO);

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
