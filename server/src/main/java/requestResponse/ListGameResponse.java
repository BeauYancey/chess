package requestResponse;

import model.GameData;
import java.util.List;

public record ListGameResponse(List<GameData> gameList) {}
