package com.example;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestWrapper<R> {

    private final URI uri;
    private final HttpMethod method;
    private final Object requestBody;
    private final Class<R> responseType;

    public HttpRequestWrapper(URI uri, HttpMethod method, Object requestBody, Class<R> responseType) {
        this.uri = uri;
        this.method = method;
        this.requestBody = requestBody;
        this.responseType = responseType;
    }

    public URI getUri() {
        return this.uri;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Optional<Object> getRequestBody() {
        return Optional.ofNullable(this.requestBody);
    }

    public Class<R> getResponseType() {
        return this.responseType;
    }

    public static class HttpRequestBuilder {
        private String uri;
        private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        private HttpMethod method;
        private Object requestBody;

        public HttpRequestBuilder withUri(String uri, Object... pathSegments) {
            var convertedPathSegments = Arrays.stream(pathSegments).map(Object::toString).toArray(String[]::new);
            this.uri = UriComponentsBuilder.fromUriString(uri)
                    .pathSegment(convertedPathSegments)
                    .build().toString();
            return this;
        }

        public HttpRequestBuilder withQueryParams(MultiValueMap<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public HttpRequestBuilder withMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder withRequestBody(Object requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public HttpRequestWrapper<String> build() {
            return this.build(String.class);
        }

        public <T> HttpRequestWrapper<T> build(Class<T> responseType) {
            Objects.requireNonNull(this.method);
            Objects.requireNonNull(this.uri);
            var newUri = UriComponentsBuilder.fromUriString(this.uri).queryParams(this.queryParams).encode().build().toUri();
            return new HttpRequestWrapper<>(newUri, this.method, this.requestBody, responseType);
        }
    }
}