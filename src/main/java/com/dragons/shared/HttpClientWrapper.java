package com.dragons.shared;

import com.dragons.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class HttpClientWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientWrapper.class);

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public HttpClientWrapper(ObjectMapper mapper, HttpClient httpClient) {
        this.mapper = mapper;
        this.httpClient = httpClient;
    }

    /**
     * Makes a HTTP GET request to the given URI and deserializes the results to a list. URI is formed without query params
     */
    public <T> List<T> getList(Class<T[]> clazz, String url, Object... pathSegments) {
        return this.getList(clazz, new LinkedMultiValueMap<>(), url, pathSegments);
    }

    /**
     * Makes a HTTP GET request to the given URI and deserializes the results to a list. URI is formed with given query params
     */
    public <T> List<T> getList(Class<T[]> clazz, MultiValueMap<String, String> queryParams, String url, Object... pathSegments) {
        return List.of(this.get(clazz, queryParams, url, pathSegments));
    }

    /**
     * Makes a HTTP GET request to the given URI. URI is formed with query params
     */
    public <T> T get(Class<T> responseClazz, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.GET)
                .withUri(uri, pathSegments)
                .withQueryParams(queryParams)
                .build(responseClazz));
    }

    /**
     * Makes a HTTP POST request to the given URI
     */
    public <T> T post(Class<T> responseClazz, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.POST)
                .withUri(uri, pathSegments)
                .build(responseClazz));
    }

    public <T> T exchange(HttpRequestWrapper<T> request) {
        var response = this.execute(request);
        this.handleErrors(response, request.getUri());
        return this.extractBody(response.getBody(), request.getResponseType());
    }

    private ResponseEntity<byte[]> execute(HttpRequestWrapper<?> requestWrapper) {
        var publisher = BodyPublishers.noBody();
        var request = HttpRequest
                .newBuilder(requestWrapper.getUri())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .method(requestWrapper.getMethod().name(), publisher)
                .build();
        return this.execute(request);
    }

    private ResponseEntity<byte[]> execute(HttpRequest request) {
        LOGGER.info("HTTP Client: Sending HTTP {} request to {}", request.method(), request.uri());
        try {
            var response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            var status = HttpStatus.resolve(response.statusCode());
            LOGGER.info("HTTP Client: HTTP response status {}", status);
            return new ResponseEntity<>(response.body(), new LinkedMultiValueMap<>(response.headers().map()), status);
        } catch (HttpTimeoutException e) {
            LOGGER.error("HTTP Client: Request time out", e);
            throw ApiException.ofExceptions(HttpStatus.REQUEST_TIMEOUT, "HTTP Client: Request time out.");
        } catch (InterruptedException e) {
            // https://www.yegor256.com/2015/10/20/interrupted-exception.html
            Thread.currentThread().interrupt();
            throw new IllegalStateException(String.format("Thread interrupted while requesting for %s", request.uri()), e);
        } catch (IOException e) {
            LOGGER.error("HTTP Client: Unknown exception", e);
            throw ApiException.internalServerError("HTTP Client: " + e.getMessage());
        }
    }

    private <T> T extractBody(byte[] response, Class<T> clazz) {
        if (response == null) {
            return null;
        }
        try {
            if (clazz.equals(String.class)) {
                return (T) new String(response, StandardCharsets.UTF_8); //when empty body Json is returned, ObjectMapper fails
            } else if (clazz.equals(byte[].class)) {
                return (T) response; //Don't convert
            } else {
                return this.mapper.readValue(response, clazz);
            }
        } catch (IOException e) {
            LOGGER.error("HTTP Client: Invalid json response", e);
            throw ApiException.internalServerError("HTTP Client: " + e.getMessage());
        }
    }

    private void handleErrors(ResponseEntity<byte[]> response, URI uri) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        throw ApiException.internalServerError("HTTP Client: Unexpected exception");
    }

    private String convertToString(Object value) {
        try {
            return this.mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("HTTP Client: Cannot convert given body to a string representation", e);
            throw ApiException.internalServerError("HTTP Client: " + e.getMessage());
        }
    }
}
