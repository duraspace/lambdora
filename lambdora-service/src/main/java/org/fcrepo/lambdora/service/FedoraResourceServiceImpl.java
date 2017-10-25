package org.fcrepo.lambdora.service;

/**
 * Provides access to instances of the FedoraResource
 *
 * @author dbernstein
 */
public class FedoraResourceServiceImpl implements FedoraResourceService {
    /**
     * Default constructor
     */
    public FedoraResourceServiceImpl() {

    }

    @Override
    public boolean exists(final String path) {
        return false;
    }

    @Override
    public FedoraResource get(final String path) {
        return null;
    }

    @Override
    public FedoraResource create(final String path) {
        return new FedoraResourceImpl(path);
    }
}
