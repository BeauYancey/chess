package service;

import requestResponse.ClearRequest;
import requestResponse.ClearResponse;

import java.util.HashMap;

public class ClearService {
    public static ClearResponse clear(ClearRequest req) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        ClearResponse res = new ClearResponse(500, headers, "Error: test");

        return res;
    }
}
