package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.Container;

/**
 * ContainerImpl
 *
 * @author dbernstein
 */
public class ContainerImpl extends FedoraResourceImpl implements Container {
    /**
     * Constructor
     *
     * @param path
     */
    public ContainerImpl(final String path) {
        super(path);
    }
}
