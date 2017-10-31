package org.fcrepo.lambdora.common.test;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

/**
 * DynamoDbTestBase - provides a base for testing classes which need to interact with
 *                    DynamoDB. Uses an embedded mock DynamoDB for this purpose.
 *
 * @author dbernstein
 * @author bbranan
 */
public abstract class IntegrationTestBase {

    private static final String TABLE_NAME = "RESOURCE_TRIPLE";
    private static final String RESOURCE_NAME_ATT = "resource_name";
    private static final String RDF_TRIPLE_ATT = "rdf_triple";
    private static final String RDF_SUBJECT_ATT = "rdf_subject";
    private static final String RDF_PREDICATE_ATT = "rdf_predicate";
    private static final String RDF_OBJECT_ATT = "rdf_object";
    private static final String PREDICATE_INDEX_NAME = "predicateIndex";
    private static final String OBJECT_INDEX_NAME = "objectIndex";

    protected AmazonDynamoDB dynamodbClient = null;

    /**
     * setup
     */
    @Before
    public void setup() {
        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        dynamodbClient = DynamoDBEmbedded.create().amazonDynamoDB();

        // Create and verify table
        final CreateTableResult createTableResult = createTable();
        assertEquals(TABLE_NAME, createTableResult.getTableDescription().getTableName());
    }

    /*
     * Builds the DynamoDB table as defined in serverless.yml
     */
    private CreateTableResult createTable() {
        final List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition(RESOURCE_NAME_ATT, ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition(RDF_TRIPLE_ATT, ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition(RDF_PREDICATE_ATT, ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition(RDF_OBJECT_ATT, ScalarAttributeType.S));

        final List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement(RESOURCE_NAME_ATT, KeyType.HASH));
        keySchema.add(new KeySchemaElement(RDF_TRIPLE_ATT, KeyType.RANGE));

        final ProvisionedThroughput provisionedthroughput =
            new ProvisionedThroughput(10L, 10L);

        final LocalSecondaryIndex predicateIndex = new LocalSecondaryIndex()
            .withIndexName(PREDICATE_INDEX_NAME)
            .withKeySchema(new KeySchemaElement(RESOURCE_NAME_ATT, KeyType.HASH))
            .withKeySchema(new KeySchemaElement(RDF_PREDICATE_ATT, KeyType.RANGE))
            .withProjection(new Projection().withNonKeyAttributes(RDF_SUBJECT_ATT, RDF_OBJECT_ATT)
                                            .withProjectionType(ProjectionType.INCLUDE));

        final GlobalSecondaryIndex objectIndex = new GlobalSecondaryIndex()
            .withIndexName(OBJECT_INDEX_NAME)
            .withKeySchema(new KeySchemaElement(RDF_OBJECT_ATT, KeyType.HASH))
            .withKeySchema(new KeySchemaElement(RDF_PREDICATE_ATT, KeyType.RANGE))
            .withProjection(new Projection().withNonKeyAttributes(RDF_SUBJECT_ATT)
                                            .withProjectionType(ProjectionType.INCLUDE))
            .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        final CreateTableRequest request =
            new CreateTableRequest()
                .withTableName(TABLE_NAME)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchema)
                .withProvisionedThroughput(provisionedthroughput)
                .withLocalSecondaryIndexes(predicateIndex)
                .withGlobalSecondaryIndexes(objectIndex);

        return dynamodbClient.createTable(request);
    }

    /**
     * teardown
     */
    @After
    public void tearDown() {
        // Shutdown the thread pools in DynamoDB Local / Embedded
        if (dynamodbClient != null) {
            dynamodbClient.shutdown();
        }
    }

}
