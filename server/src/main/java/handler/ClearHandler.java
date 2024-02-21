package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import requestResponse.ClearRequest;
import service.ClearService;
import spark.Request;
import spark.Response;
import requestResponse.ClearResponse;

import java.util.HashMap;

public class ClearHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String clear(Request req, Response res) {
        // initialize new Request Object
        HashMap<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("auth", req.headers("authorization"));
        ClearRequest request = new ClearRequest(reqHeaders);

        // delegate functionality to ClearService
        ClearResponse response = ClearService.clear(request);

        // interpret ClearResponse and return relevant info
        res.status(response.getStatus());
        return gson.toJson(response);
    }
}
