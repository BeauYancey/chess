package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import requestResponse.LoginRequest;
import requestResponse.LoginResponse;
import requestResponse.RegisterRequest;
import requestResponse.RegisterResponse;
import service.exception.Exception400;
import service.exception.Exception403;

import java.util.UUID;

public class UserService {

    public static RegisterResponse register(RegisterRequest request, AuthDAO auths, UserDAO users) throws Exception400, Exception403 {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new Exception400();
        }
        if (users.getUser(request.username()) != null) {
            throw new Exception403();
        }

        users.addUser(new UserData(request.username(), request.password(), request.email()));
        String authToken = UUID.randomUUID().toString();
        auths.addAuth(new AuthData(authToken, request.username()));

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
