package com.dragons.shared;

import com.dragons.exception.ApiException;
import com.dragons.exception.ApiExceptionDetails;
import com.dragons.exception.ApiExceptionResponse;
import com.dragons.exception.SpringDefaultExceptionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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

    @Test
    void exchange__ifNot200AndResponseContainsExpectedErrorForm__shouldThrowErrorWithGivenCode() throws IOException, InterruptedException {
        var rawErrorBody = "Raw error which will be deserialized into a valid ApiExceptionResponse";
        var expectedReason = "My custom reason";
        var expectedStatusCode = 404;
        var unexpectedStatusCode = 500;

        var requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        var mockResponse = mockResponse(rawErrorBody, expectedStatusCode);
        ApiExceptionDetails[] exceptionDetails = {new ApiExceptionDetails("ERROR", "messAGe", "field name")};
        when(this.mockHttpClient.send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofByteArray()))).thenReturn(mockResponse);
        when(this.mockMapper.readValue(rawErrorBody.getBytes(StandardCharsets.UTF_8), ApiExceptionResponse.class))
                .thenReturn(ApiExceptionResponse.ofApiException(ApiException.ofExceptions(HttpStatus.resolve(unexpectedStatusCode), expectedReason, exceptionDetails)));


        var request = new HttpRequestWrapper.HttpRequestBuilder()
                .withMethod(HttpMethod.PUT)
                .withUri(TESTING_URL)
                .build();
        ApiException exception = null;
        try {
            this.wrapper.exchange(request);
        } catch (ApiException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        var convertedException = ApiExceptionResponse.ofApiException(exception); // convert exception to a usable form
        assertThat(convertedException.getReason()).isEqualTo(expectedReason);
        assertThat(convertedException.getStatusCode()).isEqualTo(expectedStatusCode);
        assertThat(convertedException.getExceptions()).containsOnly(exceptionDetails);

        assertThat(requestCaptor.getValue().method()).isEqualTo("PUT");
        assertThat(requestCaptor.getValue().uri()).isEqualTo(this.testUri);
        assertThat(requestCaptor.getValue().headers().allValues("content-type")).containsOnly("application/json");
    }

    @Test
    void get__whenSpringReturnsError__shouldReturnCorrectString() throws IOException, InterruptedException {
        // This body will not actually be serialized, it's for easier understanding
        var exceptionBody =
                "{\n" +
                        "  \"timestamp\": \"2020-07-17T06:38:21.914+00:00\",\n" +
                        "  \"status\": 203,\n" +  // To test if status from message is propagated. Should not
                        "  \"exception\": \"Not Found\",\n" +
                        "  \"message\": \"Message\",\n" +
                        "  \"path\": \"/api/v1.31/assetsasasd\"\n" +
                        "}";

        var requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        var mockResponse = mockResponse(exceptionBody, 404);
        when(this.mockHttpClient.send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofByteArray()))).thenReturn(mockResponse);

        when(this.mockMapper.readValue(exceptionBody.getBytes(StandardCharsets.UTF_8), ApiExceptionResponse.class)).thenThrow(mock(JsonProcessingException.class));
        when(this.mockMapper.readValue(exceptionBody.getBytes(StandardCharsets.UTF_8), SpringDefaultExceptionResponse.class)).thenReturn(new SpringDefaultExceptionResponse(new Date(), 203, "Error", "Exception", "Message", "/api/v1.31/assetsasasd"));

        ApiException exception = null;
        try {
            this.wrapper.getList(String[].class, TESTING_URL);
        } catch (ApiException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        var convertedException = ApiExceptionResponse.ofApiException(exception); // convert exception to a usable form
        assertThat(convertedException.getReason()).isEqualTo("Error: Message");
        assertThat(convertedException.getStatusCode()).isEqualTo(404);
        assertThat(convertedException.getExceptions()).hasSize(1);
        assertThat(convertedException.getExceptions().get(0).getCode()).isEmpty();
        assertThat(convertedException.getExceptions().get(0).getFieldName()).isEmpty();
        assertThat(convertedException.getExceptions().get(0).getMessage()).isEqualTo("Exception");
    }

    private static HttpResponse<byte[]> mockResponse(String expectedBody, int responseCode) {
        HttpResponse<byte[]> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(expectedBody.getBytes(StandardCharsets.UTF_8));
        when(mockResponse.statusCode()).thenReturn(responseCode);
        when(mockResponse.headers()).thenReturn(HttpHeaders.of(Map.of(), (x, y) -> true));
        return mockResponse;
    }
}
