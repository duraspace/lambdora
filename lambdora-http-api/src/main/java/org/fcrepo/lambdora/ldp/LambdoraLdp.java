package org.fcrepo.lambdora.ldp;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.http.commons.domain.ContentLocation;
import org.fcrepo.kernel.api.exception.InvalidChecksumException;
import org.fcrepo.kernel.api.exception.MalformedRdfException;
import org.fcrepo.kernel.api.rdf.DefaultRdfStream;
import org.fcrepo.lambdora.service.api.Container;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.aws.DaggerAwsLambdoraApplication;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LINK;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.ok;
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
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Main entry points for Jersey request handling
 *
 * @author gtriggs
 */
@Path("/{path: .*}")
public class LambdoraLdp {

    private static final Logger LOGGER = getLogger(LambdoraLdp.class);

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

    private LambdoraApplication application;

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
        this.application = application;
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

        final ContainerService containerService = this.application.containerService();

        final URI internalURI = createFromPath(externalPath);

        final Container container = containerService.find(internalURI);
        if (container == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final DefaultRdfStream stream =
                new DefaultRdfStream(createURI(container.getIdentifier().toString()), container.getTriples());
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

        final ContainerService containerService = this.application.containerService();
        final URI objectURI = createFromPath(externalPath + slug);
        final Container container = containerService.findOrCreate(objectURI);

        final Model model = ModelFactory.createDefaultModel();
        model.read(requestBodyStream, null, "TTL");

        final Stream<Triple> triples = model.listStatements().toList().stream().map(new Function<Statement, Triple>() {
            @Override
            public Triple apply(final Statement statement) {
                return statement.asTriple();
            }
        });

        container.updateTriples(triples);

        return created(toExternalURI(container.getIdentifier())).build();
    }

    private URI createFromPath(final String path) {
        //@TODO this internal prefix should be changed
        //      to something like "fedora:info/"
        //      At the moment,  it is not clear how to use URI.getPath()
        //      if the prefix is fedora:info/
        return URI.create("fedora://info/" + path);
    }

    private URI toExternalURI(final URI uri) {
        //TODO ensure the correctly translated URI is returned.
        return URI.create(uriInfo.getBaseUri().toString() + uri.getPath());
    }
}