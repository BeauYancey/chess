package ui;

import chess.ChessGame;

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
        repl.printMsg("You entered draw");
    }

    private void move() {
        repl.printMsg("You entered move");
    }

    private void highlight() {
        repl.printMsg("You entered highlight");
    }

    private void resign() {
        repl.printMsg("You entered resign");
    }

    private void leave() {
        repl.printMsg("You entered leave");
    }
}
