package requestResponse;

import java.util.HashMap;

public class ClearResponse extends Response {
    public ClearResponse(int status, HashMap<String, String> headers) {
        super(status, headers);
    }

    public ClearResponse(int status, HashMap<String, String> headers, String message) {
        super(status, headers, message);
    }
}
