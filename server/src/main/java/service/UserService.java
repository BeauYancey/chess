package service;

import model.AuthData;
import requestResponse.LoginRequest;
import requestResponse.LoginResponse;
import requestResponse.RegisterRequest;
import requestResponse.RegisterResponse;

import java.util.UUID;

public class UserService {

    public static RegisterResponse register(RegisterRequest request) {
        // make sure the username isn't already in use
        // add information to the user database
        // add information to auth database
        String authToken = UUID.randomUUID().toString();

        return new RegisterResponse(authToken);
    }

    public static LoginResponse login(LoginRequest request) {
        // make sure the user exists, confirm password
        // add information to the auth database
        String authToken = UUID.randomUUID().toString();

        return new LoginResponse(request.username(), authToken);
    }

    public static void logout(String authToken) {
        // check that the user exists and is logged in
        AuthData user = verifyAuth(authToken);
        // remove entry from auth database
    }

    public static AuthData verifyAuth (String authToken) {
        return new AuthData("authToken", "testUser");
    }
}
