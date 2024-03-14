package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class Client {
    private final String serverURL;
    private final Repl repl;
    private State state = State.LOGGEDOUT;

    public Client(String serverURL, Repl repl) {
        this.serverURL = serverURL;
        this.repl = repl;
    }

    public void eval(String input){
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

            state = State.LOGGEDIN;
            repl.printMsg("Welcome " + username);
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

            state = State.LOGGEDIN;
            repl.printMsg("Welcome " + username);
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void logout() {
        if (state == State.LOGGEDIN) {
            state = State.LOGGEDOUT;
            repl.printMsg("Thanks for playing!");
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void create() {
        if (state == State.LOGGEDIN) {
            repl.printMsg("Enter the name of your game:");
            String name = repl.scanWord();
            repl.printMsg("Game " + name + " has been created");
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void list() {
        if (state == State.LOGGEDIN) {
            repl.printMsg("1. game-name\n" +
                    "\tWhite: white-user\n" +
                    "\tBlack: black-user");
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void join() {
        if (state == State.LOGGEDIN) {
            repl.printMsg("Enter the number of the game you wish to join:");
            int gameNum = Integer.parseInt(repl.scanWord());
            repl.printMsg("Enter the color you wish to play as (white/black):");
            String colorStr = repl.scanWord();
            ChessGame.TeamColor color;
            if (colorStr.equals("white")) {
                color = ChessGame.TeamColor.WHITE;
            }
            else if (colorStr.equals("black")) {
                color = ChessGame.TeamColor.BLACK;
            }
            else {
                repl.printErr("invalid color");
                return;
            }

            ChessGame game = new ChessGame();
            drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
            drawBoard(game.getBoard(), ChessGame.TeamColor.BLACK);
        }
        else {
            repl.printErr("invalid instruction");
        }

    }

    private void observe() {
        if (state == State.LOGGEDIN) {
            repl.printMsg("Enter the number of the game you wish to observe:");
            int gameNum = Integer.parseInt(repl.scanWord());

            ChessGame game = new ChessGame();
            drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
            drawBoard(game.getBoard(), ChessGame.TeamColor.BLACK);
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void drawBoard(ChessBoard board, ChessGame.TeamColor team) {
        repl.printMsg("Drawing board");
    }
}
