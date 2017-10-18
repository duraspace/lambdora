package org.fcrepo.lambdora;

/**
 * @author dbernstein
 */
public class Response {

    private final String message;

    /**
     * constructor
     * @param message
     */
    public Response(final String message) {
        this.message = message;
    }

    /**
     * get message member
     * @return message
     */
    public String getMessage() {
        return this.message;
    }
}
