package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.FedoraBinary;

/**
 * ContainerImpl
 *
 * @author dbernstein
 */
public class FedoraBinaryImpl extends FedoraResourceImpl implements FedoraBinary {
    /**
     * Constructor
     *
     * @param path
     */
    public FedoraBinaryImpl(final String path) {
        super(path);
    }
}
