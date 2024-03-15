package server;

import exception.ServerException;
import requestResponse.*;

import java.io.IOException;

public class ServerFacade {
    private final String url;
    private final HttpCommunicator communicator;

    public ServerFacade(String url) {
        this.url = url;
        communicator = new HttpCommunicator();
    }

    public LoginResponse login(String username, String password)
            throws ServerException, IOException {
        LoginRequest req = new LoginRequest(username, password);
        LoginResponse res = communicator.doPost(url + "/session", req, null, LoginResponse.class);
        return res;
    }

    public RegisterResponse register(String username, String password, String email)
            throws ServerException, IOException {
        RegisterRequest req = new RegisterRequest(username, password, email);
        RegisterResponse res = communicator.doPost(url + "/user", req, null, RegisterResponse.class);
        return res;
    }

    public void logout(String authToken) throws ServerException, IOException {
        communicator.doDelete(url + "/session", authToken);
    }
}
