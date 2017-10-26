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
     * @param identifier
     * @return
     */
    boolean exists(final URI identifier);

    /**
     * Returns resource if exists otherwise null.
     *
     * @param identifier
     * @return
     */
    T find(final URI identifier);

    /**
     * Creates resource
     *
     * @param identifier
     * @return newly created resource
     */
    T findOrCreate(final URI identifier);
}
