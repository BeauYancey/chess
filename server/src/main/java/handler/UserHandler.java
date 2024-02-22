package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
}
