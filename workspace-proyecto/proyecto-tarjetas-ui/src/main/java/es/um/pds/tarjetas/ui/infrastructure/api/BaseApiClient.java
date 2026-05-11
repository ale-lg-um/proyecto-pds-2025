package es.um.pds.tarjetas.ui.infrastructure.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseApiClient {
	
	// Atributos
	
	private final String baseURL;
    private final HttpClient http = HttpClient.newHttpClient();
    protected final ObjectMapper json = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    													  .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 

    // Constructor
    
    public BaseApiClient(String baseURL) {
    	this.baseURL = baseURL;
    }
    
    // Métodos HTTP
    
    protected HttpResponse<String> get(String uri) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> get(String uri, String token) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }
    
    protected <T> T get(String uri, Class<T> clazz) throws IOException, InterruptedException {
        return json.readValue(get(uri).body(), clazz);
    }

    protected HttpResponse<String> post(String uri, Object body) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> post(String uri, String token, Object body) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected <T> T post(String uri, Object body, Class<T> clazz) throws IOException, InterruptedException {
        return json.readValue(post(uri, body).body(), clazz);
    }

    protected HttpResponse<String> put(String uri, Object body) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)))
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> put(String uri, String token, Object body) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)))
                .build();
		System.out.println(json.writeValueAsString(body));
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected <T> T put(String uri, Object body, Class<T> clazz) throws IOException, InterruptedException {
        return json.readValue(put(uri, body).body(), clazz);
    }

    protected HttpResponse<String> delete(String uri, String token) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + uri))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
