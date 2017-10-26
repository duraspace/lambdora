package org.fcrepo.lambdora.service.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * DynamoDbTest - exemplifies how to bring up and down an embedded dynamodb instance.
 *
 * @author dbernstein
 */
public class DynamoDbTest {

    private AmazonDynamoDB dynamodb = null;

    @Before
    public void setup() {
        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        dynamodb = DynamoDBEmbedded.create().amazonDynamoDB();
    }

    @After
    public void tearDown() {
        // Shutdown the thread pools in DynamoDB Local / Embedded
        if (dynamodb != null) {
            dynamodb.shutdown();
        }
    }

    @Test
    public void test() throws Exception {
        listTables(dynamodb.listTables(), "DynamoDB Embedded");
    }

    private void listTables(final ListTablesResult result, final String method) {
        System.out.println("found " + Integer.toString(result.getTableNames().size()) + " tables with " + method);
        for (String table : result.getTableNames()) {
            System.out.println(table);
        }
    }
}
