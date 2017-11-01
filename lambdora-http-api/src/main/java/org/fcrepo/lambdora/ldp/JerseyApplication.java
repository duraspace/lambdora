package org.fcrepo.lambdora.ldp;

import org.fcrepo.lambdora.service.api.LambdoraApplication;
import org.fcrepo.lambdora.service.aws.DaggerAwsLambdoraApplication;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class provides configuration to Jersey
 *
 * @author gtriggs
 */
public class JerseyApplication extends ResourceConfig {
    /**
     * Default constructor
     */
    public JerseyApplication() {
        this(DaggerAwsLambdoraApplication.builder().build());
    }

    /**
     * For testing purposes
     * @param lambdoraApplication
     */
    public JerseyApplication(final LambdoraApplication lambdoraApplication) {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(lambdoraApplication).to(LambdoraApplication.class);
            }
        });
        packages("org.fcrepo");
    }
}
