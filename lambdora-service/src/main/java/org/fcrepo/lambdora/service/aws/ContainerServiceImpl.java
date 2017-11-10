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
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.CONTAINER;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.CONTAINS;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.CREATED_DATE;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.HAS_PARENT;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.RDF_SOURCE;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.TYPE;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.FEDORA_CONTAINER;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.FEDORA_RESOURCE;

import static org.fcrepo.lambdora.common.utils.UriUtils.getParent;
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
        final Node identifierNode = createURI(identifier.toString());
        final ResourceTripleDao dao = getResourceTripleDao();

        // Add system generated triples
        for (Triple t : getSystemTriples(identifier)) {
            dao.addResourceTriple(toResourceTriple(identifier, t));
        }

        //add containment triples
        final URI parent = getParent(identifier);
        if (parent != null) {
            final Node parentNode = createURI(parent.toString());

            dao.addResourceTriple(toResourceTriple(identifier,
                new Triple(identifierNode,
                    HAS_PARENT.asNode(),
                    parentNode)));

            dao.addResourceTriple(toResourceTriple(parent,
                new Triple(parentNode,
                    CONTAINS.asNode(),
                    identifierNode)));

        }

        //TODO recursively create parents if do not exist

        return new ContainerImpl(identifier, dao);
    }

    @Override
    protected Container load(final URI identifier) {
        LOGGER.debug("Load: {}", identifier);
        final ResourceTripleDao dao = getResourceTripleDao();
        return new ContainerImpl(identifier, dao);
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
            TYPE.asNode(),
            CONTAINER.asNode()));

        // rdf:type = ldp:resource
        triples.add(new Triple(subject,
            TYPE.asNode(),
            RDF_SOURCE.asNode()));

        // rdf:type = fedora:container
        triples.add(new Triple(subject,
            TYPE.asNode(),
            FEDORA_CONTAINER.asNode()));

        // rdf:type = fedora:resource
        triples.add(new Triple(subject,
            TYPE.asNode(),
            FEDORA_RESOURCE.asNode()));

        // fedora:created = now
        triples.add(new Triple(subject,
            CREATED_DATE.asNode(),
            createLiteral(Instant.now().toString())));

        return triples;
    }
}
