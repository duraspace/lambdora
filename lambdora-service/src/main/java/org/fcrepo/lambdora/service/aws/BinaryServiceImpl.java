package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.FedoraBinary;
import org.fcrepo.lambdora.service.api.BinaryService;

/**
 * Provides access to instances of the BinaryService
 *
 * @author dbernstein
 */
public class BinaryServiceImpl implements BinaryService {
    /**
     * Default constructor
     */
    public BinaryServiceImpl() {

    }

    @Override
    public boolean exists(final String path) {
        return false;
    }

    @Override
    public FedoraBinary get(final String path) {
        return null;
    }

    @Override
    public FedoraBinary create(final String path) {
        return new FedoraBinaryImpl(path);
    }
}
