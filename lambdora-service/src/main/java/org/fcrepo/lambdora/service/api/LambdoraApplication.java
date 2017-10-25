package org.fcrepo.lambdora.service.api;

/**
 * LambdoraServiceApplication
 *
 * @author dbernstein
 */
public interface LambdoraApplication {
    /**
     * get handle to a ContainerService
     *
     * @return
     */
    ContainerService containerService();

    /**
     * get handle to a BinaryService
     *
     * @return
     */
    BinaryService binaryService();

}
