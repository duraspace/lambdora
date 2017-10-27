package org.fcrepo.lambdora.ldp;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.fcrepo.http.commons.domain.ContentLocation;
import org.fcrepo.http.commons.responses.RdfNamespacedStream;
import org.fcrepo.kernel.api.RdfStream;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
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
        super();
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

        /**
         * Copied and cut down from FedoraLdp
         *
         * - Removed all the handling of binary
         * - Commented out the call to add resource headers
         * (copying that in expands out the hierarchy of Fedora classes quickly)
         */

        LOGGER.info("GET resource '{}'", externalPath);

        final ContainerService containerService = this.application.containerService();

        final URI internalURI = createFromPath(externalPath);

        final Container container = containerService.find(internalURI);
        if (container == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        //
        //        try (final RdfStream rdfStream = new DefaultRdfStream(testNode())) {
        //            addResourceHttpHeaders(resource());
        //            return getContent(rangeValue, getChildrenLimit(), rdfStream);
        //        }

        try (DefaultRdfStream stream = new DefaultRdfStream(createURI(container.getIdentifier().toString()),
                                                            container.getTriples())) {
            return ok(stream).build();
        }
    }

    /**
     * Creates a new object.
     * <p>
     * This originally used application/octet-stream;qs=1001 as a workaround
     * for JERSEY-2636, to ensure requests without a Content-Type get routed here.
     * This qs value does not parse with newer versions of Jersey, as qs values
     * must be between 0 and 1.  We use qs=1.000 to mark where this historical
     * anomaly had been.
     *
     * @param contentDisposition the content Disposition value
     * @param requestContentType the request content type
     * @param slug               the slug value
     * @param requestBodyStream  the request body stream
     * @param link               the link value
     * @param digest             the digest header
     * @return 201
     * @throws InvalidChecksumException if invalid checksum exception occurred
     * @throws IOException              if IO exception occurred
     * @throws MalformedRdfException    if malformed rdf exception occurred
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
        return URI.create("fedora://info/" + path);
    }

    private URI toExternalURI(final URI uri) {
        return URI.create("http://localhost:8080/rest" + uri.getPath());
    }


    /**
     * This method returns an HTTP response with content body appropriate to the following arguments.
     *
     * @param rangeValue starting and ending byte offsets
     * @param limit      is the number of child resources returned in the response, -1 for all
     * @param rdfStream  to which response RDF will be concatenated
     * @return HTTP response
     * @throws IOException in case of error extracting content
     */
    protected Response getContent(final String rangeValue,
                                  final int limit,
                                  final RdfStream rdfStream) throws IOException {

        /**
         * Copy(-ish) of FedoraLdp.getContent()
         *
         * - getNamespaces() is a stub method (normally, this is rerieved from the FedoraSession()
         * (Needs to be implemented based on however we are going to deal with namespaces)
         *
         * Note the concat of the passed rdfStream (which is just a Node of the "topic"), and a stream
         * generated from the testModel().
         *
         * Without the concat, the test environment just rails with an IllegalStateException
         */
        final RdfNamespacedStream outputStream;

        outputStream = new RdfNamespacedStream(
            new DefaultRdfStream(
                rdfStream.topic(),
                concat(rdfStream, DefaultRdfStream.fromModel(testNode(), testModel()))
            ), getNamespaces());

        ;

//        if (prefer != null) {
//            prefer.getReturn().addResponseHeaders(servletResponse);
//        }
        servletResponse.addHeader("Vary", "Accept, Range, Accept-Encoding, Accept-Language");

        return ok(outputStream).build();
    }

    /**  ********************************
     *
     * Stub Methods and data being used for initial development
     */

    /**
     * Stub method for testing - return a map of the namespace / uris that you want prefixes for in the RDF
     *
     * @return
     */
    protected Map<String, String> getNamespaces() {
        final Map<String, String> namespaces = new HashMap<>();
        namespaces.put("premis", "http://www.loc.gov/premis/rdf/v1#");
        namespaces.put("test", "info:fedora/test/");
        namespaces.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        namespaces.put("fedora-model", "info:fedora/fedora-system:def/model#");
        namespaces.put("ns003", "http://ndl.go.jp/dcndl/terms/");
        namespaces.put("ns002", "http://www.loc.gov/standards/mods/modsrdf-primer.html#");
        namespaces.put("ns001", "http://schema.org/");
        namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaces.put("xmlns", "http://www.w3.org/2000/xmlns/");
        namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        namespaces.put("fedora", "http://fedora.info/definitions/v4/repository#");
        namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        namespaces.put("ebucore", "http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#");
        namespaces.put("ldp", "http://www.w3.org/ns/ldp#");
        namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
        namespaces.put("fedoraconfig", "http://fedora.info/definitions/v4/config#");
        namespaces.put("foaf", "http://xmlns.com/foaf/0.1/");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        return namespaces;
    }

    /**
     * Stub method for testing
     *
     * @return
     */
    private int getChildrenLimit() {
        return 20;
    }

    /**
     * Stub method for testing - returns a resource URI for what is being requested.
     * Hard coded to pretend that we are returning the top level resource for now.
     *
     * @return
     */
    private Node testNode() {
        return createURI("http://demo.fcrepo.org:8080/fcrepo/rest/");
    }

    /**
     * Stub method for testing - return a Jena Model to create a stream from
     * In production the stream will have been generated from the service layer
     *
     * @return
     */
    private Model testModel() {
        final Model model = ModelFactory.createDefaultModel();
        try (final InputStream in = new ByteArrayInputStream(rootTTL.getBytes("UTF-8"))) {
            model.read(in, null, "TURTLE");
        } catch (IOException e) {

        }

        return model;
    }

    /**
     * Sample TURTLE string to provide an RDF model pertaining to a Fedora Resource
     * <p>
     * - this was created by curling the Fedora demo server for the root resource
     */

    private final static String rootTTL = "@prefix premis:  <http://www.loc.gov/premis/rdf/v1#> .\n" +
