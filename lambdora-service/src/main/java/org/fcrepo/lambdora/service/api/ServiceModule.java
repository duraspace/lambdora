package org.fcrepo.lambdora.service.api;

/**
 * ServiceModule is a factory for lambdora application api.  Concrete instances of
 * this module play the same role as "@Configuration" annotated classes in the
 * Spring DI framework.
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
