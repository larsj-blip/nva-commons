package nva.commons.handlers;

import static nva.commons.handlers.RequestInfo.APPLICATION_ROLES;
import static nva.commons.handlers.RequestInfo.CUSTOMER_ID;
import static nva.commons.handlers.RequestInfo.FEIDE_ID;
import static nva.commons.handlers.RequestInfo.REQUEST_CONTEXT_FIELD;
import static nva.commons.utils.JsonUtils.objectMapper;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import nva.commons.exceptions.ApiIoException;
import nva.commons.utils.IoUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequestInfoTest {

    public static final String AUTHORIZER = "authorizer";
    public static final String CLAIMS = "claims";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String JSON_POINTER = "/authorizer/claims/key";
    public static final Path EVENT_WITH_UNKNOWN_REQUEST_INFO = Path.of("apiGatewayMessages",
        "eventWithUnknownRequestInfo.json");
    public static final String UNDEFINED_REQUEST_INFO_PROPERTY = "body";
    public static final String PATH_DELIMITER = "/";
    public static final int UNNECESSARY_ROOT_NODE = 0;
    public static final int FIRST_NODE = 0;
    public static final String accessRightSample2 = "REJECT_DOI_REQUEST";
    public static final String ACCESS_RIGHT_SAMPLE_2 = accessRightSample2;
    public static final String ACCESS_RIGHT_SAMPLE_1 = "APPROVE_DOI_REQUEST";
    private static final String API_GATEWAY_MESSAGES_FOLDER = "apiGatewayMessages";
    public static final Path EVENT_WITH_ACCESS_RIGHTS = Path.of(API_GATEWAY_MESSAGES_FOLDER,
        "event_with_access_rights_claim.json");
    public static final Path EVENT_WITHOUT_ACCESS_RIGHTS = Path.of(API_GATEWAY_MESSAGES_FOLDER,
        "event_without_access_rights_claim.json");
    private static final Path NULL_VALUES_FOR_MAPS = Path.of(API_GATEWAY_MESSAGES_FOLDER,
        "mapParametersAreNull.json");
    private static final Path MISSING_MAP_VALUES = Path.of(API_GATEWAY_MESSAGES_FOLDER,
        "missingRequestInfo.json");

    @Test
    @DisplayName("RequestInfo can accept unknown fields")
    public void requestInfoAcceptsUnknownsFields() throws JsonProcessingException {
        String requestInfoString = IoUtils.stringFromResources(EVENT_WITH_UNKNOWN_REQUEST_INFO);
        RequestInfo requestInfo = objectMapper.readValue(requestInfoString, RequestInfo.class);

        assertThat(requestInfo.getOtherProperties(), hasKey(UNDEFINED_REQUEST_INFO_PROPERTY));
    }

    @Test
    @DisplayName("RequestInfo initializes queryParameters to empty map when JSON object sets "
        + "queryStringParameters to null")
    public void requestInfoInitializesQueryParametesToEmptyMapWhenJsonObjectsSetsQueryStringParametersToNull()
        throws JsonProcessingException {
        checkForNonNullMap(NULL_VALUES_FOR_MAPS, RequestInfo::getQueryParameters);
    }

    @Test
    @DisplayName("RequestInfo initializes headers to empty map when JSON object sets "
        + "Headers to null")
    public void requestInfoInitializesHeadersToEmptyMapWhenJsonObjectsSetsQueryStringParametersToNull()
        throws JsonProcessingException {
        checkForNonNullMap(NULL_VALUES_FOR_MAPS, RequestInfo::getHeaders);
    }

    @Test
    @DisplayName("RequestInfo initializes pathParameters to empty map when JSON object sets "
        + "pathParameters to null")
    public void requestInfoInitializesPathParametersToEmptyMapWhenJsonObjectsSetsQueryStringParametersToNull()
        throws JsonProcessingException {
        checkForNonNullMap(NULL_VALUES_FOR_MAPS, RequestInfo::getPathParameters);
    }

    @Test
    @DisplayName("RequestInfo initializes requestContext to empty JsonNode when JSON object sets "
        + "requestContext to null")
    public void requestInfoInitializesRequestContextToEmptyJsonNodeWhenJsonObjectsSetsRequestContextToNull()
        throws JsonProcessingException {
        checkForNonNullMap(NULL_VALUES_FOR_MAPS, RequestInfo::getRequestContext);
    }

    @Test
    @DisplayName("RequestInfo initializes queryParameters to empty map queryStringParameters is missing")
    public void requestInfoInitializesQueryParametesToEmptyMapWhenQueryStringParametersIsMissing()
        throws JsonProcessingException {
        checkForNonNullMap(MISSING_MAP_VALUES, RequestInfo::getQueryParameters);
    }

    @Test
    @DisplayName("RequestInfo initializes headers to empty map when header parameter is missing")
    public void requestInfoInitializesHeadersToEmptyMapWhenHeadersParameterIsMissing()
        throws JsonProcessingException {
        checkForNonNullMap(MISSING_MAP_VALUES, RequestInfo::getHeaders);
    }

    @Test
    @DisplayName("RequestInfo initializes headers to empty map when header parameter is missing")
    public void requestInfoInitializesHeadersToEmptyMapWhenPathPrametersParameterIsMissing()
        throws JsonProcessingException {
        checkForNonNullMap(MISSING_MAP_VALUES, RequestInfo::getPathParameters);
    }

    @Test
    @DisplayName("RequestInfo initializes requestContext to empty JsonNode requestContext is missing")
    public void requestInfoInitializesRequestContextToEmptyJsonNodeWhenRequestContextIsMissing()
        throws JsonProcessingException {
        checkForNonNullMap(MISSING_MAP_VALUES, RequestInfo::getRequestContext);
    }

    @Test
    public void requestInfoReturnsUsernameForRequestContextWithCredentials() {
        RequestInfo requestInfo = new RequestInfo();

        String expectedUsername = "orestis";
        requestInfo.setRequestContext(createNestedNodesFromJsonPointer(FEIDE_ID, expectedUsername));

        String actual = requestInfo.getFeideId().orElseThrow();
        assertEquals(actual, expectedUsername);
    }

    @Test
    public void requestInfoReturnsCustomerIdForRequestContextWithCredentials() {
        RequestInfo requestInfo = new RequestInfo();

        String expectedCustomerId = "customerId";
        requestInfo.setRequestContext(createNestedNodesFromJsonPointer(CUSTOMER_ID, expectedCustomerId));

        String actual = requestInfo.getCustomerId().orElseThrow();
        assertEquals(actual, expectedCustomerId);
    }

    @Test
    public void requestInfoReturnsAssignedRolesForRequestContextWithCredentials() {
        RequestInfo requestInfo = new RequestInfo();

        String expectedRoles = "role1,role2";
        requestInfo.setRequestContext(createNestedNodesFromJsonPointer(APPLICATION_ROLES, expectedRoles));

        String actual = requestInfo.getAssignedRoles().orElseThrow();
        assertEquals(actual, expectedRoles);
    }

    @Test
    @DisplayName("getAccessRights returns as list the values in the claim custom:accessRights")
    public void getAccessRightsReturnsAsListTheValuesInClaimCustomAccessRights() throws ApiIoException {
        String event = IoUtils.stringFromResources(EVENT_WITH_ACCESS_RIGHTS);
        ApiMessageParser<String> apiMessageParser = new ApiMessageParser<String>();
        RequestInfo requestInfo = apiMessageParser.getRequestInfo(event);
        Set<String> actualAccessRights = requestInfo.getAccessRights();
        Set<String> expectedAccessRights = Set.of(ACCESS_RIGHT_SAMPLE_1, ACCESS_RIGHT_SAMPLE_2);
        assertThat(actualAccessRights, is(equalTo(expectedAccessRights)));
    }

    @Test
    public void getAccessRightsReturnsEmptySetOfAccessRightsWhenAccessRightsClaimIsMissing() throws ApiIoException {
        String event = IoUtils.stringFromResources(EVENT_WITHOUT_ACCESS_RIGHTS);
        ApiMessageParser<String> apiMessageParser = new ApiMessageParser<String>();
        RequestInfo requestInfo = apiMessageParser.getRequestInfo(event);
        Set<String> actualAccessRights = requestInfo.getAccessRights();
        Set<String> expectedAccessRights = Collections.emptySet();
        assertThat(actualAccessRights, is(equalTo(expectedAccessRights)));
    }

    @Test
    public void canGetValueFromRequestContext() throws JsonProcessingException {

        Map<String, Map<String, Map<String, Map<String, String>>>> map = Map.of(
            REQUEST_CONTEXT_FIELD, Map.of(
                AUTHORIZER, Map.of(
                    CLAIMS, Map.of(
                        KEY, VALUE
                    )
                )
            )
        );

        RequestInfo requestInfo = objectMapper.readValue(objectMapper.writeValueAsString(map), RequestInfo.class);

        JsonPointer jsonPointer = JsonPointer.compile(JSON_POINTER);
        JsonNode jsonNode = requestInfo.getRequestContext().at(jsonPointer);

        assertFalse(jsonNode.isMissingNode());
        assertEquals(VALUE, jsonNode.textValue());
    }

    private ObjectNode createNestedNodesFromJsonPointer(JsonPointer jsonPointer, String value) {
        List<SimpleEntry<String, ObjectNode>> nodeList = createNodesForEachPathElement(jsonPointer);
        nestNodes(nodeList);
        SimpleEntry<String, ObjectNode> lastEntry = nodeList.get(lastIndex(nodeList));
        insertTextValueToLeafNode(value, lastEntry);

        return nodeList.get(FIRST_NODE).getValue();
    }

    private List<SimpleEntry<String, ObjectNode>> createNodesForEachPathElement(JsonPointer jsonPointer) {
        List<SimpleEntry<String, ObjectNode>> nodes = createListWithEmptyObjectNodes(jsonPointer);
        nodes.remove(UNNECESSARY_ROOT_NODE);
        return nodes;
    }

    private void nestNodes(List<SimpleEntry<String, ObjectNode>> nodes) {
        for (int i = 0; i < lastIndex(nodes); i++) {
            SimpleEntry<String, ObjectNode> currentEntry = nodes.get(i);
            SimpleEntry<String, ObjectNode> nextEntry = nodes.get(i + 1);
            addNextEntryAsChildToCurrentEntry(currentEntry, nextEntry);
        }
    }

    private void insertTextValueToLeafNode(String value, SimpleEntry<String, ObjectNode> lastEntry) {
        lastEntry.getValue().put(lastEntry.getKey(), value);
    }

    private void addNextEntryAsChildToCurrentEntry(SimpleEntry<String, ObjectNode> currentEntry,
                                                   SimpleEntry<String, ObjectNode> nextEntry) {
        ObjectNode currentNode = currentEntry.getValue();
        currentNode.set(currentEntry.getKey(), nextEntry.getValue());
    }

    private List<SimpleEntry<String, ObjectNode>> createListWithEmptyObjectNodes(JsonPointer jsonPointer) {
        return Arrays.stream(jsonPointer.toString()
            .split(PATH_DELIMITER))
            .map(nodeName -> new SimpleEntry<>(nodeName, objectMapper.createObjectNode()))
            .collect(Collectors.toList());
    }

    private int lastIndex(List<SimpleEntry<String, ObjectNode>> nodes) {
        return nodes.size() - 1;
    }

    private void checkForNonNullMap(Path resourceFile, Function<RequestInfo, Object> getObject)
        throws JsonProcessingException {
        String apiGatewayEvent = IoUtils.stringFromResources(resourceFile);
        RequestInfo requestInfo = objectMapper.readValue(apiGatewayEvent, RequestInfo.class);
        assertNotNull(getObject.apply(requestInfo));
    }
}

