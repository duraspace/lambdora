package org.fcrepo.lambdora.ldp;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * JerseyApplication class
 *
 * @author gtriggs
 */
public class JerseyApplication extends ResourceConfig {
    /**
     *
     */
    public JerseyApplication() {
        packages("org.fcrepo.lambdora");
    }
}
