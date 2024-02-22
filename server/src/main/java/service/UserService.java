package service;

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
}
