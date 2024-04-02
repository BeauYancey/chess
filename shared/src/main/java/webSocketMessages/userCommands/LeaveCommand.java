package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
    int gameID;
    public LeaveCommand(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
