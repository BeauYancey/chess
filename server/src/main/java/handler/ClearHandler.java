package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String clear(Request req, Response res, AuthDAO auths, UserDAO users, GameDAO games) {
        // delegate functionality to ClearService
         ClearService.clear(auths, users, games);

        // interpret ClearResponse and return relevant info
        res.status(200);
        return "{}";
    }
}
