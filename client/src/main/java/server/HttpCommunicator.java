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
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader responseBody = new InputStreamReader(connection.getInputStream())) {
                return gson.fromJson(responseBody, responseClass);
            }
        }
        else {
            MessageContainer msg;
            try (InputStreamReader responseBody = new InputStreamReader(connection.getErrorStream())) {
                msg = gson.fromJson(responseBody, MessageContainer.class);
            }
            throw new ServerException(responseCode, msg.message);
        }

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
            requestBody.write(gson.toJson(req));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader responseBody = new InputStreamReader(connection.getInputStream())) {
                return gson.fromJson(responseBody, responseClass);
            }
        }
        else {
            MessageContainer msg;
            try (InputStreamReader responseBody = new InputStreamReader(connection.getErrorStream())) {
                msg = gson.fromJson(responseBody, MessageContainer.class);
            }
            throw new ServerException(responseCode, msg.message);
        }
    }

    public void doPut(String path, Object req, String authToken) throws IOException, ServerException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        try(OutputStreamWriter requestBody = new OutputStreamWriter(connection.getOutputStream())) {
            requestBody.write(gson.toJson(req));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            MessageContainer msg;
            try (InputStreamReader responseBody = new InputStreamReader(connection.getErrorStream())) {
                msg = gson.fromJson(responseBody, MessageContainer.class);
            }
            throw new ServerException(responseCode, msg.message);
        }
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
            MessageContainer msg;
            try (InputStreamReader responseBody = new InputStreamReader(connection.getErrorStream())) {
                msg = gson.fromJson(responseBody, MessageContainer.class);
            }
            throw new ServerException(responseCode, msg.message);
        }
    }
}
