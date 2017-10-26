package org.fcrepo.lambdora.service.util;

import com.amazonaws.util.StringInputStream;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.riot.RDFLanguages.strLangNTriples;

/**
 * This class provides utilities for converting between n-triple strings and Jena Triples.
 *
 * @author dbernstein
 * @author awoods
 */
public class TripleUtil {

    private TripleUtil() {
        // no public constructor
    }

    /**
     * This method converts a string (in n-triples format) into a Jena triple
     *
     * @param string containing single n-triples triple
     * @return Triple
     */
    public static Triple toTriple(final String string) {
        // Create Jena model
        Model inputModel = createDefaultModel();
        try {
            // Load model with arg string (expecting n-triples)
            inputModel = inputModel.read(new StringInputStream(string), null, strLangNTriples);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Since there is only one statement, get it
        final Statement stmt = inputModel.listStatements().nextStatement();

        // Return the Jena triple which the statement represents
        return stmt.asTriple();
    }

    /**
     * This method translates a Jena Triple into its n-triples string
     *
     * @param triple to convert to a String
     * @return string
     */
    public static String fromTriple(final Triple triple) {
        // Create the Jena model
        final Model model = createDefaultModel();

        // Add the triple to the model
        model.add(model.asStatement(triple));

        // Write the model to an outputstream
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFDataMgr.write(out, model.getGraph(), Lang.NTRIPLES);

        // Return the result
        try {
            return out.toString(UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO: Log this error
            }
        }
    }

}

