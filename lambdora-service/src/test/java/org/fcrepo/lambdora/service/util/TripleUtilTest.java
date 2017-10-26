package org.fcrepo.lambdora.service.util;

import org.apache.jena.graph.Triple;
import org.junit.Test;

import static org.apache.jena.graph.NodeFactory.createLiteral;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.junit.Assert.assertEquals;

/**
 * TripleUtilTest
 *
 * @author dbernstein
 */
public class TripleUtilTest {
    @Test
    public void roundTripTest(){
        Triple t1 = new Triple(createURI("http://example.com/1"), createURI("http://example.com/2"), createLiteral("test"));
        String serializedValue = TripleUtil.fromTriple(t1);
        Triple t2 = TripleUtil.toTriple(serializedValue);
        assertEquals("Triples do not match",  t1, t2);
    }


}
