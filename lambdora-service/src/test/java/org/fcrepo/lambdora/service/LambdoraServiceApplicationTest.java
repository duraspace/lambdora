package org.fcrepo.lambdora.service;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * LambdoraServiceApplicationTest
 *
 * @author dbernstein
 */
public class LambdoraServiceApplicationTest {

    @Test
    public void test() {
        final LambdoraServiceApplication lsa = DaggerLambdoraServiceApplication.builder()
                                                                               .build();
        final FedoraResourceService service = lsa.fedoraResourceService();
        assertNotNull(service);
    }
}
