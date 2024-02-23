package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData setWhite(String username) {
        return new GameData(gameID, username, blackUsername, gameName, game);
    }

    public GameData setBlack(String username) {
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
}
