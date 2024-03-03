package dataAccess;

import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {

    }

    @Override
    public void removeAll() {

    }
}
