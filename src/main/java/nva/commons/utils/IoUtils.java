package nva.commons.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class IoUtils {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    private IoUtils() {
    }

    /**
     * Read resource file as an {@link InputStream}.
     * The root folder for the resources is considered to be the folders src/main/resources and src/test/resources/,
     * or any other standard reosources folder.
     *
     * @param path the Path to the resource.
     * @return an InputStream with the data.
     */
    public static InputStream inputStreamFromResources(Path path) {
        String pathString = path.toString();
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(pathString);
    }

    /**
     *  Return a String the stream data encoded in UTF-8.
     * @param stream the {@link InputStream}
     * @return the output String.
     */
    public static String streamToString(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(newInputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining(LINE_SEPARATOR));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static InputStreamReader newInputStreamReader(InputStream stream) {
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    /**
     * Read file as a {@link String}. Encoding is UTF-8.
     * New lines are being preserved.
     * @param path {@link Path} to the file
     * @return the content of the file in a String.
     */
    public static String stringFromFile(Path path) {
        try (InputStream fileInputStream = Files.newInputStream(path)) {
            return streamToString(fileInputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Read resource file as a List of Strings.
     * The root folder for the resources is considered to be the folders src/main/resources and src/test/resources/,
     * or any other standard resources folder.
     * Each element of the list contains a line of the file.
     *
     * @param path the Path to the resource.
     * @return a List with the lines of the file.
     */
    public static List<String> linesfromResource(Path path) {
        try (BufferedReader reader = new BufferedReader(newInputStreamReader(inputStreamFromResources(path)))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Read resource file as a {@link String}
     * The root folder for the resources is considered to be the folders src/main/resources and src/test/resources/,
     * or any other standard resources folder.
     * New lines in the resource file are preserved
     *
     * @param path the Path to the resource.
     * @return a {@link String} with the contents of the file.
     */
    public static String stringFromResources(Path path) {
        return streamToString(inputStreamFromResources(path));
    }

    /**
     * Tranform a string to an inputStream. Encoding is UTF-8.
     * @param input a {@link String}
     * @return a {@link InputStream}
     */
    public static InputStream stringToStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }
}