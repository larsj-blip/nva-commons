package nva.commons.exceptions;

import java.util.Objects;

public abstract class ApiGatewayException extends Exception {

    public static final String MISSING_STATUS_CODE = "Status code cannot be null for exception:";

    public ApiGatewayException(String message) {
        super(message);
    }

    public ApiGatewayException(Exception exception) {
        super(exception);
    }

    protected abstract Integer statusCode();

    /**
     * Get the status code that should be returned to the REST-client.
     *
     * @return the status code.
     */
    public int getStatusCode() {
        if (Objects.isNull(statusCode())) {
            throw new IllegalStateException(MISSING_STATUS_CODE + this.getClass().getCanonicalName());
        }
        return statusCode();
    }
}




