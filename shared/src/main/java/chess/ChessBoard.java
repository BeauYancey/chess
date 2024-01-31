package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }

        // set up white side
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }

        // set up black side
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public ChessBoard clone() {
        ChessBoard c = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8 ; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                c.addPiece(pos, this.getPiece(pos).clone());
            }
        }
        return c;
    }

    @Override
    public String toString() {
        String boardString = "\n";

        for (int i = 7; i >= 0; i--) {
            ChessPiece[] row = squares[i];
            boardString += "|";
            for (ChessPiece piece : row) {
                if (piece == null) {
                    boardString += " |";
                }
                else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        boardString += "\u2656|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        boardString += "\u2658|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        boardString += "\u2657|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        boardString += "\u2654|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        boardString += "\u2655|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        boardString += "\u2659|";
                    }
                } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                        boardString += "\u265c|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        boardString += "\u265e|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                        boardString += "\u265d|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        boardString += "\u265a|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                        boardString += "\u265b|";
                    } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        boardString += "\u265f|";
                    }
                }
            }
            boardString += "\n";
        }

        return boardString;
    }
}
