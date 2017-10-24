package org.fcrepo.lambdora.service;

import dagger.Component;

/**
 * LambdoraServiceApplication
 *
 * @author dbernstein
 */
@Component(modules = ServiceModule.class)
public interface LambdoraServiceApplication {
    /**
     * get handle to a FedoraResourceService
     * @return
     */
    FedoraResourceService fedoraResourceService();
}
