package org.fcrepo.lambdora.service.aws;

import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.FedoraResource;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * An abstract base class for FedoraResource implementations.
 *
 * @author dbernstein
 */
public abstract class FedoraResourceImpl implements FedoraResource {

    private String path;

    /**
     * Default constructor
     *
     * @param path
     */
    public FedoraResourceImpl(final String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Stream<Triple> getTriples() {
        return new ArrayList<Triple>().stream();
    }
}
