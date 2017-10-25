package org.fcrepo.lambdora.service.api;

import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.fcrepo.lambdora.service.db.ResourceTriple;

import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.jena.graph.NodeFactory.createLiteral;
import static org.apache.jena.graph.NodeFactory.createURI;

/**
 * An abstract base class for FedoraResource implementations.
 *
 * @author dbernstein
 */
public abstract class FedoraResourceImpl implements FedoraResource {

    private URI resourceName;
    private ResourceTripleDao dao;

    private static Function<ResourceTriple, Triple> toTriple = new Function<ResourceTriple, Triple>() {
        @Override
        public Triple apply(final ResourceTriple t) {
            return new Triple(createURI(t.getRdfSubject()),
                createLiteral(t.getRdfPredicate()), // TODO create proper type of node.
                createLiteral(t.getRdfObject())); // TODO create proper type of node.
        }
    };

    /**
     * Default constructor
     *
     * @param resourceName
     * @param dao
     */
    public FedoraResourceImpl(final URI resourceName, final ResourceTripleDao dao) {
        if (resourceName == null) {
            throw new IllegalArgumentException("resourceName param must be non null");
        }
        if (dao == null) {
            throw new IllegalArgumentException("dao param must be non null");
        }

        this.resourceName = resourceName;
        this.dao = dao;
    }

    @Override
    public URI getResourceName() {
        return this.resourceName;
    }

    @Override
    public Stream<Triple> getTriples() {
        return getTriples(resourceName);
    }

    private Stream<Triple> getTriples(final URI resourceName) {
        return this.dao.findByResourceName(resourceName.toString()).stream().map(toTriple);
    }
}
