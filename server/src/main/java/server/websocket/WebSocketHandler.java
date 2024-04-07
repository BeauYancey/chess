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
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final Gson gson;

    public WebSocketHandler() throws DataAccessException {
        connections = new ConnectionManager();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        gson = new Gson();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = gson.fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer();
            case JOIN_OBSERVER -> joinObserver();
            case MAKE_MOVE -> move(session, message);
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void joinPlayer() {}

    private void joinObserver() {
        System.out.println("Entering server.WebSocketHandler.joinObserver");
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
            LoadGameMessage load = new LoadGameMessage(gameData.game());
            String res = gson.toJson(load);
            session.getRemote().sendString(res);
            connections.broadcast(authData.userName(), load);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void leave() {}

    private void resign() {}
}
