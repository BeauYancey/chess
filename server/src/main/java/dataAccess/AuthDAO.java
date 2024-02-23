package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void removeAuth(String authToken);
    void removeAll();
}