"@prefix test:  <info:fedora/test/> .\n" +
"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
"@prefix fedora-model:  <info:fedora/fedora-system:def/model#> .\n" +
"@prefix ns003:  <http://ndl.go.jp/dcndl/terms/> .\n" +
"@prefix ns002:  <http://www.loc.gov/standards/mods/modsrdf-primer.html#> .\n" +
"@prefix ns001:  <http://schema.org/> .\n" +
"@prefix xsi:  <http://www.w3.org/2001/XMLSchema-instance> .\n" +
"@prefix xmlns:  <http://www.w3.org/2000/xmlns/> .\n" +
"@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
"@prefix fedora:  <http://fedora.info/definitions/v4/repository#> .\n" +
"@prefix xml:  <http://www.w3.org/XML/1998/namespace> .\n" +
"@prefix ebucore:  <http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#> .\n" +
"@prefix ldp:  <http://www.w3.org/ns/ldp#> .\n" +
"@prefix xs:  <http://www.w3.org/2001/XMLSchema> .\n" +
"@prefix fedoraconfig:  <http://fedora.info/definitions/v4/config#> .\n" +
"@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\n" +
"@prefix dc:  <http://purl.org/dc/elements/1.1/> .\n" +
"\n" +
"<http://demo.fcrepo.org:8080/fcrepo/rest/>\n" +
"    fedora:lastModified            \"2017-10-25T16:15:21.355Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;\n" +
"    rdf:type                       ldp:RDFSource ;\n" +
"    rdf:type                       ldp:Container ;\n" +
"    rdf:type                       ldp:BasicContainer ;\n" +
"    fedora:writable                true ;\n" +
"    rdf:type                       fedora:RepositoryRoot ;\n" +
"    rdf:type                       fedora:Resource ;\n" +
"    rdf:type                       fedora:Container ;\n" +
"    ldp:contains                   <http://demo.fcrepo.org:8080/fcrepo/rest/test> ;\n" +
"    ldp:contains                   <http://demo.fcrepo.org:8080/fcrepo/rest/test2> ;\n" +
"    fedora:hasTransactionProvider  <http://demo.fcrepo.org:8080/fcrepo/rest/fcr:tx> .\n";
}