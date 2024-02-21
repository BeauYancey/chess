package requestResponse;

import java.util.HashMap;

public class Request {
    private final transient HashMap<String, String> headers;

    public Request(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
