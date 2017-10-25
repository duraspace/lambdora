package org.fcrepo.lambdora.service.aws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * FedoraResourceImplTest
 *
 * @author dbernstein
 */
public class FedoraResourceImplTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void test() {
        final String path = "path";
        final FedoraResourceImpl resource = new FedoraResourceImpl(path){};
        assertNotNull(resource.getPath());
        assertNotNull(resource.getTriples());

    }
}
