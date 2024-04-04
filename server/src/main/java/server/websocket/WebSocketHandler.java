package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer();
            case JOIN_OBSERVER -> joinObserver();
            case MAKE_MOVE -> move();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void joinPlayer() {}

    private void joinObserver() {
        System.out.println("Entering server.WebSocketHandler.joinObserver");
    }

    private void move() {}

    private void leave() {}

    private void resign() {}
}
