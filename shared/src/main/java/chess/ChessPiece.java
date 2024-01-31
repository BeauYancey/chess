package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    private boolean addMove(HashSet<ChessMove> moveList, ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if (board.getPiece(newPosition) == null) {
            moveList.add(new ChessMove(myPosition, newPosition, null));
            return true; // continue
        } else {
            if (board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                moveList.add(new ChessMove(myPosition, newPosition, null));
            }
            return false; // stop going that direction
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
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] dir : directions) {
                for (int mul = 1; mul <= 8; mul++) {
                    int row = myPosition.getRow() + (mul * dir[0]);
                    int col = myPosition.getColumn() + (mul * dir[1]);

                    if (row < 1 || row > 8 || col < 1 || col > 8) {
                        break;
                    }

                    ChessPosition newPosition = new ChessPosition(row, col);
                    if (!addMove(moveList, board, myPosition, newPosition)) {
                        break;
                    }
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
                    addMove(moveList, board, myPosition, newPosition);
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
                addMove(moveList, board, myPosition, newPosition);
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
            else {
                if (row == 1) {
                    return moveList;
                }
                ChessPosition forward = new ChessPosition(row - 1, col);
                if (board.getPiece(forward) == null) { // can move forward
                    if (row == 2) { // is promoted
                        for (ChessPiece.PieceType p : promos) {
                            moveList.add(new ChessMove(myPosition, forward, p));
                        }
                    } else { // is not promoted
                        moveList.add(new ChessMove(myPosition, forward, null));
                        if (row == 7) { // initial move can be two spaces
                            ChessPosition twoSpaces = new ChessPosition(row - 2, col);
                            if (board.getPiece(twoSpaces) == null) {
                                moveList.add(new ChessMove(myPosition, twoSpaces, null));
                            }
                        }
                    }
                }
                ChessPosition left = new ChessPosition(row - 1, col - 1);
                if (col != 1) { // not on the left edge
                    if (board.getPiece(left) != null && board.getPiece(left).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if (row == 2) { // is promoted
                            for (ChessPiece.PieceType p : promos) {
                                moveList.add(new ChessMove(myPosition, left, p));
                            }
                        } else { // is not promoted
                            moveList.add(new ChessMove(myPosition, left, null));
                        }
                    }
                }
                ChessPosition right = new ChessPosition(row - 1, col + 1);
                if (col != 8) { // not on the right edge
                    if (board.getPiece(right) != null && board.getPiece(right).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if (row == 2) { // is promoted
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
        if (type == PieceType.QUEEN) {
            int[][] directions = {{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1}};
            for (int[] dir : directions) {
                for (int mul = 1; mul <= 8; mul++) {
                    int row = myPosition.getRow() + (mul * dir[0]);
                    int col = myPosition.getColumn() + (mul * dir[1]);

                    if (row < 1 || row > 8 || col < 1 || col > 8) {
                        break;
                    }

                    ChessPosition newPosition = new ChessPosition(row, col);
                    if (!addMove(moveList, board, myPosition, newPosition)) {
                        break;
                    }
                }
            }
        }
        if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
            for (int[] dir : directions) {
                for (int mul = 1; mul <= 8; mul++) {
                    int row = myPosition.getRow() + (mul * dir[0]);
                    int col = myPosition.getColumn() + (mul * dir[1]);

                    if (row < 1 || row > 8 || col < 1 || col > 8) {
                        break;
                    }

                    ChessPosition newPosition = new ChessPosition(row, col);
                    if (!addMove(moveList, board, myPosition, newPosition)) {
                        break;
                    }
                }
            }
        }
        return moveList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public ChessPiece clone() {
        return new ChessPiece(pieceColor, type);
    }
}
