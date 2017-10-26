package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

/**
 * A concrete AWS-based implementation of ContainerService.
 *
 * @author dbernstein
 */
public class ContainerServiceImpl extends FedoraResourceServiceBase<Container> implements ContainerService {

    /**
     * Default constructor
     */
    public ContainerServiceImpl(final ResourceTripleDao dao) {
        super(dao);
    }

    @Override
    protected Container createInstance(final URI uri) {
        return new ContainerImpl(uri, getResourceTripleDao());
    }
}
