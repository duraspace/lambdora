package org.fcrepo.lambdora.ldp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.http.commons.domain.ContentLocation;
import org.fcrepo.kernel.api.exception.InvalidChecksumException;
import org.fcrepo.kernel.api.exception.MalformedRdfException;
import org.fcrepo.kernel.api.exception.UnsupportedAlgorithmException;
import org.fcrepo.kernel.api.rdf.DefaultRdfStream;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.FedoraResource;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.aws.DaggerAwsLambdoraApplication;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LINK;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.fcrepo.http.commons.domain.RDFMediaType.JSON_LD;
import static org.fcrepo.http.commons.domain.RDFMediaType.N3_ALT2_WITH_CHARSET;
import static org.fcrepo.http.commons.domain.RDFMediaType.N3_WITH_CHARSET;
import static org.fcrepo.http.commons.domain.RDFMediaType.NTRIPLES;
import static org.fcrepo.http.commons.domain.RDFMediaType.RDF_XML;
import static org.fcrepo.http.commons.domain.RDFMediaType.TEXT_HTML_WITH_CHARSET;
import static org.fcrepo.http.commons.domain.RDFMediaType.TEXT_PLAIN_WITH_CHARSET;
import static org.fcrepo.http.commons.domain.RDFMediaType.TURTLE_WITH_CHARSET;
import static org.fcrepo.http.commons.domain.RDFMediaType.TURTLE_X;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.INTERNAL_URI_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Main entry points for Jersey request handling
 *
 * @author gtriggs
 */
@Path("/{path: .*}")
public class LambdoraLdp {

    private static final Logger LOGGER = getLogger(LambdoraLdp.class);
    private final URI rootURI;

    @PathParam("path")
    protected String externalPath;

    /**
     * For getting user agent
     */
    @Context
    protected HttpHeaders headers;

    @Context
    protected Request request;
    @Context
    protected HttpServletResponse servletResponse;
    @Context
    protected UriInfo uriInfo;

    @Inject
    LambdoraApplication application;

    /**
     * Default JAX-RS entry point
     */
    public LambdoraLdp() {
        this(DaggerAwsLambdoraApplication.builder().build());
    }

