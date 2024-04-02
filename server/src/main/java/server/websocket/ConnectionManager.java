package server.websocket;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;


public class ConnectionManager {
    public final HashMap<String, Connection> connections = new HashMap<>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String playerName, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerName.equals(playerName)) {
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (Connection c : removeList) {
            connections.remove(c.playerName);
        }
    }
}
