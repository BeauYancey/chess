package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;
import requestResponse.ClearResponse;

public class ClearHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String clear(Request req, Response res) {
        ClearResponse response = new ClearResponse(500, "Error: description");
        res.status(response.getStatus());
        return gson.toJson(response);
    }
}
