package org.fcrepo.lambdora.service.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.fcrepo.lambdora.service.db.DynamoDBManager;
import org.fcrepo.lambdora.service.db.ResourceTriple;

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
     * {@inheritDoc}
     */
    @Override
    public void addResourceTriple(final ResourceTriple triple) {
        mapper.save(triple, DynamoDBMapperConfig.SaveBehavior.CLOBBER.config());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourceTriple(final String resourceName, final String rdfTriple, final String rdfSubject,
                                  final String rdfPredicate, final String rdfObject) {
        addResourceTriple(new ResourceTriple(resourceName, rdfTriple, rdfSubject, rdfPredicate, rdfObject));
    }

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
    public List<ResourceTriple> findByObject(final String rdfObject) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(rdfObject));

        final String condition = "rdf_object = :expVal1";
        return executeFind(expressionAV, OBJECT_INDEX, false, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByObjectAndPredicate(final String rdfObject,
                                                         final String rdfPredicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(rdfObject));
        expressionAV.put(":expVal2", new AttributeValue().withS(rdfPredicate));

        final String condition = "rdf_object = :expVal1 AND rdf_predicate = :expVal2";
        return executeFind(expressionAV, OBJECT_INDEX, false, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByResourceName(final String resourceName) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(resourceName));

        final  String condition = "resource_name = :expVal1";
        return executeFind(expressionAV, PREDICATE_INDEX, true, condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceTriple> findByResourceNameAndPredicate(final String resourceName,
                                                               final String rdfPredicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(resourceName));
        expressionAV.put(":expVal2", new AttributeValue().withS(rdfPredicate));

        final String condition = "resource_name = :expVal1 AND rdf_predicate = :expVal2";
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
