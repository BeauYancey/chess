package server;

import dataAccess.*;
import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        AuthDAO auths = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();

        Spark.delete("/db", (Request req, Response res) -> ClearHandler.clear(req, res, auths, users, games));
        Spark.post("/user", (Request req, Response res) -> UserHandler.register(req, res, auths, users));
        Spark.post("/session", (Request req, Response res) -> UserHandler.login(req, res, auths, users));
        Spark.delete("/session", (Request req, Response res) -> UserHandler.logout(req, res, auths));
        Spark.get("/game", (Request req, Response res) -> GameHandler.listGames(req, res, auths, games));
        Spark.post("/game", (Request req, Response res) -> GameHandler.createGame(req, res, auths, games));
        Spark.put("/game", (Request req, Response res) -> GameHandler.joinGame(req, res, auths, games));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
