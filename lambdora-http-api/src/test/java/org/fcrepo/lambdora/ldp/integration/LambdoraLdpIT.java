package org.fcrepo.lambdora.ldp.integration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import org.fcrepo.lambdora.common.test.IntegrationTestBase;
import org.fcrepo.lambdora.ldp.JerseyApplication;
import org.fcrepo.lambdora.ldp.JerseyRequestHandler;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.aws.AwsServiceModule;
import org.fcrepo.lambdora.service.aws.DaggerAwsLambdoraApplication;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

/**
 * LambdoraLdpIT
 *
 * @author dbernstein
 */
public class LambdoraLdpIT extends IntegrationTestBase {

    @Mock
    private MockLambdaContext lambdaContext;

    private JerseyRequestHandler handler;

    /**
     * Default constructor
     */
    public LambdoraLdpIT() {
        super();
    }

    @Before
    public void setUp() {
        super.setup();
        final LambdoraApplication lambdoraApplication = DaggerAwsLambdoraApplication.builder()
            .awsServiceModule(new AwsServiceModule(this.dynamodbClient)).build();
        final JerseyApplication application = new JerseyApplication(lambdoraApplication);
        handler = new JerseyRequestHandler(application);

    }

    @Test
    public void testGetRoot() {
        final AwsProxyRequest request = buildGetRequest("/rest/");
        final Map<String, String> headers = new HashMap<>();
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should exist", OK.getStatusCode(), response.getStatusCode());
    }


    @Test
    public void testGetNonExistentResource() {
        final AwsProxyRequest request = buildGetRequest("/rest/does-not-exist");
        final Map<String, String> headers = new HashMap<>();
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should not exist", NOT_FOUND.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testCreateResource() {
        createResource("test");
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest("/rest/test"), lambdaContext);
        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }

    @Test
    public void testCreateResourceWithNullSlug() {
        final AwsProxyRequest request = buildRequest("/rest/", "POST");
        final Map<String, String> headers = new HashMap<>();
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should have been created", CREATED.getStatusCode(), response.getStatusCode());
        final String location = response.getHeaders().get("Location");
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest(URI.create(location).getPath()),
            lambdaContext);

        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }

    @Test
    public void testHeadOnRoot() {
        final AwsProxyRequest request = buildRequest("/rest/", "HEAD");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should allow head", OK.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testHeadOnExistentResource() {
        createResource("test");
        final AwsProxyRequest request2 = buildRequest("/rest/test", "HEAD");
        final AwsProxyResponse response2 = handler.handleRequest(request2, lambdaContext);
        assertEquals("resource should return OK on HEAD", OK.getStatusCode(), response2.getStatusCode());
    }

    @Test
    public void testHeadOnNonExistentResource() {
        final AwsProxyRequest request = buildRequest("/rest/does-not-exist", "HEAD");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should not exist", NOT_FOUND.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnRoot() {
        final AwsProxyRequest request = buildRequest("/rest/", "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should allow put", METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnNonExistentResource() {
        final AwsProxyRequest request = buildRequest("/rest/test", "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("new resource should allow put", CREATED.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnExistentResource() {
        final String resource = "resource";
        createResource(resource);
        final AwsProxyRequest request = buildRequest("/rest/" + resource, "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should return NO_CONTENT on PUT", NO_CONTENT.getStatusCode(), response.getStatusCode());
    }

    private void createResource(final String resourcePath) {
        final AwsProxyRequest request1 = buildRequest("/rest/", "POST");
        final Map<String, String> headers = new HashMap<>();
        headers.put("Slug", resourcePath);
        request1.setHeaders(headers);
        final AwsProxyResponse response1 = handler.handleRequest(request1, lambdaContext);
        assertEquals("resource should have been created", CREATED.getStatusCode(), response1.getStatusCode());
    }

    private AwsProxyRequest buildRequest(final String path, final String method) {
        return new AwsProxyRequestBuilder(path, method).build();
    }

    private AwsProxyRequest buildGetRequest(final String path) {
        return new AwsProxyRequestBuilder(path, "GET").build();
    }
}
