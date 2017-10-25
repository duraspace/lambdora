package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.FedoraResourceImpl;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

/**
 * AWS-based concrete implementation of the Container interface.
 *
 * @author dbernstein
 */
public class ContainerImpl extends FedoraResourceImpl implements Container {
    /**
     * Constructor
     *
     * @param identifier
     * @param dao
     */
    public ContainerImpl(final URI identifier, final ResourceTripleDao dao) {
        super(identifier, dao);
    }
}
