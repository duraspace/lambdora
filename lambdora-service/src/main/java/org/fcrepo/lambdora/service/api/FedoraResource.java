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
     * Get the internal URI identifier that represents this resource
     *
     * @return identifier
     */
    URI getIdentifier();

    /**
     * Returns a stream of triples associated with
     * the resource.
     *
     * @return stream of triples
     */
    Stream<Triple> getTriples();

    /**
     * Adds the stream of triples
     *
     * @param triples
     */
    void updateTriples(Stream<Triple> triples);

    /**
     * Returns parent or null (in the case that the root resource is being interrogated)
     *
     * @return
     */
    URI getParent();
}
