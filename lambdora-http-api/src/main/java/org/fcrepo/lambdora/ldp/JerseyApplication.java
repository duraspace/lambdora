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
        /**
         * TODO - Should these be discrete pacakges, or just bring in everything under org.fcrepo?
         */
        packages("org.fcrepo.lambdora.ldp", "org.fcrepo.http.api.responses", "org.fcrepo.http.commons.responses");
    }
}
