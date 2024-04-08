package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.HashMap;


@WebSocket
public class WebSocketHandler {
    private final HashMap<Integer, ConnectionManager> connections;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final Gson gson;

    public WebSocketHandler() throws DataAccessException {
        connections = new HashMap<Integer, ConnectionManager>();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        gson = new Gson();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = gson.fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> joinObserver(session, message);
            case MAKE_MOVE -> move(session, message);
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void joinPlayer(Session session, String msg) throws IOException {
        JoinPlayerCommand cmd = gson.fromJson(msg, JoinPlayerCommand.class);
        try {
            AuthData authData = authDAO.getAuth(cmd.getAuthString());

            addConnection(session, cmd.gameID, authData.userName());

            NotificationMessage notification = new NotificationMessage(authData.userName() +
                    "joined the game as the " +
                    cmd.playerColor + " player");
            this.connections.get(cmd.gameID).broadcast(authData.userName(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void joinObserver(Session session, String msg) throws IOException {
        JoinObserverCommand cmd = gson.fromJson(msg, JoinObserverCommand.class);
        try {
            AuthData authData = authDAO.getAuth(cmd.getAuthString());

            addConnection(session, cmd.gameID, authData.userName());

            NotificationMessage notification = new NotificationMessage(authData.userName() +
                    "joined the game as the an observer");
            this.connections.get(cmd.gameID).broadcast(authData.userName(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void addConnection(Session session, int gameID, String username) {
        if (this.connections.containsKey(gameID)) {
            this.connections.get(gameID).add(username, session);
        }
        else {
            ConnectionManager manager = new ConnectionManager();
            manager.add(username, session);
            this.connections.put(gameID, manager);
        }
    }

    private void move(Session session, String msg) throws IOException {
        MakeMoveCommand cmd = gson.fromJson(msg, MakeMoveCommand.class);
        try {
            GameData gameData = null;
            for (GameData games : GameService.listGames(cmd.getAuthString(), authDAO, gameDAO).games()) {
                if (games.gameID() == cmd.gameID) {
                    gameData = games;
                    break;
                }
            }
            if (gameData == null) {
                return;
            }
            AuthData authData = authDAO.getAuth(cmd.getAuthString());

            gameData.game().makeMove(cmd.move);
            GameService.updateGame(gameData, cmd.getAuthString(), authDAO, gameDAO);

            NotificationMessage notification = new NotificationMessage(authData.userName() + " just made a move.");
            String res = gson.toJson(notification);
            session.getRemote().sendString(res);
            connections.get(cmd.gameID).broadcast(authData.userName(), notification);

            LoadGameMessage load = new LoadGameMessage(gameData);
            res = gson.toJson(load);
            session.getRemote().sendString(res);
            connections.get(cmd.gameID).broadcast(authData.userName(), load);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void leave() {}

    private void resign() {}
}
