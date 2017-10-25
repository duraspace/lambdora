package org.fcrepo.lambdora.service.aws;

import dagger.Module;
import dagger.Provides;
import org.fcrepo.lambdora.service.api.BinaryService;
import org.fcrepo.lambdora.service.api.ContainerService;
import org.fcrepo.lambdora.service.api.ServiceModule;
import org.fcrepo.lambdora.service.dao.DynamoDBResourceTripleDao;

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
        return new ContainerServiceImpl(DynamoDBResourceTripleDao.getInstance());
    }

    @Override
    @Provides
    public BinaryService providerBinaryService() {
        return new BinaryServiceImpl(DynamoDBResourceTripleDao.getInstance());
    }
}
