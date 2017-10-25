package org.fcrepo.lambdora.service.api;

import org.fcrepo.lambdora.service.dao.ResourceTripleDao;
import org.fcrepo.lambdora.service.db.ResourceTriple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * FedoraResourceImplTest
 *
 * @author dbernstein
 */
@RunWith(MockitoJUnitRunner.class)
public class FedoraResourceImplTest {

    @Mock
    private ResourceTripleDao mockDao;

    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new URI("fedora:info/test");

        when(mockDao.findByResourceName(uri.toString())).thenReturn(Arrays.asList(new ResourceTriple(uri.toString(),
            "triple", uri.toString(), "test", "test")));
    }

    @After
    public void tearDown() {

    }

    /**
     * An simple smoke test.
     */
    @Test
    public void test() throws Exception {
        final FedoraResourceImpl resource = new FedoraResourceImpl(uri, mockDao) {
        };
        assertNotNull(resource.getIdentifier());
        assertNotNull(resource.getTriples());

    }
}
