package com.dragons.shared;

import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

public class HttpRequestWrapper<R> {

    private final URI uri;
    private final HttpMethod method;
    private final Class<R> responseType;

    public HttpRequestWrapper(URI uri, HttpMethod method, Class<R> responseType) {
        this.uri = uri;
        this.method = method;
        this.responseType = responseType;
    }

    public URI getUri() {
        return this.uri;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Class<R> getResponseType() {
        return this.responseType;
    }

    public static class HttpRequestBuilder {
        private String uri;
        private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        private HttpMethod method;

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

        public <T> HttpRequestWrapper<T> build(Class<T> responseType) {
            Objects.requireNonNull(this.method);
            Objects.requireNonNull(this.uri);
            var newUri = UriComponentsBuilder.fromUriString(this.uri).queryParams(this.queryParams).encode().build().toUri();
            return new HttpRequestWrapper<>(newUri, this.method, responseType);
        }
    }
}