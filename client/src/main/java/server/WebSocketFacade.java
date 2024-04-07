package server;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ServerException;
import ui.Repl;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.MakeMoveCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;


public class WebSocketFacade extends Endpoint {
    Session session;
    Repl repl;
    Gson gson;

    public WebSocketFacade(String url, Repl repl) throws ServerException {
        gson = new Gson();
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.repl = repl;

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
        repl.printErr(msg.errorMessage);
    }
    private void loadGame(String message) {
        repl.printMsg("LOAD_GAME ServerMessage received");
    }
    private void notification(String message) {
        NotificationMessage msg = gson.fromJson(message, NotificationMessage.class);
        repl.printMsg(msg.message);
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

}
