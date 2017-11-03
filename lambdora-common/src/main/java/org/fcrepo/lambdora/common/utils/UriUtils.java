package org.fcrepo.lambdora.common.utils;

import java.net.URI;

/**
 * UriUtils
 *
 * @author dbernstein
 */
public class UriUtils {

    /**
     * Prefix for internal repository resources
     */
    public static final String INTERNAL_URI_PREFIX = "fedora://info";

    private UriUtils() {
    }

    /**
     * Returns the parent URI or null if the specified uri is the root uri
     *
     * @param uri
     * @return the parent URI or null
     */
    public static URI getParent(final URI uri) {
        if (isRoot(uri)) {
            return null;
        } else {
            final URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
            final String parentStr = parent.toString();
            return isRoot(parent) ? parent : URI.create(parentStr.substring(0, parentStr.length() - 1));
        }
    }

    private static boolean isRoot(final URI uri) {
        return uri.getPath().equals("/");
    }
}
