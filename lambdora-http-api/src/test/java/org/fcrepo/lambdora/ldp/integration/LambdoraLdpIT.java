package org.fcrepo.lambdora.ldp.integration;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.fcrepo.lambdora.common.test.IntegrationTestBase;
import org.fcrepo.lambdora.ldp.JerseyApplication;
import org.fcrepo.lambdora.ldp.JerseyRequestHandler;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.aws.AwsServiceModule;
import org.fcrepo.lambdora.service.aws.DaggerAwsLambdoraApplication;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.HAS_PARENT;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.INTERNAL_URI_PREFIX;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.TYPE;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.CONTAINS;

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

    private String context = "context";
    private String protocol = "http";
    private String host = "host";

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
        final AwsProxyRequest request = buildGetRequest("/");
        final Map<String, String> headers = new HashMap<>();
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should exist", OK.getStatusCode(), response.getStatusCode());
        final Model model = createModel(response);
        final AtomicInteger count = new AtomicInteger();
        model.listStatements().forEachRemaining(x -> {
            assertFalse("Root should have not parent", x.getPredicate().getURI().equals(
                HAS_PARENT.getURI()));
            count.incrementAndGet();
        });
    }


    @Test
    public void testGetNonExistentResource() {
        final AwsProxyRequest request = buildGetRequest("/does-not-exist");
        final Map<String, String> headers = new HashMap<>();
        request.setHeaders(headers);
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should not exist", NOT_FOUND.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testCreateResource() {
        createResource("test");
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest("/test"), lambdaContext);
        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }


    @Test
    public void testCreateContainerWithTriples() {

        final String body = "<> <http://purl.org/dc/elements/1.1/title> 'A Title' ; " +
            "<http://www.openarchives.org/ore/terms/proxyFor> " +
            "<http://sweetclipart.com/multisite/sweetclipart/files/monkey_with_banana.png> ; " +
            "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
            "<http://www.openarchives.org/ore/terms/Proxy> . ";

        createResource("test", body);
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest("/test"), lambdaContext);

        final Model model = createModel(getResponse);
        model.listStatements().forEachRemaining(x -> {
            if (x.getPredicate().getURI().equals(TYPE.getURI())) {
                assertEquals("subjects of rdf type predicates should equal the resource URI",
                    getBaseUri() + "/test", x.getSubject().getURI());
            }

            if (x.getPredicate().getURI().equals(HAS_PARENT.getURI())) {
                assertEquals("subjects of rdf type predicates should equal the resource URI",
                    getBaseUri() + "/", x.getObject().asResource().getURI());
            }

        });

        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }

    @Test
    public void testLdpContains() {
        createResource("test");
        final AwsProxyResponse response1 = handler.handleRequest(buildGetRequest("/"), lambdaContext);
        testContains(response1, getBaseUri() + "/test");

        createResource("test/child");
        final AwsProxyResponse response2 = handler.handleRequest(buildGetRequest("/test"), lambdaContext);
        testContains(response2, getBaseUri() + "/test/child");

    }

    private void testContains(final AwsProxyResponse response, final String containedUri) {
        final Model model = createModel(response);
        final AtomicInteger count = new AtomicInteger();
        model.listStatements().forEachRemaining(x -> {
            if (x.getPredicate().getURI().equals(CONTAINS.getURI())) {
                assertEquals("subjects of rdf type predicates should equal the resource URI",
                    containedUri, x.getObject().asResource().getURI());
                count.incrementAndGet();
            }
        });
        assertEquals("expected number of contains statements", 1, count.get());
    }

    private Model createModel(final AwsProxyResponse getResponse) {
        final Model model = ModelFactory.createDefaultModel();
        final String responseBody = getResponse.getBody();
        model.read(new ByteArrayInputStream(responseBody.getBytes(Charset.defaultCharset())), "", "TTL");
        return model;
    }

    @Test
    public void testCreateResourceWithNullSlug() {
        final AwsProxyRequest request = buildRequest("/", "POST");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should have been created", CREATED.getStatusCode(), response.getStatusCode());
        final String location = response.getHeaders().get("Location");
        final AwsProxyResponse getResponse = handler.handleRequest(buildGetRequest(location.replace(getBaseUri(), "")),
            lambdaContext);

        assertTrue("rdf should not contain any references to internal base uri.",
            !getResponse.getBody().contains(INTERNAL_URI_PREFIX));
        assertEquals("newly created resource should exist", OK.getStatusCode(), getResponse.getStatusCode());
    }

    @Test
    public void testHeadOnRoot() {
        final AwsProxyRequest request = buildRequest("/", "HEAD");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should allow head", OK.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testHeadOnExistentResource() {
        createResource("test");
        final AwsProxyRequest request2 = buildRequest("/test", "HEAD");
        final AwsProxyResponse response2 = handler.handleRequest(request2, lambdaContext);
        assertEquals("resource should return OK on HEAD", OK.getStatusCode(), response2.getStatusCode());
    }

    @Test
    public void testHeadOnNonExistentResource() {
        final AwsProxyRequest request = buildRequest("/does-not-exist", "HEAD");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should not exist", NOT_FOUND.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnRoot() {
        final AwsProxyRequest request = buildRequest("/", "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("root should allow put", METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnNonExistentResource() {
        final AwsProxyRequest request = buildRequest("/test", "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("new resource should allow put", CREATED.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPutOnExistentResource() {
        final String resource = "resource";
        createResource(resource);
        final AwsProxyRequest request = buildRequest("/" + resource, "PUT");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should return NO_CONTENT on PUT", NO_CONTENT.getStatusCode(), response.getStatusCode());
    }

    @Test
    public void testPatch() {
        final String resource = "resource";
        createResource(resource);
        final AwsProxyRequest request = buildPatchRequest("/" + resource, "PATCH");
        final AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        assertEquals("resource should return NO_CONTENT on PATCH",
                     NO_CONTENT.getStatusCode(),
                     response.getStatusCode());
    }

    private String createResource(final String resourcePath) {
        return createResource(resourcePath, null);
    }

    private String createResource(final String resourcePath, final String requestBody) {
        final AwsProxyRequest request1 = buildRequest("/", "POST");
        final Map<String, String> headers = new HashMap<>();
        headers.put("Slug", resourcePath);
        request1.getHeaders().putAll(headers);
        request1.setBody(requestBody);
        final AwsProxyResponse response1 = handler.handleRequest(request1, lambdaContext);
        final String location = response1.getHeaders().get("Location");
        assertEquals("location header is not correct", getBaseUri() + "/" + resourcePath, location);
        assertEquals("resource should have been created", CREATED.getStatusCode(), response1.getStatusCode());
        return location;
    }

    private AwsProxyRequest buildRequest(final String path, final String method) {

        return new AwsProxyRequestBuilder(path, method)
            .serverName(host)
            .stage(context)
            .header("X-Forwarded-Proto", protocol)
            .build();
    }

    private AwsProxyRequest buildPatchRequest(final String path, final String method) {

        return new AwsProxyRequestBuilder(path, method)
            .serverName(host)
            .stage(context)
            .header("Content-Type", "application/sparql-update")
            .build();
    }

    private String getBaseUri() {
        return protocol + "://" + host + "/" + context;
    }

    private AwsProxyRequest buildGetRequest(final String path) {
        return buildRequest(path, "GET");
    }
}
