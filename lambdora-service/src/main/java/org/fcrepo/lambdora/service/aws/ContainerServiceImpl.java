package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;

/**
 * A concrete AWS-based implementation of ContainerService.
 *
 * @author dbernstein
 */
public class ContainerServiceImpl implements ContainerService {
    /**
     * Default constructor
     */
    public ContainerServiceImpl() {

    }

    @Override
    public boolean exists(final String path) {
        return false;
    }

    @Override
    public Container get(final String path) {
        return null;
    }

    @Override
    public Container create(final String path) {
        return new ContainerImpl(path);
    }
}
