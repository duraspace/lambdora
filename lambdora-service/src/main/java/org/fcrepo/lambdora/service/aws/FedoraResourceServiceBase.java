package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Service;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.slf4j.Logger;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A base class for Fedora Service implementations.
 *
 * @author dbernstein
 */
public abstract class FedoraResourceServiceBase<T> implements Service<T> {

    private static final Logger LOGGER = getLogger(FedoraResourceServiceBase.class);

    private ResourceTripleDao resourceTripleDao;

    /**
     * Default constructor
     */
    public FedoraResourceServiceBase(final ResourceTripleDao resourceTripleDao) {
        this.resourceTripleDao = resourceTripleDao;
    }

    @Override
    public boolean exists(final URI identifier) {
        return !this.resourceTripleDao.findByResourceName(identifier.toString()).isEmpty();
    }

    @Override
    public T find(final URI identifier) {
        if (exists(identifier)) {
            return load(identifier);
        } else {
            return null;
        }
    }

    @Override
    public T findOrCreate(final URI identifier) {
        LOGGER.debug("FindOrCreate: {}", identifier);
        final T resource = find(identifier);
        return (resource != null ? resource : create(identifier));
    }

    /**
     * return ResourceTripleDao
     *
     * @return
     */
    protected ResourceTripleDao getResourceTripleDao() {
        return resourceTripleDao;
    }

    /**
     * create new resource
     *
     * @param identifier
     * @return
     */
    abstract protected T create(final URI identifier);


    /**
     * load existing resource
     *
     * @param identifier
     * @return
     */
    abstract protected T load(final URI identifier);
}
