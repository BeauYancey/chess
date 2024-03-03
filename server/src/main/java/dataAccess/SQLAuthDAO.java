package dataAccess;

import dataAccess.AuthDAO;
import dataAccess.DatabaseManager;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void removeAuth(String authToken) {

    }

    @Override
    public void removeAll() {

    }
}
