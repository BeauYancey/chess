package dataAccess;

import model.UserData;
import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private ArrayList<UserData> userDatabase;
    private int size;

    public MemoryUserDAO() {
        userDatabase = new ArrayList<>();
        size = 0;
    }
    @Override
    public UserData getUser(String username) {
        for (int i = 0; i < size; i++) {
            if (userDatabase.get(i).username().equals(username)) {
                return userDatabase.get(i);
            }
        }
        return null;
    }

    @Override
    public void addUser(UserData userData) {
        userDatabase.add(userData);
        size++;
    }

    @Override
    public void removeAll() {
        userDatabase.clear();
        size = 0;
    }
}
