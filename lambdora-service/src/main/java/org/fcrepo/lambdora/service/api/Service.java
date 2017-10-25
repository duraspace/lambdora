package org.fcrepo.lambdora.service.api;

import java.net.URI;

/**
 * Generic Service interface for creation and retrieval of
 * resources.
 *
 * @author dbernstein
 */
public interface Service<T> {

    /**
     * Returns true if exists
     *
     * @param resourceName
     * @return
     */
    boolean exists(final URI resourceName);

    /**
     * Returns resource if exists otherwise null.
     *
     * @param resourceName
     * @return
     */
    T get(final URI resourceName);

    /**
     * Creates resource
     *
     * @param resourceName
     * @return
     */
    T create(final URI resourceName);
}
