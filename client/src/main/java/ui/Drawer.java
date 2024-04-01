package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Drawer {
    public static String drawBoard(ChessBoard board, ChessGame.TeamColor team) {
        String brd = "";
        brd += drawHeader(team);
        for (int i = 1; i <= 8; i++) {
            brd += drawRow(board, i, team);
        }
        brd += drawHeader(team);
        brd += RESET_BG_COLOR + RESET_TEXT_COLOR;
        return brd;
    }

    public static String drawBoard(ChessBoard board, ChessGame.TeamColor team, ChessPosition self) {
        String brd = "";
        brd += drawHeader(team);
        Collection<ChessPosition> highlight = new ArrayList<>();
        for (ChessMove move : board.getPiece(self).pieceMoves(board, self)) {
            highlight.add(move.getEndPosition());
        }
        for (int i = 1; i <= 8; i++) {
            brd += drawRow(board, i, team, self, highlight);
        }
        brd += drawHeader(team);
        brd += RESET_BG_COLOR + RESET_TEXT_COLOR;
        return brd;
    }

    private static String drawHeader(ChessGame.TeamColor team) {

        if (team == ChessGame.TeamColor.WHITE) {
            return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        }
        else {
            return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        }
    }

    private static String drawRow(ChessBoard board, int rowIndex, ChessGame.TeamColor team) {
        if (team == ChessGame.TeamColor.WHITE) {
            rowIndex = 9 - rowIndex;
        }

        String row = "";
        row += SET_BG_COLOR_WHITE + " " + rowIndex + " ";

        if (team == ChessGame.TeamColor.WHITE) {
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

    private static String drawRow(ChessBoard board, int rowIndex, ChessGame.TeamColor team, ChessPosition self,
                                  Collection<ChessPosition> highlight) {
        if (team == ChessGame.TeamColor.WHITE) {
            rowIndex = 9 - rowIndex;
        }

        String row = "";
        row += SET_BG_COLOR_WHITE + " " + rowIndex + " ";

        if (team == ChessGame.TeamColor.WHITE) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(rowIndex, col);
                String bgColor;
                if (highlight.contains(position)) {
                    bgColor = ((rowIndex + col) % 2) == 1 ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                }
                else if (self.equals(position)) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }
                else {
                    bgColor = ((rowIndex + col) % 2) == 1 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
                row += drawSquare(board.getPiece(position), bgColor);
            }
        }

        else {
            for (int col = 8; col >= 1; col--) {
                ChessPosition position = new ChessPosition(rowIndex, col);
                String bgColor;
                if (highlight.contains(position)) {
                    bgColor = ((rowIndex + col) % 2) == 0 ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                }
                else if (self.equals(position)) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }
                else {
                    bgColor = ((rowIndex + col) % 2) == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
                row += drawSquare(board.getPiece(position), bgColor);
            }
        }
        return row + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " " + rowIndex + " " + RESET_BG_COLOR + "\n";
    }

    private static String drawSquare(ChessPiece piece, String bgColor) {
        if (piece == null) {
            return bgColor + EMPTY;
        }
        else {
            String textColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                    SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;
            return bgColor + textColor + " " + piece + " ";
        }
    }
}
