package org.fcrepo.lambdora.service.api;

/**
 * This interfaces defines the root entry point for accessing
 * Lambdora application resources (from the dependency-injection
 * framework).
 *
 * @author dbernstein
 */
public interface LambdoraApplication {
    /**
     * find handle to a ContainerService
     *
     * @return
     */
    ContainerService containerService();

    /**
     * find handle to a BinaryService
     *
     * @return
     */
    BinaryService binaryService();

}
