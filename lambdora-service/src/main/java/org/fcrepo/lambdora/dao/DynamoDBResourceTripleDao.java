package org.fcrepo.lambdora.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.fcrepo.lambdora.db.DynamoDBManager;
import org.fcrepo.lambdora.db.ResourceTriple;

/**
 * Allows interaction with ResourceTriples stored in DynamoDB
 *
 * @author bbranan
 * @author tdonohue
 */
public class DynamoDBResourceTripleDao implements ResourceTripleDao {

    private static final String OBJECT_INDEX = "objectIndex";
    private static final String PREDICATE_INDEX = "predicateIndex";

    private static volatile DynamoDBResourceTripleDao instance;
    private static final DynamoDBMapper mapper = DynamoDBManager.getMapper();

    /**
     * Retrieve and instance of this DAO
     *
     * @return DynamoDBResourceTripleDao
     */
    public static DynamoDBResourceTripleDao getInstance() {
        if (instance == null) {
            synchronized (DynamoDBResourceTripleDao.class) {
                if (instance == null) {
                    instance = new DynamoDBResourceTripleDao();
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByObject(final String object) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(object));

        final String condition = "object = :expVal1";
        return executeFind(expressionAV, OBJECT_INDEX, false, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByObjectAndPredicate(final String object,
                                                         final String predicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(object));
        expressionAV.put(":expVal2", new AttributeValue().withS(predicate));

        final String condition = "object = :expVal1 AND predicate = :expVal2";
        return executeFind(expressionAV, OBJECT_INDEX, false, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByName(final String name) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(name));

        final  String condition = "name = :expVal1";
        return executeFind(expressionAV, PREDICATE_INDEX, true, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByNameAndPredicate(final String name,
                                                       final String predicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(name));
        expressionAV.put(":expVal2", new AttributeValue().withS(predicate));

        final String condition = "name = :expVal1 AND predicate = :expVal2";
        return executeFind(expressionAV, PREDICATE_INDEX, true, condition);
    }

    /*
     * Perform query against DynamoDB
     */
    private List<ResourceTriple> executeFind(final Map<String, AttributeValue> expressionAV,
                                             final String indexName,
                                             final boolean consistentRead,
                                             final String conditionExpression) {
        final DynamoDBQueryExpression<ResourceTriple> query =
            new DynamoDBQueryExpression<ResourceTriple>()
                .withIndexName(indexName)
                .withConsistentRead(consistentRead)
                .withKeyConditionExpression(conditionExpression)
                .withExpressionAttributeValues(expressionAV);

        return mapper.query(ResourceTriple.class, query);
    }

}
