package Client;

import Exceptions.ManagerLoadException;
import Exceptions.ManagerSaveException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private static final String URN_REGISTER = "/register";
    private String url;
    private String apiToken;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url + URN_REGISTER))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка" + response.statusCode());
            }
            return response.body();
        } catch (Exception exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    public void put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка сохранения, статус код: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    public String load(String key) throws ManagerLoadException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new ManagerLoadException("Ошибка при загрузке файла");
        }
    }
}

