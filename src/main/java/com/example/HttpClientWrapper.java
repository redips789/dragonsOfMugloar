package com.example;

import com.example.exception.ApiException;
import com.example.exception.ApiExceptionDetails;
import com.example.exception.ApiExceptionResponse;
import com.example.exception.SpringDefaultExceptionResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
     * Makes a HTTP GET request to the given URI. URI is formed without query params
     */
    public <T> T get(Class<T> responseClazz, String uri, Object... pathSegments) {
        return this.get(responseClazz, new LinkedMultiValueMap<>(), uri, pathSegments);
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
    public <T> T post(Object requestBody, Class<T> responseClazz, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.POST)
                .withUri(uri, pathSegments)
                .withRequestBody(requestBody)
                .build(responseClazz));
    }

    /**
     * Makes a HTTP POST request to the given URI
     */
    public <T> T post(Object requestBody, Class<T> responseClazz, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.POST)
                .withUri(uri, pathSegments)
                .withQueryParams(queryParams)
                .withRequestBody(requestBody)
                .build(responseClazz));
    }

    /**
     * Makes a HTTP PUT request to the given URI
     */
    public <T> T put(Object requestBody, Class<T> responseClazz, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.PUT)
                .withUri(uri, pathSegments)
                .withRequestBody(requestBody)
                .build(responseClazz));
    }

    public <T> T put(Object requestBody, Class<T> responseClazz, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.PUT)
                .withUri(uri, pathSegments)
                .withQueryParams(queryParams)
                .withRequestBody(requestBody)
                .build(responseClazz));
    }

    /**
     * Makes a HTTP PATCH request to the given URI
     */
    public <T> T patch(Object requestBody, Class<T> responseClazz, String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.PATCH)
                .withUri(uri, pathSegments)
                .withRequestBody(requestBody)
                .build(responseClazz));
    }

    /**
     * Makes a HTTP DELETE request to the given URI. Returns the returned body as a String
     */
    public String delete(String uri, Object... pathSegments) {
        return this.exchange(new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.DELETE)
                .withUri(uri, pathSegments)
                .build());
    }

    public <T> T exchange(HttpRequestWrapper<T> request) {
        var response = this.execute(request);
        this.handleErrors(response, request.getUri());
        return this.extractBody(response.getBody(), request.getResponseType());
    }

    private ResponseEntity<byte[]> execute(HttpRequestWrapper<?> requestWrapper) {
        var publisher = requestWrapper.getRequestBody()
                .map(this::convertToString)
                .map(BodyPublishers::ofString)
                .orElseGet(BodyPublishers::noBody);
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
            throw ApiException.ofExceptions(HttpStatus.REQUEST_TIMEOUT, "HTTP Client: Request time out."); // Should it really rethrow timeout?
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
        try {
            var exceptionResponse = this.mapper.readValue(response.getBody(), ApiExceptionResponse.class);
            if (exceptionResponse.getExceptions() == null
                    && exceptionResponse.getReason() == null
                    && exceptionResponse.getStatusCode() == 0
                    && exceptionResponse.getTimestamp() == null) {
                throw new IllegalStateException("Incorrect error model");
            }
            throw ApiException.ofExceptions(response.getStatusCode(), exceptionResponse.getReason(), exceptionResponse.getExceptions());
        } catch (IOException e) {
            LOGGER.warn("HTTP Client: Bad error response, will try to parse the error again", e);
        } catch (IllegalStateException e) {
            LOGGER.warn("Error not in standard expected form, try again");
        }
        // If it wasn't ApiExceptionResponse, let's try doing SpringDefaultExceptionResponse
        try {
            var springException = this.mapper.readValue(response.getBody(), SpringDefaultExceptionResponse.class);
            if (springException.getStatus() == null) {
                var responseBody = response.getBody() == null ? "" : new String(response.getBody(), StandardCharsets.UTF_8);
                throw new IllegalStateException("Incorrect error model: " + responseBody);
            }
            LOGGER.error("Error {} when calling {}, with a self reported exception: '{}' and message '{}'",
                    springException.getStatus(), uri, springException.getException().orElse(""), springException.getMessage().orElse(""));
            List<ApiExceptionDetails> details = new ArrayList<>();
            springException.getException().ifPresent(exceptionMessage -> details.add(new ApiExceptionDetails("", exceptionMessage, "")));
            throw ApiException.ofExceptions(response.getStatusCode(), springException.getError() + springException.getMessage().map(message -> ": " + message).orElse(""), details);
        } catch (IOException | IllegalStateException e) {
            LOGGER.error("HTTP Client: Bad error response", e);
            throw ApiException.internalServerError("HTTP Client: Bad response");
        }
    }

    private String convertToString(Object value) {
        try {
            return this.mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("HTTP Client: Cannot convert given body to a string representation", e);
            throw ApiException.internalServerError("HTTP Client: " + e.getMessage());
        }
    }

    public <T> T postMultipartFile(String filename, byte[] file, Class<T> responseType, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        return this.exchangeMultipartFile("POST", filename, file, responseType, queryParams, uri, pathSegments);
    }

    public <T> T patchMultipartFile(String filename, byte[] file, Class<T> responseType, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        return this.exchangeMultipartFile("PATCH", filename, file, responseType, queryParams, uri, pathSegments);
    }

    private <T> T exchangeMultipartFile(String method, String filename, byte[] file, Class<T> responseType, MultiValueMap<String, String> queryParams, String uri, Object... pathSegments) {
        var convertedPathSegments = Arrays.stream(pathSegments).map(Object::toString).toArray(String[]::new);
        var fullURI = UriComponentsBuilder.fromUriString(uri)
                .queryParams(queryParams)
                .pathSegment(convertedPathSegments)
                .encode()
                .build().toUri();

        var request = buildMultipartRequest(filename, file, fullURI, method);

        var response = this.execute(request);
        this.handleErrors(response, fullURI);
        return this.extractBody(response.getBody(), responseType);
    }

    /**
     * Carlist application accepts only one type of content - multipart/form-data. To better understand what's happening in the code, here is
     * raw Request that the code seeks to build:
     * <p>
     * POST /api/<version>/photos/cars/<id>
     * Content-Type: multipart/form-data
     * Accept: application/json
     * Content-Length: 26010                             //Calculated by Java
     * <p>
     * ----------------------------generatedRandomUUID
     * Content-Disposition: form-data; name="file"; filename="file"
     * <p>
     * <5OD-4.JPG>
     * ----------------------------generatedRandomUUID--
     * <p>
     * It's important to address few points:
     * - "generatedRandomUUID" refers to this line: ``String boundary = "-------------" + UUID.randomUUID().toString();``
     * - Between 'generatedRandomUUID' there is '<5OD-4.JPG>'. This was automatically generated when file is uploaded: ``byteArrays.add(bodyData);`` from
     * ``buildMultipartBody``.
     */
    private static HttpRequest buildMultipartRequest(String fileName, byte[] file, URI uri, String method) {
        var boundary = "-------------" + UUID.randomUUID().toString();
        var multipartBody = buildMultipartBody(fileName, boundary, file);

        return HttpRequest.newBuilder()
                .uri(uri)
                .method(method, HttpRequest.BodyPublishers.ofByteArrays(multipartBody))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .build();
    }

    private static List<byte[]> buildMultipartBody(String fileName, String boundary, byte[] bodyData) {
        return List.of(
                ("--" + boundary).getBytes(StandardCharsets.UTF_8),
                ("\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8),
                bodyData,
                "\r\n".getBytes(StandardCharsets.UTF_8),
                ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
    }
}
