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
import service.exception.Exception401;
import service.exception.Exception403;

import java.util.UUID;

public class UserService {

    public static RegisterResponse register(RegisterRequest request, AuthDAO authDAO, UserDAO userDAO)
            throws Exception400, Exception403 {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new Exception400();
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new Exception403();
        }

        userDAO.addUser(new UserData(request.username(), request.password(), request.email()));
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(authToken, request.username()));

        return new RegisterResponse(authToken);
    }

    public static LoginResponse login(LoginRequest request, AuthDAO authDAO, UserDAO userDAO)
            throws Exception400, Exception401 {
        if (request.username() == null || request.password() == null) {
            throw new Exception400();
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new Exception401();
        }

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, request.username());
        authDAO.addAuth(newAuth);

        return new LoginResponse(request.username(), authToken);
    }

    public static void logout(String authToken, AuthDAO authDAO) throws Exception401 {
        if (authDAO.getAuth(authToken) == null) {
            throw new Exception401();
        }

        authDAO.removeAuth(authToken);
    }
}
