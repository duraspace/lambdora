package org.fcrepo.lambdora.ldp;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.Response.ok;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.JSON_LD;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.N3_WITH_CHARSET;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.N3_ALT2_WITH_CHARSET;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.NTRIPLES;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.RDF_XML;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.TEXT_HTML_WITH_CHARSET;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.TEXT_PLAIN_WITH_CHARSET;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.TURTLE_WITH_CHARSET;
import static org.fcrepo.lambdora.ldp.domain.RDFMediaType.TURTLE_X;

/**
 * LambdoraLdp class
 *
 * @author gtriggs
 */
@Path("/{path: .*}")
public class LambdoraLdp {
    @PathParam("path")
    protected String externalPath;

    /**
     * For getting user agent
     */
    @Context
    protected HttpHeaders headers;

    /**
     * Default JAX-RS entry point
     */
    public LambdoraLdp() {
        super();
    }

    /**
     * Retrieve the node profile
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
        return ok("GET: Welcome to Lambdora, the current time is " + new Date() +
                ". path=" + externalPath).build();
    }

    private int getChildrenLimit() {
        final List<String> acceptHeaders = headers.getRequestHeader(ACCEPT);
        if (acceptHeaders != null && acceptHeaders.size() > 0) {
            final List<String> accept = Arrays.asList(acceptHeaders.get(0).split(","));
            if (accept.contains(TEXT_HTML)) {
                // Magic number '100' is tied to common-metadata.vsl display of ellipses
                return 100;
            }
        }

        final List<String> limits = headers.getRequestHeader("Limit");
        if (null != limits && limits.size() > 0) {
            try {
                return Integer.parseInt(limits.get(0));

            } catch (final NumberFormatException e) {
                throw new ClientErrorException("Invalid 'Limit' header value: " + limits.get(0), SC_BAD_REQUEST, e);
            }
        }
        return -1;
    }
}