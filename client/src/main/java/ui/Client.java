package ui;

import chess.*;
import chess.ChessGame.TeamColor;
import exception.ServerException;
import model.GameData;
import server.ServerFacade;
import java.io.IOException;
import java.util.List;

public class Client {
    private final ServerFacade serverFacade;
    private final Repl repl;
    private State state = State.LOGGEDOUT;
    private String authToken = null;
    private List<GameData> gameList;
    private GameplayClient gameplayClient;

    public Client(String serverURL, Repl repl) {
        serverFacade = new ServerFacade(serverURL);
        this.repl = repl;
    }

    public void eval(String input){
        if (state == State.GAMEPLAY) {
            state = gameplayClient.eval(input);
            return;
        }
        switch (input) {
            case "help" -> help();
            case "login" -> login();
            case "register" -> register();
            case "logout" -> logout();
            case "create" -> create();
            case "list" -> list();
            case "join" -> join();
            case "observe" -> observe();
            default -> repl.printErr("invalid instruction");
        }
    }

    private void help() {
        if (state == State.LOGGEDOUT) {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tLogin: log in to your account to access the full client\n" +
                    "\tRegister: create a new account\n" +
                    "\tQuit: terminate this chess client");
        }
        else if (state == State.LOGGEDIN) {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tLogout: end your session\n" +
                    "\tCreate: create a new game\n" +
                    "\tList: see all games\n" +
                    "\tJoin: join a game as the black or white player\n" +
                    "\tObserve: watch as others play a game\n" +
                    "\tQuit: terminate this chess client");
        }
    }

    private void login() {
        if (state == State.LOGGEDOUT) {
            repl.printMsg("Enter your username:");
            String username = repl.scanWord();
            repl.printMsg("Enter your password:");
            String password = repl.scanWord();

            try {
                authToken = serverFacade.login(username, password).authToken();
                state = State.LOGGEDIN;
                repl.printMsg("Welcome " + username);
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void register() {
        if (state == State.LOGGEDOUT) {
            repl.printMsg("Enter your username:");
            String username = repl.scanWord();
            repl.printMsg("Enter your email:");
            String email = repl.scanWord();
            repl.printMsg("Enter your password:");
            String password = repl.scanWord();

            try {
                authToken = serverFacade.register(username, password, email).authToken();
                state = State.LOGGEDIN;
                repl.printMsg("Welcome " + username);
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void logout() {
        if (state == State.LOGGEDIN) {
            try {
                serverFacade.logout(authToken);
                authToken = null;
                state = State.LOGGEDOUT;
                repl.printMsg("Thanks for playing!");
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void create() {
        if (state == State.LOGGEDIN) {
            repl.printMsg("Enter the name of your game:");
            String name = repl.scanWord();
            try {
                serverFacade.createGame(name, authToken);
                repl.printMsg("Game " + name + " has been created");
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void list() {
        if (state == State.LOGGEDIN) {
            try {
                this.gameList = serverFacade.listGames(authToken).games();
                if (this.gameList.isEmpty()) {
                    repl.printMsg("There are no games in the database");
                    return;
                }
                int i = 0;
                while (i < this.gameList.size()) {
                    GameData game = this.gameList.get(i);
                    i++;
                    String white = game.whiteUsername() == null ? "none" : game.whiteUsername();
                    String black = game.blackUsername() == null ? "none" : game.blackUsername();
                    String gameStr = i + ". " + game.gameName() +
                            "\n\tWhite Player: " + white +
                            "\n\tBlack Player: " + black;
                    repl.printMsg(gameStr);
                }
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void join() {
        if (state == State.LOGGEDIN) {
            if (gameList == null) {
                repl.printErr("You must list the games before you can choose one to join");
                return;
            }
            repl.printMsg("Enter the number of the game you wish to join:");
            int index = Integer.parseInt(repl.scanWord()) - 1;
            int gameID = gameList.get(index).gameID();

            repl.printMsg("Enter the color you wish to play as (white/black):");
            String colorStr = repl.scanWord();
            if (!(colorStr.equals("white") || colorStr.equals("black"))) {
                repl.printErr("invalid color");
                return;
            }

            try {
                serverFacade.joinGame(colorStr, gameID, authToken);

                ChessGame game = gameList.get(index).game();

                TeamColor color = colorStr.equals("white") ? TeamColor.WHITE : TeamColor.BLACK;
                repl.printMsg(Drawer.drawBoard(game.getBoard(), color));

                gameplayClient = new GameplayClient(game, color, repl);
                state = State.GAMEPLAY;
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }

    }

    private void observe() {
        if (state == State.LOGGEDIN) {
            if (gameList == null) {
                repl.printErr("You must list the games before you can choose one to observe");
                return;
            }
            repl.printMsg("Enter the number of the game you wish to observe:");
            int index = Integer.parseInt(repl.scanWord()) - 1;
            int gameID = gameList.get(index).gameID();

            try {
                serverFacade.joinGame(null, gameID, authToken);

                ChessGame game = gameList.get(index).game();
                repl.printMsg(Drawer.drawBoard(game.getBoard(), TeamColor.WHITE));

                gameplayClient = new GameplayClient(game, null, repl);
                state = State.GAMEPLAY;
            }
            catch (ServerException ex) {
                repl.printErr("Error code " + ex.getStatus() + ": " + ex.getMessage());
            }
            catch (IOException ex) {
                repl.printErr(ex.getMessage());
            }
        }
        else {
            repl.printErr("invalid instruction");
        }
    }
}