    /**
     * An entry point used currently for testing.
     *
     * @param application
     */
    public LambdoraLdp(final LambdoraApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("application must be non-null");
        }
        this.application = application;
        this.rootURI = createFromPath("/");
    }

    /**
     * Retrieve the repository resource profile
     *
     * @param rangeValue the range value
     * @return a binary or the triples for the specified node
     * @throws IOException if IO exception occurred
     */
    @GET
    @Produces({TURTLE_WITH_CHARSET + ";qs=1.0", JSON_LD + ";qs=0.8",
            N3_WITH_CHARSET, N3_ALT2_WITH_CHARSET, RDF_XML, NTRIPLES, TEXT_PLAIN_WITH_CHARSET,
            TURTLE_X, TEXT_HTML_WITH_CHARSET})
    public Response getResource(@HeaderParam("Range") final String rangeValue) throws IOException {
        LOGGER.info("GET: {}", externalPath);

        final URI internalURI = createFromPath(externalPath);

        final Container container = getContainerService().find(internalURI);
        if (container == null) {
            if (!isRoot(internalURI)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return getResource(createRoot());
            }
        }

        return getResource(container);
    }

    /**
     * Retrieve the resource headers
     *
     * @return response
     */
    @HEAD
    @Produces({TURTLE_WITH_CHARSET + ";qs=1.0", JSON_LD + ";qs=0.8",
        N3_WITH_CHARSET, N3_ALT2_WITH_CHARSET, RDF_XML, NTRIPLES, TEXT_PLAIN_WITH_CHARSET,
        TURTLE_X, TEXT_HTML_WITH_CHARSET})
    public Response head() throws UnsupportedAlgorithmException {
        LOGGER.info("HEAD for: {}", externalPath);

        final URI internalUri = createFromPath(externalPath);

        final Container container = getContainerService().find(internalUri);

        if (container == null) {
            if (!isRoot(internalUri)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return ok().build();
    }


    /**
     * Create a resource at a specified path, or replace triples with provided RDF.
     *
     * @param requestContentType the request content type
     * @param requestBodyStream  the request body stream
     * @param contentDisposition the content disposition value
     * @param ifMatch            the if-match value
     * @param links              the link values
     * @param digest             the digest header
     * @return 204
     * @throws InvalidChecksumException      if invalid checksum exception occurred
     * @throws MalformedRdfException         if malformed rdf exception occurred
     * @throws UnsupportedAlgorithmException
     */
    @PUT
    @Consumes
    public Response createOrReplaceObjectRdf(
        @HeaderParam(CONTENT_TYPE) final MediaType requestContentType,
        @ContentLocation final InputStream requestBodyStream,
        @HeaderParam(CONTENT_DISPOSITION) final ContentDisposition contentDisposition,
        @HeaderParam("If-Match") final String ifMatch,
        @HeaderParam(LINK) final List<String> links,
        @HeaderParam("Digest") final String digest)
        throws InvalidChecksumException, MalformedRdfException, UnsupportedAlgorithmException {

        final URI internalUri = createFromPath(externalPath);
        if (isRoot(internalUri)) {
            //method not allowed if root
            return Response.status(METHOD_NOT_ALLOWED).build();
        }

        final ContainerService containerService = getContainerService();
        //create resource if exists
        if (!containerService.exists(internalUri)) {
            final Container container = containerService.findOrCreate(internalUri);
            final Model model = ModelFactory.createDefaultModel();
            model.read(requestBodyStream, null, "TTL");
            final Stream<Triple> triples = model.listStatements().toList().stream().map(Statement::asTriple);
            container.updateTriples(triples);
            return created(toExternalURI(container.getIdentifier(), headers)).build();
        } else {
            //TODO delete resource, create resource, and update triples.
            return noContent().build();
        }
    }


    private boolean isRoot(final URI internalURI) {
        return internalURI.equals(rootURI);
    }

    private Container createRoot() {
        return getContainerService().findOrCreate(rootURI);
    }

    private Response getResource(final FedoraResource resource) {
        final DefaultRdfStream stream =
            new DefaultRdfStream(createURI(toExternalURI(resource.getIdentifier(), headers).toString()),
                resource.getTriples().map(translateToExternalUris));
        return ok(stream).build();
    }

    /**
     * Creates a new object.
     *
     * This originally used application/octet-stream;qs=1001 as a workaround
     * for JERSEY-2636, to ensure requests without a Content-Type get routed here.
     * This qs value does not parse with newer versions of Jersey, as qs values
     * must be between 0 and 1.  We use qs=1.000 to mark where this historical
     * anomaly had been.
     *
     * @param contentDisposition the content Disposition value
     * @param requestContentType the request content type
     * @param slug the slug value
     * @param requestBodyStream  the request body stream
     * @param link the link value
     * @param digest the digest header
     * @return 201
     * @throws InvalidChecksumException if invalid checksum exception occurred
     * @throws IOException if IO exception occurred
     * @throws MalformedRdfException if malformed rdf exception occurred
     */
    @POST
    @Consumes({MediaType.APPLICATION_OCTET_STREAM + ";qs=1.000", WILDCARD})
    @Produces({TURTLE_WITH_CHARSET + ";qs=1.0", JSON_LD + ";qs=0.8",
            N3_WITH_CHARSET, N3_ALT2_WITH_CHARSET, RDF_XML, NTRIPLES, TEXT_PLAIN_WITH_CHARSET,
            TURTLE_X, TEXT_HTML_WITH_CHARSET, "*/*"})
    public Response createObject(@HeaderParam(CONTENT_DISPOSITION) final ContentDisposition contentDisposition,
                                 @HeaderParam(CONTENT_TYPE) final MediaType requestContentType,
                                 @HeaderParam("Slug") final String slug,
                                 @ContentLocation final InputStream requestBodyStream,
                                 @HeaderParam(LINK) final String link,
                                 @HeaderParam("Digest") final String digest)
            throws InvalidChecksumException, IOException, MalformedRdfException {
        LOGGER.info("POST: {}", externalPath);

        final ContainerService containerService = getContainerService();
        final URI resourceUri = createFromPath(externalPath);

        //check that resource exists
        if (!containerService.exists(resourceUri)) {
            if (!isRoot(resourceUri)) {
                return status(NOT_FOUND).build();
            } else {
                createRoot();
            }
        }

        final String newResourceName = slug == null ? UUID.randomUUID().toString() : slug;
        final String resourcePath = (isRoot(resourceUri) ? "" : resourceUri.getPath());
        final URI newResourceUri = createFromPath(resourcePath + "/" + newResourceName);
        final Container container = containerService.findOrCreate(newResourceUri);
        final Model model = ModelFactory.createDefaultModel();

        model.read(requestBodyStream, container.getIdentifier().toString(), "TTL");
        final Stream<Triple> triples = model.listStatements().toList().stream().map(Statement::asTriple);
        container.updateTriples(triples);
        return created(toExternalURI(container.getIdentifier(), headers)).build();
    }

    private Function<Triple, Triple> translateToExternalUris = triple ->
        new Triple(translateToExternalUri(triple.getSubject()),
                translateToExternalUri(triple.getPredicate()),
                translateToExternalUri(triple.getObject()));

    private Node translateToExternalUri(final Node node) {
        if (node.isURI()) {
            final String uri = node.getURI();
            if (uri.startsWith(INTERNAL_URI_PREFIX)) {
                return createURI(toExternalURI(URI.create(uri), headers).toString());
            }
        }
        return node;
    }

    private URI createFromPath(final String path) {
        return URI.create(INTERNAL_URI_PREFIX + (path.startsWith("/") ? path : "/" + path));
    }

    private URI toExternalURI(final URI uri, final HttpHeaders headers) {
        final String host = headers.getHeaderString("Host");
        final String protocol = headers.getHeaderString("X-Forwarded-Proto");
        final String context = headers.getHeaderString("context");
        return URI.create(protocol + "://" + host + "/" + context + uri.getPath());
    }

    private ContainerService getContainerService() {
        return this.application.containerService();
    }
}