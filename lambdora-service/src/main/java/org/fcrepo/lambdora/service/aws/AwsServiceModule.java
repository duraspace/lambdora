package org.fcrepo.lambdora.service.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
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

    private DynamoDBResourceTripleDao resourceTripleDao;

    /**
     * Default Constructor
     */
    public AwsServiceModule() {
        this.resourceTripleDao = new DynamoDBResourceTripleDao();
    }

    /**
     * Constructor
     * @param dynamoDB
     */
    public AwsServiceModule(final AmazonDynamoDB dynamoDB) {
        this.resourceTripleDao = new DynamoDBResourceTripleDao(new DynamoDBMapper(dynamoDB));
    }

    @Override
    @Provides
    public ContainerService providerContainerService() {
        return new ContainerServiceImpl(this.resourceTripleDao);
    }

    @Override
    @Provides
    public BinaryService providerBinaryService() {
        return new BinaryServiceImpl(resourceTripleDao);
    }
}
