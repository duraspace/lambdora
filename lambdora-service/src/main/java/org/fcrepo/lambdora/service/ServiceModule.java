package org.fcrepo.lambdora.service;

import dagger.Module;
import dagger.Provides;

/**
 * ServiceModule is a factory for services
 *
 * @author dbernstein
 */
@Module
public class ServiceModule {
    /**
     * Provides instance of service
     *
     * @return the service
     */
    @Provides
    public FedoraResourceService providerFedoraResourceService() {
        return new FedoraResourceServiceImpl();
    }
}
