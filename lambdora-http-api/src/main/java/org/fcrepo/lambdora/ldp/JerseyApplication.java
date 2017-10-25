package org.fcrepo.lambdora.ldp;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class provides configuration to Jersey
 *
 * @author gtriggs
 */
public class JerseyApplication extends ResourceConfig {
    /**
     * Tell Jersey what packages to scan for annotations
     */
    public JerseyApplication() {
        packages("org.fcrepo.lambdora.ldp");
    }
}
