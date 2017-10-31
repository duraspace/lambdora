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

import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
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
        final AwsProxyRequest request = buildRequest("/rest/", "POST");
        final Map<String, String> headers = new HashMap<>();
        headers.put("Slug", "test");
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should have been created", CREATED.getStatusCode(), response.getStatusCode());
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest("/rest/test"), lambdaContext);
        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }

    private AwsProxyRequest buildRequest(final String path, final String method) {
        return new AwsProxyRequestBuilder(path, method).build();
    }

    private AwsProxyRequest buildGetRequest(final String path) {
        return new AwsProxyRequestBuilder(path, "GET").build();
    }
}
