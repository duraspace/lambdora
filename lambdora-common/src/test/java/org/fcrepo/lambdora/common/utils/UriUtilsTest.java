package org.fcrepo.lambdora.common.utils;

import org.junit.Test;

import java.net.URI;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.fcrepo.lambdora.common.rdf.RdfLexicon.INTERNAL_URI_PREFIX;

/**
 * UriUtils
 *
 * @author dbernstein
 */
public class UriUtilsTest {

    @Test
    public void test() {
        final URI root1 = URI.create(INTERNAL_URI_PREFIX + "/");
        assertNull("parent should be null", UriUtils.getParent(root1));
    }

    @Test
    public void testChildOfRoot() {
        final URI uri = URI.create(INTERNAL_URI_PREFIX + "/test");
        assertEquals("incorrect parent", URI.create(INTERNAL_URI_PREFIX + "/"), UriUtils.getParent(uri));
    }

    @Test
    public void testGrandchildOfRoot() {
        final URI uri = URI.create(INTERNAL_URI_PREFIX + "/child/grandchild");
        assertEquals("incorrect parent", URI.create(INTERNAL_URI_PREFIX + "/child"), UriUtils.getParent(uri));
    }

}
