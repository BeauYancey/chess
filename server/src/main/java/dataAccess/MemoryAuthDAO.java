package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;

public class MemoryAuthDAO implements AuthDAO {

    private ArrayList<AuthData> authDatabase;
    private int size;
    public MemoryAuthDAO() {
        authDatabase = new ArrayList<>();
        size = 0;
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (int i = 0; i < size; i++) {
            if (authDatabase.get(i).authToken().equals(authToken)) {
                return authDatabase.get(i);
            }
        }
        return null;
    }

    @Override
    public void addAuth(AuthData authData) {
        authDatabase.add(authData);
        size++;
    }

    @Override
    public void removeAuth(String authToken) {
        for (int i = 0; i < size; i++) {
            if (authDatabase.get(i).authToken().equals(authToken)) {
                authDatabase.remove(i);
                size--;
                return;
            }
        }
    }

    @Override
    public void removeAll() {
        authDatabase.clear();
        size = 0;
    }

    public List<AuthData> listAll() {
        return authDatabase;
    }
}
