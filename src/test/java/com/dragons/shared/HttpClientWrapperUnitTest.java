package com.dragons.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class HttpClientWrapperUnitTest {

    private static final String TESTING_URL = "http://localhost:666/working-uri-with-no-real-use";

    @InjectMocks
    private HttpClientWrapper wrapper;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private ObjectMapper mockMapper;

    private final URI testUri = URI.create(TESTING_URL);

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void getList__whenRequestingStringNotWithJson__shouldReturnCorrectString() throws IOException, InterruptedException {
        String[] expectedBody = {"Received body ąčęėįšųūž0 with unicode", "second string"};
        var responseFromHttpClient = "[\"" + expectedBody[0] + "\",\"" + expectedBody[1] + "\"]"; // This can be anything, as we have control over the mapper, but I want to keep it consistent

        var requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        var mockResponse = mockResponse(responseFromHttpClient, 200);
        when(this.mockHttpClient.send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofByteArray()))).thenReturn(mockResponse);
        when(this.mockMapper.readValue(responseFromHttpClient.getBytes(StandardCharsets.UTF_8), String[].class)).thenReturn(expectedBody);

        var response = this.wrapper.getList(String[].class, TESTING_URL);

        assertThat(requestCaptor.getValue().method()).isEqualTo("GET");
        assertThat(requestCaptor.getValue().uri()).isEqualTo(this.testUri);
        assertThat(requestCaptor.getValue().bodyPublisher().orElseThrow()).isInstanceOf(HttpRequest.BodyPublishers.noBody().getClass());
        assertThat(requestCaptor.getValue().headers().allValues("content-type")).containsOnly("application/json");

        assertThat(response).containsAll(List.of(expectedBody));
    }

    private static HttpResponse<byte[]> mockResponse(String expectedBody, int responseCode) {
        HttpResponse<byte[]> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(expectedBody.getBytes(StandardCharsets.UTF_8));
        when(mockResponse.statusCode()).thenReturn(responseCode);
        when(mockResponse.headers()).thenReturn(HttpHeaders.of(Map.of(), (x, y) -> true));
        return mockResponse;
    }
}
