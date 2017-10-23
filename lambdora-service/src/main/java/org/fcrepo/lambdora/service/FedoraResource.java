package org.fcrepo.lambdora.service;

import org.apache.jena.graph.Triple;

import java.util.stream.Stream;

/**
 * FedoraResource
 *
 * @author dbernstein
 */
public interface FedoraResource {

    /**
     * Get the path to the resource
     *
     * @return path
     */
    String getPath();

    /**
     * Returns a stream of triples associated with
     * the resource.
     *
     * @return stream of triples
     */
    Stream<Triple> getTriples();

}
