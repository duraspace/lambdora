package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.BinaryService;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.db.DynamoDbTestBase;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * AwsLambdoraApplicationIT - Integration test for the LambdoraApplication
 * DI framework.
 *
 * @author dbernstein
 */
public class AwsLambdoraApplicationIT extends DynamoDbTestBase {

    private LambdoraApplication application;

    @Override
    public void setup() {
        super.setup();

        application = DaggerAwsLambdoraApplication.builder()
            .awsServiceModule(new AwsServiceModule(this.dynamodbClient))
            .build();
    }

    /**
     * A simple smoke test.
     */
    @Test
    public void smokeTest() {

        final ContainerService containerService = application.containerService();
        assertNotNull(containerService);
        final BinaryService binaryService = application.binaryService();
        assertNotNull(binaryService);
    }

    /**
     * Tests creation, existence and retrieval of a new container
     */
    @Test
    public void testContainerServiceRoundTrip() {
        final ContainerService containerService = application.containerService();
        final URI identifier = URI.create("fcrepo:info/test");
        assertFalse("identifier should not exist", containerService.exists(identifier));
        final Container container = containerService.findOrCreate(identifier);
        assertEquals("identifiers are equal", identifier, container.getIdentifier());
        final AtomicInteger count = new AtomicInteger(0);
        container.getTriples().forEach(triple -> {
            assertEquals("subject matches identifier", identifier.toString(), triple.getSubject().getURI());
            assertEquals("predicate is rdf:type", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                triple.getPredicate().getURI());
            assertEquals("object is ldp:Container", "http://www.w3.org/ns/ldp#Container",
                triple.getObject().getLiteral().toString());
            count.incrementAndGet();
        });

        assertEquals("has expected number of triples.", 1, count.get());
        assertTrue("identifier should exist", containerService.exists(identifier));
    }
}
