package org.fcrepo.lambdora.ldp;

import com.google.common.collect.ImmutableSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.fcrepo.http.commons.responses.RdfNamespacedStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.fcrepo.kernel.api.RdfCollectors.toModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Notes
 *
 * This (what exists of it) is mostly copied from FedoraLdpTest, to try and be able to see what is happening
 * through the Jersey layer, without deploying it.
 *
 * It doesn't currently pass checkstyle (was testing with it disabled just to make progress without worrying about
 * how it would finally be structured).
 *
 * @author gtriggs
 */

@RunWith(MockitoJUnitRunner.class)
public class LambdoraLdpTest {
    private LambdoraLdp testObj;

    @Mock
    private Request mockRequest;

    private HttpServletResponse mockResponse;

    /**
     * Testing the get getResource method
     *
     * NB I was hoping to test the serialization after a response is handed to Jersey, but turns
     * out that isn't what this method does
     *
     * In a test environemt, getResource will return with a valid Response.
     * However, in AWS, it is still giving an Internal Server Error (presumably due to the
     * transformation providers in Jersey)
     */
    @Test
    @Ignore
    public void testGet() throws Exception {
        /**
         * Not currently dealing with FedoraReource classes
         */
//        setResource(FedoraResource.class);


        final Response actual = testObj.getResource(null);
        assertEquals(OK.getStatusCode(), actual.getStatus());


        /**
         * getResource doesn't add any headers at the moment
         */
//        assertTrue("Should have a Link header", mockResponse.containsHeader(LINK));
//        assertTrue("Should have an Allow header", mockResponse.containsHeader("Allow"));
//        assertTrue("Should be an LDP Resource",
//                mockResponse.getHeaders(LINK).contains("<" + LDP_NAMESPACE + "Resource>;rel=\"type\""));

        try (final RdfNamespacedStream entity = (RdfNamespacedStream) actual.getEntity()) {
            final Model model = entity.stream.collect(toModel());
            final List<String> rdfNodes = model.listObjects().mapWith(RDFNode::toString).toList();
            assertTrue("Expected RDF contexts missing", rdfNodes.containsAll(ImmutableSet.of(
                    "LDP_CONTAINMENT", "LDP_MEMBERSHIP", "PROPERTIES", "SERVER_MANAGED")));
        }
    }

    @Before
    public void setUp() {
        /**
         * Prepare the test environment
         *
         * LambdoraLdp doesn't currently have a lot of the fields that are injected into a full
         * FedoraLdp class hierarchy
         */
        testObj = spy(new LambdoraLdp());

        mockResponse = new MockHttpServletResponse();

//        idTranslator = new HttpResourceConverter(mockSession,
//                UriBuilder.fromUri("http://localhost/fcrepo/{path: .*}"));

//        setField(testObj, "request", mockRequest);
        setField(testObj, "servletResponse", mockResponse);
//        setField(testObj, "uriInfo", getUriInfoImpl());
//        setField(testObj, "headers", mockHeaders);
//        setField(testObj, "idTranslator", idTranslator);
//        setField(testObj, "nodeService", mockNodeService);
//        setField(testObj, "containerService", mockContainerService);
//        setField(testObj, "binaryService", mockBinaryService);
//        setField(testObj, "httpConfiguration", mockHttpConfiguration);
//        setField(testObj, "session", mockSession);
//        setField(testObj, "securityContext", mockSecurityContext);
//        setField(testObj, "lockManager", mockLockManager);

//        when(mockHttpConfiguration.putRequiresIfMatch()).thenReturn(false);

//        when(mockContainer.getEtagValue()).thenReturn("");
//        when(mockContainer.getPath()).thenReturn(path);
//        when(mockContainer.getDescription()).thenReturn(mockContainer);
//        when(mockContainer.getDescribedResource()).thenReturn(mockContainer);

//        when(mockNonRdfSourceDescription.getEtagValue()).thenReturn("");
//        when(mockNonRdfSourceDescription.getPath()).thenReturn(binaryDescriptionPath);
//        when(mockNonRdfSourceDescription.getDescribedResource()).thenReturn(mockBinary);

//        when(mockBinary.getEtagValue()).thenReturn("");
//        when(mockBinary.getPath()).thenReturn(binaryPath);
//        when(mockBinary.getDescription()).thenReturn(mockNonRdfSourceDescription);

//        when(mockHeaders.getHeaderString("user-agent")).thenReturn("Test UserAgent");

//        when(mockLockManager.lockForRead(any())).thenReturn(mockLock);
//        when(mockLockManager.lockForWrite(any(), any(), any())).thenReturn(mockLock);
//        when(mockLockManager.lockForDelete(any())).thenReturn(mockLock);
//        when(mockSession.getId()).thenReturn("foo1234");
//        when(mockSession.getFedoraSession()).thenReturn(mockFedoraSession);
    }
}
