package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;
import requestResponse.ClearResponse;

import java.util.HashMap;

public class ClearHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String clear(Request req, Response res) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        ClearResponse response = new ClearResponse(500, headers, "Error: description");
        res.status(response.getStatus());
        return gson.toJson(response);
    }
}
