package nva.commons.apigateway;

import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.jupiter.api.Test;

class AccessRightEntryTest {

    @Test
    public void toStringReturnsTheSameStringForEquivalentObjects() {
        var accessRight = randomString();
        var customerId = randomUri();
        var group1 = new AccessRightEntry(accessRight, customerId);
        var group2 = new AccessRightEntry(accessRight, customerId);
        assertThat(group1.toString(), is(equalTo(group2.toString())));
    }
}