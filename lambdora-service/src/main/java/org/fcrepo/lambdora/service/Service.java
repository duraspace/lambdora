package org.fcrepo.lambdora.service;

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
     * @param path
     * @return
     */
    boolean exists(final String path);

    /**
     * Returns resource if exists otherwise null.
     *
     * @param path
     * @return
     */
    T get(final String path);

    /**
     * Creates resource
     *
     * @param path
     * @return
     */
    T create(final String path);
}
