package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.BinaryService;
import org.fcrepo.lambdora.service.api.FedoraBinary;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

/**
 * Provides access to instances of the BinaryService
 *
 * @author dbernstein
 */
public class BinaryServiceImpl extends FedoraResourceServiceBase<FedoraBinary> implements BinaryService {

    /**
     *
     * @param dao
     */
    public BinaryServiceImpl(final ResourceTripleDao dao) {
        super(dao);
    }

    @Override
    protected FedoraBinary create(final URI identifier) {
        return new FedoraBinaryImpl(identifier, getResourceTripleDao());
    }
}
