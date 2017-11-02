package org.fcrepo.lambdora.service.aws;

import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.slf4j.Logger;

import java.net.URI;

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

    private static URI getParent(final URI uri) {
        if (uri.getPath().equals("") || uri.getPath().equals("/")) {
            return null;
        } else {
            return uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
        }
    }

    @Override
    protected Container create(final URI identifier) {
        LOGGER.debug("Create: {}", identifier);
        final String identifierStr = identifier.toString();
        final URI parent = getParent(identifier);

        final ResourceTripleDao dao = getResourceTripleDao();

        dao.addResourceTriple(toResourceTriple(identifier,
            new Triple(createURI(identifierStr),
                createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                createURI("http://www.w3.org/ns/ldp#Container"))));
        dao.addResourceTriple(toResourceTriple(identifier,
            new Triple(createURI(identifierStr),
                createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                createURI("http://www.w3.org/ns/ldp#RDFSource"))));


        //TODO recursively create parents if do not exist, adding ldp:contains with a reference to child
        final Container container = new ContainerImpl(identifier, dao);
        if (parent != null) {
            final String parentStr = parent.toString();
            dao.addResourceTriple(toResourceTriple(identifier,
                new Triple(createURI(identifierStr),
                    createURI("http://fedora.info/definitions/v4/repository#hasParent"),
                    createURI(parentStr))));

            dao.addResourceTriple(toResourceTriple(parent,
                new Triple(createURI(parentStr),
                    createURI("http://www.w3.org/ns/ldp#contains"),
                    createURI(identifierStr))));
        }
        return container;
    }
}
