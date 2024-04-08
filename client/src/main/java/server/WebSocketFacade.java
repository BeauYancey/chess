package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ServerException;
import ui.EscapeSequences;
import ui.GameplayClient;
import ui.Repl;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;


public class WebSocketFacade extends Endpoint {
    Session session;
    Repl repl;
    Gson gson;
    GameplayClient client;

    public WebSocketFacade(String url, GameplayClient client) throws ServerException {
        this.gson = new Gson();
        this.repl = client.repl;
        this.client = client;

        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType msgType = serverMessage.getServerMessageType();
                    switch (msgType) {
                        case ERROR -> error(message);
                        case LOAD_GAME -> loadGame(message);
                        case NOTIFICATION -> notification(message);
                    }
                }
            });
        }
        catch (Exception ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
    private void error(String message) {
        ErrorMessage msg = gson.fromJson(message, ErrorMessage.class);
        repl.printMsg("\b\b\b");
        repl.printErr(msg.errorMessage);
        repl.printPrompt();
    }
    private void loadGame(String message) {
        LoadGameMessage msg = gson.fromJson(message, LoadGameMessage.class);
        client.gameData = msg.game;
        client.draw();
    }
    private void notification(String message) {
        NotificationMessage msg = gson.fromJson(message, NotificationMessage.class);
        repl.printMsg("\b\b\b");
        repl.printMsg(msg.message);
        repl.printPrompt();
    }
    public void makeMove(String auth, int gameID, ChessMove move) throws ServerException {
        try {
            MakeMoveCommand cmd = new MakeMoveCommand(auth, gameID, move);
            this.session.getBasicRemote().sendText(gson.toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
    public void joinPlayer(String auth, int gameID, ChessGame.TeamColor color) throws ServerException {
        try {
            JoinPlayerCommand cmd = new JoinPlayerCommand(auth, gameID, color);
            this.session.getBasicRemote().sendText(gson.toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
    public void joinObserver(String auth, int gameID) throws ServerException {
        try {
            JoinObserverCommand cmd = new JoinObserverCommand(auth, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
    public void leave(String auth, int gameID) throws ServerException {
        try {
            LeaveCommand cmd = new LeaveCommand(auth, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
    public void resign(String auth, int gameID) throws ServerException {
        try {
            ResignCommand cmd = new ResignCommand(auth, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(cmd));
        }
        catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
}
