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
            case JOIN_PLAYER -> nothing();
            case JOIN_OBSERVER -> nothing();
            case MAKE_MOVE -> nothing();
            case LEAVE -> nothing();
            case RESIGN -> nothing();
        }
    }

    private void nothing() {}
}
