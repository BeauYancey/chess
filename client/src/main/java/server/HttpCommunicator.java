package server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.*;
import exception.ServerException;

public class HttpCommunicator {

    private final Gson gson;
    public HttpCommunicator() {
        gson = new Gson();
    }
    public <T> T doPost(String path, Object req, String authToken, Class<T> responseClass)
            throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        try(OutputStreamWriter requestBody = new OutputStreamWriter(connection.getOutputStream())) {
            // Write request body to OutputStream ...
            requestBody.write(gson.toJson(req));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader responseBody = new InputStreamReader(connection.getInputStream())) {
                return gson.fromJson(responseBody, responseClass);
            }
        }
        else {
            throw new ServerException(responseCode);
        }
    }
}
