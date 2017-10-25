package org.fcrepo.lambdora.service.aws;

import dagger.Module;
import dagger.Provides;
import org.fcrepo.lambdora.service.api.BinaryService;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.ServiceModule;

/**
 * A concrete AWS-based implementation of the ServiceModule interface.
 *
 * @author dbernstein
 */
@Module
public class AwsServiceModule implements ServiceModule {
    @Override
    @Provides
    public ContainerService providerContainerService() {
        return new ContainerServiceImpl();
    }

    @Override
    @Provides
    public BinaryService providerBinaryService() {
        return new BinaryServiceImpl();
    }
}
