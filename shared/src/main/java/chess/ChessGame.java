package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor team;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();

        team = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : possibleMoves) {
            ChessPiece before = board.getPiece(move.getEndPosition());
            attemptMove(move);
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
            undoMove(move, before);
        }
        return validMoves;
    }

    private void attemptMove(ChessMove move){
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            piece.promote(move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());
    }

    private void undoMove(ChessMove move, ChessPiece before) {
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (move.getPromotionPiece() != null) {
            piece.promote(ChessPiece.PieceType.PAWN);
        }
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), before);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);
        if (piece.getTeamColor() != getTeamTurn()) {
            String msg = "You attempted to move a " + piece.getTeamColor() + " piece but the current turn is " + getTeamTurn();
            throw new InvalidMoveException(msg);
        }
        if (!validMoves(startPos).contains(move)) {
            String msg = piece.getPieceType() + " at " + move.getStartPosition() + " cannot move to " + move.getEndPosition();
            throw new InvalidMoveException(msg);
        }
        attemptMove(move);
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private Collection<ChessPosition> getAllPositions(TeamColor teamColor){
        HashSet<ChessPosition> allPositions = new HashSet<>();
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    allPositions.add(pos);
                }
            }
        }
        return allPositions;
    }

    private Collection<ChessMove> getAllMoves(TeamColor teamColor) {
        HashSet<ChessMove> allMoves = new HashSet<>();
        for (ChessPosition pos : getAllPositions(teamColor)) {
            ChessPiece piece = board.getPiece(pos);
            allMoves.addAll(piece.pieceMoves(board, pos));
        }
        return allMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemy = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (ChessMove move : getAllMoves(enemy)) {
            ChessPiece inPeril = board.getPiece(move.getEndPosition());
            if (inPeril != null && inPeril.getTeamColor() == teamColor && inPeril.getPieceType() == ChessPiece.PieceType.KING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        } else {
            Collection<ChessMove> allMoves = getAllMoves(teamColor);
            for (ChessMove move : allMoves) {
                ChessPiece before = board.getPiece(move.getEndPosition());
                attemptMove(move);
                if (!isInCheck(teamColor)) {
                    undoMove(move, before);
                    return false;
                }
                undoMove(move, before);
            }
            return true;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessPosition> allPositions = getAllPositions(teamColor);
        HashSet<ChessMove> moves = new HashSet<>();
        for (ChessPosition pos : allPositions) {
            moves.addAll(validMoves(pos));
        }
        if (moves.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
