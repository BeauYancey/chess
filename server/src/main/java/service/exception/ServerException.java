package service.exception;

public class ServerException extends Exception {
    private int status;
    public ServerException(int status, String msg) {
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
