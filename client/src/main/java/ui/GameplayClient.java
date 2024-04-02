package ui;

import chess.*;
import server.ServerFacade;

public class GameplayClient {
    ChessGame.TeamColor color;
    ChessGame game;
    Repl repl;
    ServerFacade facade;

    public GameplayClient(ChessGame game, ChessGame.TeamColor color, Repl repl, ServerFacade facade) {
        this.color = color;
        this.game = game;
        this.repl = repl;
        this.facade = facade;
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
        if (color != null) {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tDraw: Re-draw the chessboard\n" +
                    "\tMove: Make a move\n" +
                    "\tHighlight: Highlight all legal moves for a specified piece\n" +
                    "\tResign: Forfeit the match and end the game\n" +
                    "\tLeave: Leave the game");
        }
        else {
            repl.printMsg("Choose from one of the following options:\n" +
                    "\tHelp: see this message again\n" +
                    "\tDraw: Re-draw the chessboard\n" +
                    "\tHighlight: Highlight all legal moves for a specified piece\n" +
                    "\tLeave: Leave the game");
        }
    }

    private void draw() {
        repl.printMsg(Drawer.drawBoard(game.getBoard(), color));
    }

    private void move() {
        if (this.color == null) {
            repl.printErr("invalid instruction");
            return;
        }
        repl.printMsg("Enter the position of the piece you would like to move (i.e. d4).");
        ChessPosition startPosition = repl.scanPosition();
        repl.printMsg("Enter the position you would like to move this piece to (i.e. d4).");
        ChessPosition endPosition = repl.scanPosition();
        repl.printMsg("If this move results in a promotion, enter the promotion piece. Otherwise, press enter");
        String pieceStr = repl.scanWord();
        ChessPiece.PieceType promo = strToPiece(pieceStr);
        ChessMove move = new ChessMove(startPosition, endPosition, promo);

        if (!game.getBoard().getPiece(startPosition).pieceMoves(game.getBoard(), startPosition).contains(move)) {
            repl.printErr("Invalid move. Please enter a gameplay command to continue.");
        }
    }

    private void highlight() {
        repl.printMsg("Enter the position of the piece you would like to see move moves for.");
        ChessPosition position = repl.scanPosition();
        ChessGame.TeamColor color = this.color == null ? ChessGame.TeamColor.WHITE : this.color;
        repl.printMsg(Drawer.drawBoard(game.getBoard(), color, position));
    }

    private void resign() {
        if (this.color == null) {
            repl.printErr("invalid instruction");
            return;
        }
        repl.printMsg("You entered resign");
    }

    private void leave() {
        repl.printMsg("You entered leave");
    }

    private ChessPiece.PieceType strToPiece(String pieceStr) {
        return switch (pieceStr) {
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            default -> null;
        };
    }
}
