package org.fcrepo.lambdora.common.utils;

import java.net.URI;

import static org.fcrepo.lambdora.common.rdf.RdfLexicon.INTERNAL_URI_PREFIX;

/**
 * UriUtils
 *
 * @author dbernstein
 */
public class UriUtils {

    private UriUtils() {
    }

    private static final URI ROOT = URI.create(INTERNAL_URI_PREFIX + "/");

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
            //get the parent path (with a slash on the end)
            final URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
            final String parentStr = parent.toString();
            //if the resolved parent is root, return it is as, otherwise remove the slash and
            //return it.
            return isRoot(parent) ? parent : URI.create(parentStr.substring(0, parentStr.length() - 1));
        }
    }

    private static boolean isRoot(final URI uri) {
        return ROOT.equals(uri);
    }
}
