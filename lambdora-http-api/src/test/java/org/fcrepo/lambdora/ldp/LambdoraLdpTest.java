package org.fcrepo.lambdora.ldp;

import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Triple;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.junit.Before;
import org.junit.Ignore;
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
     */
    @Test
    @Ignore
    public void testGet() throws Exception {

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

        verify(this.mockApplication).containerService();
        verify(this.mockContainerService).find(any());
        verify(this.mockContainer).getIdentifier();
        verify(this.mockContainer).getTriples();
    }


    @Test
    public void testPostCreateContainer() throws Exception {

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

        verify(this.mockApplication).containerService();
        verify(this.mockContainerService).findOrCreate(internalURI);
        verify(this.mockContainer).getIdentifier();
        verify(this.mockContainer).updateTriples(any());
    }

    @Before
    public void setUp() {
        testObj = spy(new LambdoraLdp(mockApplication));

        when(mockApplication.containerService()).thenReturn(mockContainerService);

        mockResponse = new MockHttpServletResponse();

        setField(testObj, "servletResponse", mockResponse);
    }
}
