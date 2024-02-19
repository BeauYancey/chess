package requestResponse;

public class ClearResponse {
    private final transient int status;
    private final String message;

    public ClearResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
