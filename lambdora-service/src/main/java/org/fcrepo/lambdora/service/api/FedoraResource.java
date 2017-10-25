package org.fcrepo.lambdora.service.api;

import org.apache.jena.graph.Triple;

import java.net.URI;
import java.util.stream.Stream;

/**
 * A base interface for all Fedora resources.
 *
 * @author dbernstein
 */
public interface FedoraResource {

    /**
     * Get the internal URI that represents this resource
     *
     * @return path
     */
    URI getResourceName();

    /**
     * Returns a stream of triples associated with
     * the resource.
     *
     * @return stream of triples
     */
    Stream<Triple> getTriples();

}
