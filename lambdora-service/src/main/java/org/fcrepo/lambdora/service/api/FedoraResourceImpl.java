package org.fcrepo.lambdora.service.api;

import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.fcrepo.lambdora.service.db.ResourceTriple;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fcrepo.lambdora.service.util.TripleUtil.toResourceTriple;
import static org.fcrepo.lambdora.service.util.TripleUtil.toTriple;

/**
 * An abstract base class for FedoraResource implementations.
 *
 * @author dbernstein
 */
public abstract class FedoraResourceImpl implements FedoraResource {

    private URI identifier;
    private ResourceTripleDao dao;

    /**
     * Simple function converting a ResourceTriple to Triple
     */
    private static Function<ResourceTriple, Triple> toTriple = new Function<ResourceTriple, Triple>() {
        @Override
        public Triple apply(final ResourceTriple t) {
            return toTriple(t.getRdfTriple());
        }
    };


    /**
     * Default constructor
     *
     * @param identifier
     * @param dao
     */
    public FedoraResourceImpl(final URI identifier, final ResourceTripleDao dao) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier param must be non null");
        }
        if (dao == null) {
            throw new IllegalArgumentException("dao param must be non null");
        }

        this.identifier = identifier;
        this.dao = dao;
    }

    @Override
    public URI getIdentifier() {
        return this.identifier;
    }

    @Override
    public Stream<Triple> getTriples() {
        return getTriples(identifier);
    }

    private Stream<Triple> getTriples(final URI resourceName) {
        return this.dao.findByResourceName(resourceName.toString()).stream().map(toTriple);
    }

    @Override
    public void updateTriples(final Stream<Triple> triples) {
        triples.forEach(triple -> {
            this.dao.addResourceTriple(toResourceTriple(getIdentifier(), triple));
        });
    }

    @Override
    public URI getParent() {
        final List<ResourceTriple> parentList = this.dao.findByResourceNameAndPredicate(this.identifier.toString(),
            "http://fedora.info/definitions/v4/repository#hasParent").stream().collect(Collectors.toList());
        if (!parentList.isEmpty()) {
            return URI.create(parentList.get(0).getRdfSubject());
        } else {
            return null;
        }
    }
}
