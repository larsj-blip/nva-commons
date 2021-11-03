package no.unit.nva.events.handlers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.IntrospectionException;
import java.util.List;

public abstract class AbstractEventHandlerTest {

    public static final boolean CONTAINS_EMPTY_FIELDS = true;
    public static final boolean DOES_NOT_CONTAIN_EMPTY_FIELDS = !CONTAINS_EMPTY_FIELDS;

    protected void assertThatJsonObjectContainsEmptyFields(ObjectNode objectNode) throws IntrospectionException {
        List<String> properties = SampleEventDetail.propertyNamesOfEmptyFields();
        properties.forEach(property -> assertThat(property, objectNode.has(property), is(CONTAINS_EMPTY_FIELDS)));
    }

    protected void assertThatJsonNodeDoesNotContainEmptyFields(ObjectNode objectNode) throws IntrospectionException {
        List<String> properties = SampleEventDetail.propertyNamesOfEmptyFields();
        properties.forEach(
            property -> assertThat(property, objectNode.has(property), is(DOES_NOT_CONTAIN_EMPTY_FIELDS)));
    }
}