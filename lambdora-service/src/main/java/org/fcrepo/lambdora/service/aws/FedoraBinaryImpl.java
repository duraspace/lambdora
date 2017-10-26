package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.FedoraBinary;
import org.fcrepo.lambdora.service.api.FedoraResourceImpl;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

/**
 * AWS-based concrete implementation of the FedoraBinary interface.
 *
 * @author dbernstein
 */
public class FedoraBinaryImpl extends FedoraResourceImpl implements FedoraBinary {
    /**
     * Constructor
     *
     * @param resourceName
     * @param dao
     */
    public FedoraBinaryImpl(final URI resourceName, final ResourceTripleDao dao) {
        super(resourceName, dao);
    }
}
