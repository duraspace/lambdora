package org.fcrepo.lambdora.service.aws;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.slf4j.Logger;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.apache.jena.graph.NodeFactory.createLiteral;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.fcrepo.lambdora.service.util.TripleUtil.toResourceTriple;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A concrete AWS-based implementation of ContainerService.
 *
 * @author dbernstein
 */
public class ContainerServiceImpl extends FedoraResourceServiceBase<Container> implements ContainerService {

    private static final Logger LOGGER = getLogger(ContainerServiceImpl.class);

    /**
     * Default constructor
     */
    public ContainerServiceImpl(final ResourceTripleDao dao) {
        super(dao);
    }

    @Override
    protected Container create(final URI identifier) {
        LOGGER.debug("Create: {}", identifier);

        final ResourceTripleDao dao = getResourceTripleDao();

        // Add system generated triples
        for (Triple t : getSystemTriples(identifier)) {
            dao.addResourceTriple(toResourceTriple(identifier, t));
        }

        //TODO recursively create parents if do not exist, adding ldp:contains with a reference to child
        final Container container =  new ContainerImpl(identifier, dao);
        return container;
    }

    /**
     * Get system generated triples for this resource
     * @param identifier resource URI
     * @return List of Triples
     */
    private List<Triple> getSystemTriples(final URI identifier) {
        final List<Triple> triples = new ArrayList<>();
        final Node subject = createURI(identifier.toString());

        // rdf:type = ldp:container
        triples.add(new Triple(subject,
            createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
            createURI("http://www.w3.org/ns/ldp#Container")));

        // rdf:type = ldp:resource
        triples.add(new Triple(subject,
            createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
            createURI("http://www.w3.org/ns/ldp#RDFSource")));

        // rdf:type = fedora:container
        triples.add(new Triple(subject,
            createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
            createURI("http://fedora.info/definitions/v4/repository#Container")));

        // rdf:type = fedora:resource
        triples.add(new Triple(subject,
            createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
            createURI("http://fedora.info/definitions/v4/repository#Resource")));

        // fedora:created = now
        triples.add(new Triple(subject,
            createURI("http://fedora.info/definitions/v4/repository#created"),
            createLiteral(Instant.now().toString())));

        return triples;
    }
}
