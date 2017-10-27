package org.fcrepo.lambdora.ldp;

import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Mock
    private LambdoraApplication mockApplication;

    @Mock
    private ContainerService mockContainerService;

    @Mock
    private Container mockContainer;


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
    public void testGet() throws Exception {
        /**
         * Not currently dealing with FedoraReource classes
         */
//        setResource(FedoraResource.class);

        testObj.externalPath = "";

        final URI internalURI = URI.create("fedora://info/test");

        when(this.mockContainerService.find(any())).thenReturn(mockContainer);
        when(this.mockContainer.getIdentifier()).thenReturn(internalURI);
        when(this.mockContainer.getTriples()).thenReturn(Arrays.asList(new Triple(createURI("http://test"),
                                                                                  createURI("http://test"),
                                                                                  createURI("http://test")))
                                                                .stream());
        final Response actual = testObj.getResource(null);
        assertEquals(OK.getStatusCode(), actual.getStatus());
        /**
         * getResource doesn't add any headers at the moment
         */
//        assertTrue("Should have a Link header", mockResponse.containsHeader(LINK));
//        assertTrue("Should have an Allow header", mockResponse.containsHeader("Allow"));
//        assertTrue("Should be an LDP Resource",
//                mockResponse.getHeaders(LINK).contains("<" + LDP_NAMESPACE + "Resource>;rel=\"type\""));

//        try (final RdfNamespacedStream entity = (RdfNamespacedStream) actual.getEntity()) {
//            final Model model = entity.stream.collect(toModel());
//            final List<String> rdfNodes = model.listObjects().mapWith(RDFNode::toString).toList();
//            assertTrue("Expected RDF contexts missing", rdfNodes.containsAll(ImmutableSet.of(
//                    "LDP_CONTAINMENT", "LDP_MEMBERSHIP", "PROPERTIES", "SERVER_MANAGED")));
//        }

        verify(this.mockApplication).containerService();
        verify(this.mockContainerService).find(any());
        verify(this.mockContainer).getIdentifier();
        verify(this.mockContainer).getTriples();
    }


    @Test
    public void testPostCreateContainer() throws Exception {
        /**
         * Not currently dealing with FedoraReource classes
         */
//        setResource(FedoraResource.class);

        testObj.externalPath = "";

        final String requestBody =
            "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
            "@prefix ldp:  <http://www.w3.org/ns/ldp#> .\n" +
            "\n" +
            "<http://demo.fcrepo.org:8080/fcrepo/rest/>\n" +
            "    rdf:type                       ldp:RDFSource ;\n" +
            "    rdf:type                       ldp:BasicContainer ;\n";

        final InputStream is = IOUtils.toInputStream(requestBody, Charset.defaultCharset());
        final URI internalURI = URI.create("fedora://info/test");
        when(this.mockContainerService.findOrCreate(internalURI)).thenReturn(mockContainer);
        when(this.mockContainer.getIdentifier()).thenReturn(internalURI);
        doNothing().when(this.mockContainer).updateTriples(any());
        final Response actual = testObj.createObject(null, null, "test", is,null,null);
        assertEquals(CREATED.getStatusCode(), actual.getStatus());
        assertEquals("/rest/test", actual.getLocation().getPath());


        /**
         * getResource doesn't add any headers at the moment
         */
//        assertTrue("Should have a Link header", mockResponse.containsHeader(LINK));
//        assertTrue("Should have an Allow header", mockResponse.containsHeader("Allow"));
//        assertTrue("Should be an LDP Resource",
//                mockResponse.getHeaders(LINK).contains("<" + LDP_NAMESPACE + "Resource>;rel=\"type\""));

//        try (final RdfNamespacedStream entity = (RdfNamespacedStream) actual.getEntity()) {
//            final Model model = entity.stream.collect(toModel());
//            final List<String> rdfNodes = model.listObjects().mapWith(RDFNode::toString).toList();
//            assertTrue("Expected RDF contexts missing", rdfNodes.containsAll(ImmutableSet.of(
//                    "LDP_CONTAINMENT", "LDP_MEMBERSHIP", "PROPERTIES", "SERVER_MANAGED")));
//        }

        verify(this.mockApplication).containerService();
        verify(this.mockContainerService).findOrCreate(internalURI);
        verify(this.mockContainer).getIdentifier();
        verify(this.mockContainer).updateTriples(any());
    }

    @Before
    public void setUp() {
        /**
         * Prepare the test environment
         *
         * LambdoraLdp doesn't currently have a lot of the fields that are injected into a full
         * FedoraLdp class hierarchy
         */
        testObj = spy(new LambdoraLdp(mockApplication));

        when(mockApplication.containerService()).thenReturn(mockContainerService);

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
