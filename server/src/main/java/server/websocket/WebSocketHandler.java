package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataAccess.*;
import exception.ServerException;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

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
            case LEAVE -> leave(session, message);
            case RESIGN -> resign(session, message);
        }
    }

    private void joinPlayer(Session session, String msg) throws IOException {
        JoinPlayerCommand cmd = gson.fromJson(msg, JoinPlayerCommand.class);
        try {
            GameData gameData = getGame(cmd.getAuthString(), cmd.gameID);
            AuthData authData = authDAO.getAuth(cmd.getAuthString());

            if (cmd.playerColor == ChessGame.TeamColor.WHITE) {
                if (!authData.userName().equals(gameData.whiteUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            cmd.playerColor +
                            " player in this game");
                    session.getRemote().sendString(gson.toJson(err));
                    return;
                }
            }
            else {
                if (!authData.userName().equals(gameData.blackUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            cmd.playerColor +
                            " player in this game");
                    session.getRemote().sendString(gson.toJson(err));
                    return;
                }
            }

            addConnection(session, cmd.gameID, authData.userName());

            // Send LoadGameMessage to user
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(load));

            // Notify all other users in the game
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
            GameData gameData = getGame(cmd.getAuthString(), cmd.gameID);
            AuthData authData = authDAO.getAuth(cmd.getAuthString());

            if (gameData == null) {
                ErrorMessage err = new ErrorMessage("Error: game does not exist");
                session.getRemote().sendString(gson.toJson(err));
                return;
            }

            addConnection(session, cmd.gameID, authData.userName());

            // Send LoadGameMessage to user
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(load));

            // Notify all other users in the game
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
            GameData gameData = getGame(cmd.getAuthString(), cmd.gameID);
            AuthData authData = authDAO.getAuth(cmd.getAuthString());
            ChessGame.TeamColor pieceColor = gameData.game().getBoard().getPiece(cmd.move.getStartPosition()).getTeamColor();

            if (pieceColor == ChessGame.TeamColor.WHITE) {
                if (!authData.userName().equals(gameData.whiteUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            pieceColor +
                            " player in this game");
                    session.getRemote().sendString(gson.toJson(err));
                    return;
                }
            }
            else {
                if (!authData.userName().equals(gameData.blackUsername())) {
                    ErrorMessage err = new ErrorMessage("Error: you are not the " +
                            pieceColor +
                            " player in this game");
                    session.getRemote().sendString(gson.toJson(err));
                    return;
                }
            }


            gameData.game().makeMove(cmd.move);
            GameService.updateGame(gameData, cmd.getAuthString(), authDAO, gameDAO);

            // Notify all other users that a move was made
            NotificationMessage notification = new NotificationMessage(authData.userName() + " just made a move.");
            connections.get(cmd.gameID).broadcast(authData.userName(), notification);

            // Send LoadGameMessage to all involved users
            LoadGameMessage load = new LoadGameMessage(gameData);
            session.getRemote().sendString(gson.toJson(load));
            connections.get(cmd.gameID).broadcast(authData.userName(), load);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void leave(Session session, String msg) throws IOException {
        try {
            LeaveCommand cmd = gson.fromJson(msg, LeaveCommand.class);
            AuthData authData = authDAO.getAuth(cmd.getAuthString());
            GameData gameData = getGame(cmd.getAuthString(), cmd.gameID);
            if (authData.userName().equals(gameData.whiteUsername())) {
                gameDAO.leaveGame(gameData.gameID(), "white");
            }
            else if (authData.userName().equals(gameData.blackUsername())) {
                gameDAO.leaveGame(gameData.gameID(), "black");
            }

            connections.get(cmd.gameID).remove(authData.userName());
            NotificationMessage notification = new NotificationMessage(authData.userName() + " left the game.");
            connections.get(cmd.gameID).broadcast(authData.userName(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private void resign(Session session, String msg) throws IOException {
        try {
            LeaveCommand cmd = gson.fromJson(msg, LeaveCommand.class);
            AuthData authData = authDAO.getAuth(cmd.getAuthString());
            GameData gameData = getGame(cmd.getAuthString(), cmd.gameID);

            if (!(authData.userName().equals(gameData.blackUsername()) ||
                    authData.userName().equals(gameData.whiteUsername()))) {
                ErrorMessage err = new ErrorMessage("You are not a player in this game");
                session.getRemote().sendString(gson.toJson(err));
                return;
            }

            if (gameData.game().getTeamTurn() == null) {
                ErrorMessage err = new ErrorMessage("This game is already over");
                session.getRemote().sendString(gson.toJson(err));
                return;
            }
            gameData.game().makeMove(new ChessMove(new ChessPosition(2, 2), new ChessPosition(4, 2), null));
            gameData.game().setTeamTurn(null);
            GameService.updateGame(gameData, cmd.getAuthString(), authDAO, gameDAO);

            // Notify all users that the player resigned
            NotificationMessage notification = new NotificationMessage(authData.userName() + " just resigned.");
            session.getRemote().sendString(gson.toJson(notification));
            connections.get(cmd.gameID).broadcast(authData.userName(), notification);
        }
        catch (Exception ex) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
    }

    private GameData getGame(String auth, int gameID) throws ServerException, DataAccessException {
        GameData gameData = null;
        for (GameData game : GameService.listGames(auth, authDAO, gameDAO).games()) {
            if (game.gameID() == gameID) {
                gameData = game;
                break;
            }
        }
        return gameData;
    }
}
