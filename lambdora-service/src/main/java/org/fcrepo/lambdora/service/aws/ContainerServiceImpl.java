package org.fcrepo.lambdora.service.aws;

import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.dao.ResourceTripleDao;

import java.net.URI;

import static org.apache.jena.graph.NodeFactory.createLiteral;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.fcrepo.lambdora.service.util.TripleUtil.toResourceTriple;

/**
 * A concrete AWS-based implementation of ContainerService.
 *
 * @author dbernstein
 */
public class ContainerServiceImpl extends FedoraResourceServiceBase<Container> implements ContainerService {

    /**
     * Default constructor
     */
    public ContainerServiceImpl(final ResourceTripleDao dao) {
        super(dao);
    }

    @Override
    protected Container create(final URI identifier) {
        final ResourceTripleDao dao = getResourceTripleDao();
        final Triple t = new Triple(createURI(identifier.toString()),
            createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
            createLiteral("http://www.w3.org/ns/ldp#Container"));

        dao.addResourceTriple(toResourceTriple(identifier, t));
        //TODO recursively create parents if do not exist, adding ldp:contains with a reference to child
        final Container container =  new ContainerImpl(identifier, dao);
        return container;
    }
}
