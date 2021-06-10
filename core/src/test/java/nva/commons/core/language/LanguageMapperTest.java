package nva.commons.core.language;

import static nva.commons.core.language.LanguageMapper.ERROR_MESSAGE_MISSING_RESOURCE_EXCEPTION;
import static nva.commons.core.language.LanguageMapper.LEXVO_URI_PREFIX;
import static nva.commons.core.language.LanguageMapper.LEXVO_URI_UNDEFINED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import nva.commons.core.ioutils.IoUtils;
import nva.commons.logutils.LogUtils;
import nva.commons.logutils.TestAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class LanguageMapperTest {

    public static final String ISO_639_3_LANGUAGE_CODE = "eng";
    public static final String ISO_639_1_LANGUAGE_CODE = "en";
    public static final String ISO_639_3_LANGUAGE_CODE_UPPERCASE = "ENG";
    public static final String ISO_639_1_LANGUAGE_CODE_UPPERCASE = "EN";
    public static final String NON_EXISTENT_CODE = "afadfad";
    public static final int ISO_CODE_INDEX = 0;
    public static final int LEXVO_URI_INDEX = 1;

    @Test
    public void toUriReturnsUriWhenInputIsIso3() {
        URI expectedOutput = URI.create(LEXVO_URI_PREFIX + ISO_639_3_LANGUAGE_CODE);
        URI actualOutput = LanguageMapper.toUri(ISO_639_3_LANGUAGE_CODE);
        assertThat(actualOutput, is(equalTo(expectedOutput)));
    }

    @Test
    public void toUriReturnsUriWhenInputIsIso1() {
        URI expectedOutput = URI.create(LEXVO_URI_PREFIX + ISO_639_3_LANGUAGE_CODE);
        URI actualOutput = LanguageMapper.toUri(ISO_639_1_LANGUAGE_CODE);
        assertThat(actualOutput, is(equalTo(expectedOutput)));
    }

    @Test
    public void toUriReturnsUndefinedWhenInputLanguageCodeIsNotFound() {
        URI actualOutput = LanguageMapper.toUri(NON_EXISTENT_CODE);
        assertThat(actualOutput, is(equalTo(LEXVO_URI_UNDEFINED)));
    }

    @ParameterizedTest(name = "LanguageMapper.toUri returns URI when input is code: {0}")
    @ValueSource(strings = {ISO_639_3_LANGUAGE_CODE_UPPERCASE, ISO_639_1_LANGUAGE_CODE_UPPERCASE})
    public void toUriReturnsUriWhenInputIsUpperCase(String upperCaseLangCode) {
        URI expectedOutput = URI.create(LEXVO_URI_PREFIX + ISO_639_3_LANGUAGE_CODE);
        URI actualOutput = LanguageMapper.toUri(upperCaseLangCode);
        assertThat(actualOutput, is(equalTo(expectedOutput)));
    }

    @ParameterizedTest(name = "LanguageMapper.toUri returns URI for undefined language when input is code: \"{0}\"")
    @NullAndEmptySource
    public void toUriReturnUndefinedWhenInputIsEmpty(String emptyLanguageCode) {
        URI actualOutput = LanguageMapper.toUri(emptyLanguageCode);
        assertThat(actualOutput, is(equalTo(LEXVO_URI_UNDEFINED)));
    }

    @Test
    public void toUriWritesFailureMessageInLogWhenFailing() {
        TestAppender appender = LogUtils.getTestingAppender(LanguageMapper.class);
        LanguageMapper.toUri(NON_EXISTENT_CODE);
        String expectedValue = ERROR_MESSAGE_MISSING_RESOURCE_EXCEPTION + NON_EXISTENT_CODE;
        assertThat(appender.getMessages(), containsString(expectedValue));
    }

    @Test
    public void toUriReturnsUriAsSpecifiedInLexvoResourceFilesForAllIsoCodes() {
        final Map<String, URI> lexvoMappings = new ConcurrentHashMap<>();
        IoUtils.linesfromResource(Path.of("lexvo", "lexvoMappings.txt"))
            .stream()
            .map(line -> line.split("\\s+"))
            .forEach(array -> insertTomMap(array, lexvoMappings));

        Set<String> isoCodes = lexvoMappings.keySet();
        for (String isoCode : isoCodes) {
            URI expectedUri = lexvoMappings.get(isoCode);
            URI actualUri = LanguageMapper.toUri(isoCode);
            assertThat(actualUri, is(equalTo(expectedUri)));
        }
    }

    private void insertTomMap(String[] array, Map<String, URI> lexvoMappings) {
        String isoCode = array[ISO_CODE_INDEX];
        URI lexvoUri = URI.create(array[LEXVO_URI_INDEX]);
        if (lexvoMappings.containsKey(isoCode) && !lexvoMappings.get(isoCode).equals(lexvoUri)) {
            throw new RuntimeException("ISO code mismatch:" + isoCode);
        } else {
            lexvoMappings.put(isoCode, lexvoUri);
        }
    }
}
