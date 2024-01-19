package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    private int validateAddMove(HashSet<ChessMove> moveList, ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if (board.getPiece(newPosition) == null) {
            moveList.add(new ChessMove(myPosition, newPosition, null));
            return 1;
        } else {
            if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                moveList.add(new ChessMove(myPosition, newPosition, null));
            }
            return -1;
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece.PieceType type = piece.getPieceType();
        HashSet<ChessMove> moveList = new HashSet<>();
        if (type == PieceType.BISHOP) {
            for (int i = 1; i < 8; i++) { // Go towards the top-right
                int row = myPosition.getRow() + i;
                int col = myPosition.getColumn() + i;

                if (row > 8 || col > 8) { break; } // Out of bounds

                ChessPosition newPosition = new ChessPosition(row, col);
                if (validateAddMove(moveList, board, myPosition, newPosition) == -1) {
                    break;
                }
            }
            for (int i = 1; i < 8; i++) { // Go towards the top-left
                int row = myPosition.getRow() + i;
                int col = myPosition.getColumn() - i;

                if (row > 8 || col < 1) { break; } // Out of bounds

                ChessPosition newPosition = new ChessPosition(row, col);
                if (validateAddMove(moveList, board, myPosition, newPosition) == -1) {
                    break;
                }
            }
            for (int i = 1; i < 8; i++) { // Go towards the bottom-right
                int row = myPosition.getRow() - i;
                int col = myPosition.getColumn() + i;

                if (row < 1 || col > 8) { break; } // Out of bounds

                ChessPosition newPosition = new ChessPosition(row, col);
                if (validateAddMove(moveList, board, myPosition, newPosition) == -1) {
                    break;
                }
            }
            for (int i = 1; i < 8; i++) { // Go towards the bottom-left
                int row = myPosition.getRow() - i;
                int col = myPosition.getColumn() - i;

                if (row < 1 || col < 1) { break; } // Out of bounds

                ChessPosition newPosition = new ChessPosition(row, col);
                if (validateAddMove(moveList, board, myPosition, newPosition) == -1) {
                    break;
                }
            }
        }
        if (type == PieceType.KING) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    int row = myPosition.getRow() + i;
                    int col = myPosition.getColumn() + j;

                    if (row > 8 || row < 1 || col > 8 || col < 1) {
                        continue;
                    }
                    ChessPosition newPosition = new ChessPosition(row, col);
                    validateAddMove(moveList, board, myPosition, newPosition);
                }
            }
        }
        if (type == PieceType.KNIGHT) {
            int[][] moves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
            for (int[] i : moves) {
                int row = myPosition.getRow() + i[0];
                int col = myPosition.getColumn() + i[1];

                if (row > 8 || row < 1 || col > 8 || col < 1) {
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(row, col);
                validateAddMove(moveList, board, myPosition, newPosition);
            }
        }
        if (type == PieceType.PAWN) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessPiece.PieceType[] promos = {PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN, PieceType.BISHOP};

            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (row == 8) {
                    return moveList;
                }
                ChessPosition forward = new ChessPosition(row + 1, col);
                if (board.getPiece(forward) == null) { // can move forward
                    if (row == 7) { // is promoted
                        for (ChessPiece.PieceType p : promos) {
                            moveList.add(new ChessMove(myPosition, forward, p));
                        }
                    } else { // is not promoted
                        moveList.add(new ChessMove(myPosition, forward, null));
                        if (row == 2) { // initial move can be two spaces
                            ChessPosition twoSpaces = new ChessPosition(row + 2, col);
                            if (board.getPiece(twoSpaces) == null) {
                                moveList.add(new ChessMove(myPosition, twoSpaces, null));
                            }
                        }
                    }
                }
                ChessPosition left = new ChessPosition(row + 1, col - 1);
                if (col != 1) { // not on the left edge
                    if (board.getPiece(left) != null && board.getPiece(left).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (row == 7) { // is promoted
                            for (ChessPiece.PieceType p : promos) {
                                moveList.add(new ChessMove(myPosition, left, p));
                            }
                        } else { // is not promoted
                            moveList.add(new ChessMove(myPosition, left, null));
                        }
                    }
                }
                ChessPosition right = new ChessPosition(row + 1, col + 1);
                if (col != 8) { // not on the right edge
                    if (board.getPiece(right) != null && board.getPiece(right).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (row == 7) { // is promoted
                            for (ChessPiece.PieceType p : promos) {
                                moveList.add(new ChessMove(myPosition, right, p));
                            }
                        } else { // is not promoted
                            moveList.add(new ChessMove(myPosition, right, null));
                        }
                    }
                }
            }
        }
        if (type == PieceType.QUEEN) {}
        if (type == PieceType.ROOK) {}
        return moveList;
    }
}
