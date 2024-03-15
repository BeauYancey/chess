package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import requestResponse.LoginRequest;
import requestResponse.LoginResponse;
import requestResponse.RegisterRequest;
import requestResponse.RegisterResponse;
import exception.ServerException;

import java.util.UUID;

public class UserService {

    public static RegisterResponse register(RegisterRequest request, AuthDAO authDAO, UserDAO userDAO)
            throws ServerException, DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ServerException(400, "Error: bad request");
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new ServerException(403, "Error: already taken");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(request.password());

        userDAO.addUser(new UserData(request.username(), hashedPassword, request.email()));
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(authToken, request.username()));

        return new RegisterResponse(request.username(), authToken);
    }

    public static LoginResponse login(LoginRequest request, AuthDAO authDAO, UserDAO userDAO)
            throws ServerException, DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new ServerException(400, "Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user == null || !encoder.matches(request.password(), user.password())) {
            throw new ServerException(401, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, request.username());
        authDAO.addAuth(newAuth);

        return new LoginResponse(request.username(), authToken);
    }

    public static void logout(String authToken, AuthDAO authDAO) throws ServerException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }

        authDAO.removeAuth(authToken);
    }
}
