package nva.commons.handlers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import nva.commons.RequestBody;
import nva.commons.exceptions.ApiIoException;
import nva.commons.hanlders.ApiMessageParser;
import nva.commons.utils.IoUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApiMessageParserTest {

    public static final String API_GATEWAY_MESSAGES_FOLDER = "apiGatewayMessages";
    public static final Path MISSING_REQUEST_INFO = Path.of(API_GATEWAY_MESSAGES_FOLDER, "missingRequestInfo.json");
    public static final String SOME_EXCEPTION_MESSAGE = "Some error message";
    private static final Path MISSING_BODY = Path.of(API_GATEWAY_MESSAGES_FOLDER, "missingBody.json");
    private static final Path BODY_NON_JSON_STRING = Path.of(API_GATEWAY_MESSAGES_FOLDER, "bodyIsNonJsonString.json");
    public static final String NON_JSON_STRING_BODY = "Hello world";
    private static final Path BODY_JSON_STRING = Path.of(API_GATEWAY_MESSAGES_FOLDER, "bodyIsAJsonString.json");
    private static final Path BODY_JSON_ELEMENT = Path.of(API_GATEWAY_MESSAGES_FOLDER, "bodyIsAJsonElement.json");

    private <T> ApiMessageParser<T> messageParser(ObjectMapper mapper) {
        return new ApiMessageParser<>(mapper);
    }

    @DisplayName("getRequestInfo throws ApiIoException when JSON string does not contain the necessary request "
        + "information")
    @Test
    public void getRequestInfoThrowsApiIoExceptionWhenJsonStringDoesNotContainTheNecessaryRequestInformation()
        throws JsonProcessingException {
        String json = IoUtils.stringFromResources(MISSING_REQUEST_INFO);
        ApiMessageParser<String> parser = parserWithMapperThatThrowsIoException();
        ApiIoException exception = assertThrows(ApiIoException.class, () -> parser.getRequestInfo(json));
        assertThat(exception.getMessage(), containsString(ApiMessageParser.COULD_NOT_PARSE_REQUEST_INFO));
    }

    @DisplayName("getBodyElementFromJson returns null for empty body")
    @Test
    public void getBodyElementFromJsonReturnsEmptyStringForEmptyBodyAndStringParser() throws IOException {

        String json = IoUtils.stringFromResources(MISSING_BODY);
        ApiMessageParser<String> parser = new ApiMessageParser<>();
        String body = parser.getBodyElementFromJson(json, String.class);
        assertThat(body, is(nullValue()));
    }

    @DisplayName("getBodyElementFromJson returns a String for a non JSON string valued body")
    @Test
    public void getBodyElementFromJsonReturnsAStringForAStringValuedBody() throws IOException {
        String json = IoUtils.stringFromResources(BODY_NON_JSON_STRING);
        ApiMessageParser<String> parser = new ApiMessageParser<>();
        String body = parser.getBodyElementFromJson(json, String.class);
        assertThat(body, is(equalTo(NON_JSON_STRING_BODY)));
    }

    @DisplayName("getBodyElementFromJson returns a requestObject for a JSON string body")
    @Test
    public void getBodyElementFromJsonReturnsAStringForAJsonStringBody() throws IOException {
        String json = IoUtils.stringFromResources(BODY_JSON_STRING);
        ApiMessageParser<RequestBody> parser = new ApiMessageParser<>();
        RequestBody body = parser.getBodyElementFromJson(json, RequestBody.class);
        RequestBody expected = new RequestBody("value1", "value2");
        assertThat(body, is(equalTo(expected)));
    }

    @DisplayName("getBodyElementFromJson returns a requestObject for a JSON element")
    @Test
    public void getBodyElementFromJsonReturnsAStringForAJsonElement() throws IOException {
        String json = IoUtils.stringFromResources(BODY_JSON_ELEMENT);
        ApiMessageParser<RequestBody> parser = new ApiMessageParser<>();
        RequestBody body = parser.getBodyElementFromJson(json, RequestBody.class);
        RequestBody expected = new RequestBody("value1", "value2");
        assertThat(body, is(equalTo(expected)));
    }

    private ApiMessageParser<String> parserWithMapperThatThrowsIoException()
        throws JsonProcessingException {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(anyString(), any(Class.class))).thenThrow(
            new JsonMappingException(null, SOME_EXCEPTION_MESSAGE));
        return messageParser(mapper);
    }
}