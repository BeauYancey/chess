package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {
    public static void clear(AuthDAO auths, UserDAO users, GameDAO games) {
        auths.removeAll();
        users.removeAll();
        games.removeAll();
    }
}
