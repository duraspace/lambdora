package org.fcrepo.lambdora.service.api;

/**
 * ServiceModule is a factory for fedora application api.
 *
 * @author dbernstein
 */
public interface ServiceModule {
    /**
     * Provides instance of ContainerService
     *
     * @return the service
     */
    public ContainerService providerContainerService();

    /**
     * Provides instance of BinaryService
     *
     * @return the service
     */
    public BinaryService providerBinaryService();

}
