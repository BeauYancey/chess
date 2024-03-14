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
    private PieceType type;

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

    public void promote(PieceType promotionPiece) {
        type = promotionPiece;
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
            continueGivenDirections(board, myPosition, moveList, directions);
        }
        if (type == PieceType.KING) {
            int[][] moves = {{-1,-1}, {-1,0}, {-1,1}, {0,1}, {1,1}, {1,0}, {1,-1}, {0,-1}};
            addGivenMoves(board, myPosition, moveList, moves);
        }
        if (type == PieceType.KNIGHT) {
            int[][] moves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
            addGivenMoves(board, myPosition, moveList, moves);
        }
        if (type == PieceType.PAWN) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessPiece.PieceType[] promos = {PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN, PieceType.BISHOP};

            int begin = 2;
            int end = 8;
            int dir = 1;
            ChessGame.TeamColor enemy = ChessGame.TeamColor.BLACK;

            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                begin = 7;
                end = 1;
                dir = -1;
                enemy = ChessGame.TeamColor.WHITE;
            }

            if (row == end) {
                return moveList;
            }
            ChessPosition forward = new ChessPosition(row + dir, col);
            if (board.getPiece(forward) == null) { // can move forward
                if (row == end - dir) { // is promoted
                    for (ChessPiece.PieceType p : promos) {
                        moveList.add(new ChessMove(myPosition, forward, p));
                    }
                } else { // is not promoted
                    moveList.add(new ChessMove(myPosition, forward, null));
                    if (row == begin) { // initial move can be two spaces
                        ChessPosition twoSpaces = new ChessPosition(row + (dir * 2), col);
                        if (board.getPiece(twoSpaces) == null) {
                            moveList.add(new ChessMove(myPosition, twoSpaces, null));
                        }
                    }
                }
            }
            ChessPosition left = new ChessPosition(row + dir, col - 1);
            if (col != 1) { // not on the left edge
                if (board.getPiece(left) != null && board.getPiece(left).getTeamColor() == enemy) {
                    if (row == end - dir) { // is promoted
                        for (ChessPiece.PieceType p : promos) {
                            moveList.add(new ChessMove(myPosition, left, p));
                        }
                    } else { // is not promoted
                        moveList.add(new ChessMove(myPosition, left, null));
                    }
                }
            }
            ChessPosition right = new ChessPosition(row + dir, col + 1);
            if (col != 8) { // not on the right edge
                if (board.getPiece(right) != null && board.getPiece(right).getTeamColor() == enemy) {
                    if (row == end - dir) { // is promoted
                        for (ChessPiece.PieceType p : promos) {
                            moveList.add(new ChessMove(myPosition, right, p));
                        }
                    } else { // is not promoted
                        moveList.add(new ChessMove(myPosition, right, null));
                    }
                }
            }
        }
        if (type == PieceType.QUEEN) {
            int[][] directions = {{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1}};
            continueGivenDirections(board, myPosition, moveList, directions);
        }
        if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
            continueGivenDirections(board, myPosition, moveList, directions);
        }
        return moveList;
    }

    private void addGivenMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moveList,
                               int[][] moves) {
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

    private void continueGivenDirections(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moveList,
                                         int[][] directions) {
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
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (type == ChessPiece.PieceType.ROOK) {
                return "\u2656";
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                return "\u2658";
            } else if (type == ChessPiece.PieceType.BISHOP) {
                return "\u2657";
            } else if (type == ChessPiece.PieceType.KING) {
                return "\u2654";
            } else if (type == ChessPiece.PieceType.QUEEN) {
                return "\u2655";
            } else if (type == ChessPiece.PieceType.PAWN) {
                return "\u2659";
            }
        } else if (pieceColor == ChessGame.TeamColor.BLACK) {
            if (type == ChessPiece.PieceType.ROOK) {
                return "\u265c";
            } else if (type == ChessPiece.PieceType.KNIGHT) {
                return "\u265e";
            } else if (type == ChessPiece.PieceType.BISHOP) {
                return "\u265d";
            } else if (type == ChessPiece.PieceType.KING) {
                return "\u265a";
            } else if (type == ChessPiece.PieceType.QUEEN) {
                return "\u265b";
            } else if (type == ChessPiece.PieceType.PAWN) {
                return "\u265f";
            }
        }
        return null;
    }
}
