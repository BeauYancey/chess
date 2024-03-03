package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {
    public static void clear(AuthDAO auths, UserDAO users, GameDAO games) throws DataAccessException {
        auths.removeAll();
        users.removeAll();
        games.removeAll();
    }
}
