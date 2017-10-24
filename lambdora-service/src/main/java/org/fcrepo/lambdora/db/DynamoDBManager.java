package org.fcrepo.lambdora.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * Connects to DynamoDB
 *
 * Initial code borrowed from: https://github.com/awslabs/lambda-java8-dynamodb
 *
 * @author bbranan
 */
public class DynamoDBManager {

    private static volatile DynamoDBManager instance;

    private static DynamoDBMapper mapper;

    private DynamoDBManager() {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        mapper = new DynamoDBMapper(client);
    }

    private static DynamoDBManager getInstance() {
        if (instance == null) {
            synchronized (DynamoDBManager.class) {
                if (instance == null) {
                    instance = new DynamoDBManager();
                }
            }
        }

        return instance;
    }

    /**
     * Provides a mapper for interaction with DynamoDB
     * @return DynamoDB mapper
     */
    public static DynamoDBMapper getMapper() {
        final DynamoDBManager manager = getInstance();
        return manager.mapper;
    }

}
