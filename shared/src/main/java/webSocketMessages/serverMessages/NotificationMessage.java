package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private final String message;
    public NotificationMessage(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.message = msg;
    }
}
