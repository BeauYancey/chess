package requestResponse;

import java.util.HashMap;

public class Response {
    private final transient int status;
    private String message = null;
    private final transient HashMap<String, String> headers;

    public Response(int status, HashMap<String, String> headers, String message) {
        this.status = status;
        this.headers = headers;
        this.message = message;
    }

    public Response(int status, HashMap<String, String> headers) {
        this.status = status;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
