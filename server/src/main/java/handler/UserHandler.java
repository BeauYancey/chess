package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import requestResponse.LoginRequest;
import requestResponse.LoginResponse;
import requestResponse.RegisterRequest;
import requestResponse.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String register(Request req, Response res) {
        // delegate functionality to the service
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResponse response = UserService.register(request);

        // interpret and return registerResponse
        res.status(200);
        return gson.toJson(response);
    }

    public static String login(Request req, Response res) {
        // delegate functionality to the service
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        LoginResponse response = UserService.login(request);

        // interpret and return loginResponse
        res.status(200);
        return gson.toJson(response);
    }

    public static String logout(Request req, Response res) {
        // delegate functionality to the service
        String auth = req.headers("authorization");
        UserService.logout(auth);

        res.status(200);
        return "{}";
    }
}
