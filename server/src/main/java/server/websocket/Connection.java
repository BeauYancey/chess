package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;


public class Connection {
    public String playerName;
    public Session session;
    private Gson gson = new Gson();

    public Connection(String playerName, Session session) {
        this.playerName = playerName;
        this.session = session;
    }

    public void send(ServerMessage msg) throws IOException {
        session.getRemote().sendString(gson.toJson(msg));
    }
}
