package org.fcrepo.lambdora.service;

import org.apache.jena.graph.Triple;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * FedoraResourceImpl
 *
 * @author dbernstein
 */
public class FedoraResourceImpl implements FedoraResource {

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
