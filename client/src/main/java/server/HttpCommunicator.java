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

    class MessageContainer {
        public String message;
        public MessageContainer(String msg) {
            this.message = msg;
        }
    }

    public <T> T doGet(String path, String authToken, Class<T> responseClass) throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        int responseCode = connection.getResponseCode();
        return readResponse(responseCode, responseClass, connection);
    }

    private <T> T readResponse(int responseCode, Class<T> responseClass, HttpURLConnection connection)
            throws IOException, ServerException {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader responseBody = new InputStreamReader(connection.getInputStream())) {
                return gson.fromJson(responseBody, responseClass);
            }
        }
        else {
            readError(responseCode, connection);
            return null;
        }
    }

    private void readError(int responseCode, HttpURLConnection connection) throws IOException, ServerException {
        MessageContainer msg;
        try (InputStreamReader responseBody = new InputStreamReader(connection.getErrorStream())) {
            msg = gson.fromJson(responseBody, MessageContainer.class);
        }
        throw new ServerException(responseCode, msg.message);
    }

    public <T> T doPost(String path, Object req, String authToken, Class<T> responseClass)
            throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");

        int responseCode = sendRequest(authToken, req, connection);
        return readResponse(responseCode, responseClass, connection);
    }

    public void doPut(String path, Object req, String authToken) throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("PUT");

        int responseCode = sendRequest(authToken, req, connection);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            readError(responseCode, connection);
        }
    }

    int sendRequest(String auth, Object req, HttpURLConnection connection) throws IOException{
        connection.setDoOutput(true);

        if (auth != null) {
            connection.setRequestProperty("Authorization", auth);
        }

        try(OutputStreamWriter requestBody = new OutputStreamWriter(connection.getOutputStream())) {
            requestBody.write(gson.toJson(req));
        }
        return connection.getResponseCode();
    }

    public void doDelete(String path, String authToken) throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            readError(responseCode, connection);
        }
    }
}
