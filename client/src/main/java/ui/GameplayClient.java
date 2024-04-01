package ui;

import chess.ChessGame;
import chess.ChessPosition;

public class GameplayClient {
    ChessGame.TeamColor color;
    ChessGame game;
    Repl repl;

    public GameplayClient(ChessGame game, ChessGame.TeamColor color, Repl repl) {
        this.color = color;
        this.game = game;
        this.repl = repl;
    }

    public State eval(String input) {
        switch (input) {
            case "help" -> help();
            case "draw" -> draw();
            case "move" -> move();
            case "highlight" -> highlight();
            case "resign" -> resign();
            case "leave" -> leave();
            default -> repl.printErr("invalid instruction");
        }
        if (input.equals("leave")) {
            return State.LOGGEDIN;
        }
        else {
            return State.GAMEPLAY;
        }
    }

    private void help() {
        repl.printMsg("Choose from one of the following options:\n" +
                "\tHelp: see this message again\n" +
                "\tDraw: Re-draw the chessboard\n" +
                "\tMove: Make a move\n" +
                "\tHighlight: Highlight all legal moves for a specified piece\n" +
                "\tResign: Forfeit the match and end the game\n" +
                "\tLeave: Leave the game");
    }

    private void draw() {
        repl.printMsg(Drawer.drawBoard(game.getBoard(), color));
    }

    private void move() {
        repl.printMsg("Enter the position of the piece you would like to move (i.e. d4).");
        ChessPosition startPosition = repl.scanPosition();
        repl.printMsg("Enter the position you would like to move this piece to (i.e. d4).");
        ChessPosition endPosition = repl.scanPosition();
    }

    private void highlight() {
        repl.printMsg("Enter the position of the piece you would like to see move moves for.");
        ChessPosition position = repl.scanPosition();
        ChessGame.TeamColor color = this.color == null ? ChessGame.TeamColor.WHITE : this.color;
        repl.printMsg(Drawer.drawBoard(game.getBoard(), color, position));
    }

    private void resign() {
        repl.printMsg("You entered resign");
    }

    private void leave() {
        repl.printMsg("You entered leave");
    }
}
