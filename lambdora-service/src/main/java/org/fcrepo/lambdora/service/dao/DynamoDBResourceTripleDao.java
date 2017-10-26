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

    private DynamoDBMapper mapper;

    /**
     * Creates a ResourceTriple data access object for communication with DynamoDB
     */
    public DynamoDBResourceTripleDao() {
        this(DynamoDBManager.getMapper());
    }

    /**
     * Creates a ResourceTriple data access object for communication with DynamoDB
     * using the supplied mapper
     *
     * @param mapper DynamoDBMapper
     */
    public DynamoDBResourceTripleDao(final DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

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
     * Get List of triples based on the RDF object uri. The List returned is a
     * {@code com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList}, which loads List items on demand
     * (as List is iterated through). Be aware that List methods requiring full iteration (e.g. size()) will
     * load all list items immediately into memory.
     * <P>
     * This is an eventually consistent read request.
     *
     * @param rdfObject uri
     * @see com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
     */
    @Override
    public List<ResourceTriple> findByObject(final String rdfObject) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(rdfObject));

        final String condition = "rdf_object = :expVal1";
        return executeFindInIndex(condition, expressionAV, OBJECT_INDEX, false);
    }

    /**
     * Get List of triples based on the RDF object uri and predicate. The List returned is a
     * {@code com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList}, which loads List items on demand
     * (as List is iterated through). Be aware that List methods requiring full iteration (e.g. size()) will
     * load all list items immediately into memory.
     * <P>
     * This is an eventually consistent read request.
     *
     * @param rdfObject uri
     * @param rdfPredicate predicate
     * @see com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
     */
    @Override
    public List<ResourceTriple> findByObjectAndPredicate(final String rdfObject,
                                                         final String rdfPredicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(rdfObject));
        expressionAV.put(":expVal2", new AttributeValue().withS(rdfPredicate));

        final String condition = "rdf_object = :expVal1 AND rdf_predicate = :expVal2";
        return executeFindInIndex(condition, expressionAV, OBJECT_INDEX, false);
    }

    /**
     * Get List of triples based on the resource name. The List returned is a
     * {@code com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList}, which loads List items on demand
     * (as List is iterated through). Be aware that List methods requiring full iteration (e.g. size()) will
     * load all list items immediately into memory.
     * <P>
     * This is a strongly consistent read request.
     *
     * @param resourceName resource name
     * @see com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
     */
    @Override
    public List<ResourceTriple> findByResourceName(final String resourceName) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(resourceName));

        final  String condition = "resource_name = :expVal1";
        return executeFindInIndex(condition, expressionAV, PREDICATE_INDEX, true);
    }

    /**
     * Get List of triples based on the resource name and RDF predicate. The List returned is a
     * {@code com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList}, which loads List items on demand
     * (as List is iterated through). Be aware that List methods requiring full iteration (e.g. size()) will
     * load all list items immediately into memory.
     * <P>
     * This is a strongly consistent read request.
     *
     * @param resourceName resource name
     * @param rdfPredicate predicate
     * @see com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
     */
    @Override
    public List<ResourceTriple> findByResourceNameAndPredicate(final String resourceName,
                                                               final String rdfPredicate) {
        final Map<String, AttributeValue> expressionAV = new HashMap<>();
        expressionAV.put(":expVal1", new AttributeValue().withS(resourceName));
        expressionAV.put(":expVal2", new AttributeValue().withS(rdfPredicate));

        final String condition = "resource_name = :expVal1 AND rdf_predicate = :expVal2";
        return executeFindInIndex(condition, expressionAV, PREDICATE_INDEX, true);
    }

    /**
     * Perform a query against DynamoDB. The List returned is a
     * {@code com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList}, which loads List items on demand
     * (as List is iterated through). Be aware that List methods requiring full iteration (e.g. size()) will
     * load all list items immediately into memory.
     *
     * @param conditionExpression Key condition expression defining the query
     * @param expressionAV Map of DynamoDB Expression Attribute Values (used to populate conditionExpression)
     * @param indexName Index to run the query against
     * @param consistentRead Whether to require consistent read (only available for LocalSecondaryIndexes)
     * @return PaginatedQueryList of ResourceTriples
     * @see com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
     */
    private List<ResourceTriple> executeFindInIndex(final String conditionExpression,
                                                    final Map<String, AttributeValue> expressionAV,
                                                    final String indexName,
                                                    final boolean consistentRead) {
        final DynamoDBQueryExpression<ResourceTriple> query =
            new DynamoDBQueryExpression<ResourceTriple>()
                .withIndexName(indexName)
                .withConsistentRead(consistentRead)
                .withKeyConditionExpression(conditionExpression)
                .withExpressionAttributeValues(expressionAV);

        return mapper.query(ResourceTriple.class, query);
    }

}
