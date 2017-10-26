package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Service;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

/**
 * A base class for Fedora Service implementations.
 *
 * @author dbernstein
 */
public abstract class FedoraResourceServiceBase<T> implements Service<T> {
    private ResourceTripleDao resourceTripleDao;

    /**
     * Default constructor
     */
    public FedoraResourceServiceBase(final ResourceTripleDao resourceTripleDao) {
        this.resourceTripleDao = resourceTripleDao;
    }

    @Override
    public boolean exists(final URI resourceName) {
        return this.resourceTripleDao.findByResourceName(resourceName.toString()).size() > 0;
    }

    @Override
    public T get(final URI resourceName) {
        if (exists(resourceName)) {
            return createInstance(resourceName);
        } else {
            return null;
        }
    }

    @Override
    public T create(final URI resourceName) {
        return null;
    }

    protected ResourceTripleDao getResourceTripleDao() {
        return resourceTripleDao;
    }

    abstract protected T createInstance(final URI uri);
}
