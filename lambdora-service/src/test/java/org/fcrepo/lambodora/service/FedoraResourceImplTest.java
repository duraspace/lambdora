package org.fcrepo.lambodora.service;

import org.fcrepo.lambdora.service.FedoraResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * org.fcrepo.lambodora.service.FedoraResourceImplTest
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
    public void testConstructor() {
        final FedoraResourceImpl resource = new FedoraResourceImpl();
        assertNotNull(resource);
    }
}
