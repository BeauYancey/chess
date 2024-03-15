package server;

import exception.ServerException;
import requestResponse.LoginRequest;
import requestResponse.LoginResponse;
import requestResponse.RegisterRequest;
import requestResponse.RegisterResponse;

import java.io.IOException;

public class ServerFacade {
    private final String url;
    private final HttpCommunicator communicator;

    public ServerFacade(String url) {
        this.url = url;
        communicator = new HttpCommunicator();
    }

    public LoginResponse login(String username, String password) throws Exception {
        LoginRequest req = new LoginRequest(username, password);
        LoginResponse res = communicator.doPost(url + "/session", req, null, LoginResponse.class);
        return res;
    }

    public RegisterResponse register(String username, String password, String email) throws Exception {
        RegisterRequest req = new RegisterRequest(username, password, email);
        RegisterResponse res = communicator.doPost(url + "/user", req, null, RegisterResponse.class);
        return res;
    }
}
