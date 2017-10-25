package org.fcrepo.lambdora.service.aws;

import org.fcrepo.lambdora.service.api.BinaryService;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * AwsLambdoraApplicationTest
 *
 * @author dbernstein
 */
public class AwsLambdoraApplicationTest {

    /**
     * A simple smoke test.
     */
    @Test
    public void test() {
        final LambdoraApplication lsa = DaggerAwsLambdoraApplication.builder()
            .build();
        final ContainerService containerService = lsa.containerService();
        assertNotNull(containerService);
        final BinaryService binaryService = lsa.binaryService();
        assertNotNull(binaryService);
    }
}
