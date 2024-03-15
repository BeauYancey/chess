package ui;

import static ui.EscapeSequences.*;
import chess.*;
import chess.ChessGame.TeamColor;
import exception.ServerException;
import server.ServerFacade;

public class Client {
    private final String serverURL;
    private final ServerFacade serverFacade;
    private final Repl repl;
    private State state = State.LOGGEDOUT;
    private String authToken = null;

    public Client(String serverURL, Repl repl) {
        this.serverURL = serverURL;
        serverFacade = new ServerFacade(serverURL);
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

            try {
                authToken = serverFacade.login(username, password).authToken();
            }
            catch (Exception ex) {
                repl.printErr(ex.getMessage());
                return;
            }

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

            try {
                authToken = serverFacade.register(username, password, email).authToken();
            }
            catch (Exception ex) {
                repl.printErr(ex.getMessage());
                return;
            }

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
            TeamColor color;
            if (colorStr.equals("white")) {
                color = TeamColor.WHITE;
            }
            else if (colorStr.equals("black")) {
                color = TeamColor.BLACK;
            }
            else {
                repl.printErr("invalid color");
                return;
            }

            ChessGame game = new ChessGame();
            drawBoard(game.getBoard(), TeamColor.WHITE);
            drawBoard(game.getBoard(), TeamColor.BLACK);
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
            try {
                game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null));
            }
            catch (InvalidMoveException ex) {
                repl.printErr("Invalid Move:");
                repl.printErr(ex.getMessage());
            }
            drawBoard(game.getBoard(), TeamColor.WHITE);
            drawBoard(game.getBoard(), TeamColor.BLACK);
        }
        else {
            repl.printErr("invalid instruction");
        }
    }

    private void drawBoard(ChessBoard board, TeamColor team) {
        String brd = "";
        brd += drawHeader(team);
        for (int i = 1; i <= 8; i++) {
            brd += drawRow(board, i, team);
        }
        brd += drawHeader(team);
        brd += RESET_BG_COLOR + RESET_TEXT_COLOR;
        repl.printMsg(brd);
    }

    private String drawHeader(TeamColor team) {

        if (team == TeamColor.WHITE) {
            return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        }
        else {
            return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        }
    }

    private String drawRow(ChessBoard board, int rowIndex, TeamColor team) {
        if (team == TeamColor.WHITE) {
            rowIndex = 9 - rowIndex;
        }

        String row = "";
        row += SET_BG_COLOR_WHITE + " " + rowIndex + " ";

        if (team == TeamColor.WHITE) {
            for (int col = 1; col <= 8; col++) {
                String bgColor = ((rowIndex + col) % 2) == 1 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                row += drawSquare(board.getPiece(new ChessPosition(rowIndex, col)), bgColor);
            }
        }
        else {
            for (int col = 8; col >= 1; col--) {
                String bgColor = ((rowIndex + col) % 2) == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                row += drawSquare(board.getPiece(new ChessPosition(rowIndex, col)), bgColor);
            }
        }
        return row + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + rowIndex + " " + RESET_BG_COLOR + "\n";
    }

    private String drawSquare(ChessPiece piece, String bgColor) {
        if (piece == null) {
            return bgColor + EMPTY;
        }
        else {
            String textColor = piece.getTeamColor() == TeamColor.WHITE ?
                    SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
            return bgColor + textColor + " " + piece + " ";
        }
    }
}
