package no.unit.nva.stubs;

import static java.util.Objects.isNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.ioutils.IoUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@JacocoGenerated
public class FakeS3Client implements S3Client {

    private static final int START_FROM_BEGINNING = 0;
    private final Map<String, InputStream> filesAndContent;

    public FakeS3Client(String... filesInBucket) {
        this(readResourceFiles(filesInBucket));
    }

    public FakeS3Client(Map<String, InputStream> filesAndContent) {
        this.filesAndContent = new ConcurrentHashMap<>(filesAndContent);
    }

    //TODO: fix if necessary
    @SuppressWarnings("PMD.CloseResource")
    @Override
    public <ReturnT> ReturnT getObject(GetObjectRequest getObjectRequest,
                                       ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer) {
        String filename = getObjectRequest.key();
        InputStream inputStream = extractInputStream(filename);
        byte[] contentBytes = readAllBytes(inputStream);
        GetObjectResponse response = GetObjectResponse.builder().contentLength((long) contentBytes.length).build();
        return transformResponse(responseTransformer, new ByteArrayInputStream(contentBytes), response);
    }

    /**
     * Lists objects paginated one by one.
     *
     * @param listObjectsRequest the request
     * @return Response containing only one object.
     */
    @Override
    public ListObjectsResponse listObjects(ListObjectsRequest listObjectsRequest) {
        List<String> fileKeys = filesAndContent.keySet().stream().sorted().collect(Collectors.toList());
        var startIndex = calculateStartIndex(fileKeys, listObjectsRequest.marker());
        var endIndex = calculateEndIndex(fileKeys, listObjectsRequest.marker(), listObjectsRequest.maxKeys());
        var truncated = endIndex < fileKeys.size();
        List<S3Object> files = fileKeys.subList(startIndex, endIndex).stream()
            .map(filename -> S3Object.builder().key(filename).build())
            .collect(Collectors.toList());

        return ListObjectsResponse.builder().contents(files).isTruncated(truncated).build();
    }

    //TODO: fix if necessary
    @SuppressWarnings("PMD.CloseResource")
    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
        String path = putObjectRequest.key();
        InputStream inputStream = requestBody.contentStreamProvider().newStream();
        this.filesAndContent.put(path, inputStream);
        return PutObjectResponse.builder().build();
    }

    @Override
    public String serviceName() {
        return "FakeS3Client";
    }

    @Override
    public void close() {

    }

    private static Map<String, InputStream> readResourceFiles(String[] filesInBucket) {
        List<String> suppliedFilenames = Arrays.asList(filesInBucket);
        return suppliedFilenames.stream()
            .map(filename -> new SimpleEntry<>(filename, IoUtils.inputStreamFromResources(filename)))
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private int calculateEndIndex(List<String> fileKeys, String marker, Integer pageSize) {
        int startIndex = calculateStartIndex(fileKeys, marker);
        return Math.min(startIndex + pageSize, fileKeys.size());
    }

    private int calculateStartIndex(List<String> fileKeys, String marker) {
        if (isNull(marker)) {
            return START_FROM_BEGINNING;
        } else {
            var calculatedStartIndex = fileKeys.indexOf(marker) + 1;
            if (calculatedStartIndex < fileKeys.size()) {
                return calculatedStartIndex;
            }
        }
        throw new IllegalStateException("Start index is out of bounds in FakeS3Client");
    }

    private byte[] readAllBytes(InputStream inputStream) {
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream extractInputStream(String filename) {
        if (filesAndContent.containsKey(filename)) {
            return filesAndContent.get(filename);
        } else {
            throw NoSuchKeyException.builder().message("File does not exist:" + filename).build();
        }
    }

    private <ReturnT> ReturnT transformResponse(ResponseTransformer<GetObjectResponse, ReturnT> responseTransformer,
                                                InputStream inputStream, GetObjectResponse response) {
        try {
            return responseTransformer.transform(response, AbortableInputStream.create(inputStream));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}