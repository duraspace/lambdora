package org.fcrepo.lambdora.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Defines a DynamoDB data table for a Fedora object resource
 *
 * @author bbranan
 */
@DynamoDBTable(tableName = "RESOURCE")
public class Resource {

    @DynamoDBHashKey
    private String triple;

    @DynamoDBRangeKey
    private String name;

    @DynamoDBAttribute
    private String subject;

    @DynamoDBAttribute
    private String predicate;

    @DynamoDBAttribute
    private String object;

    /**
     * Create an empty resource
     */
    public Resource() {
    }

    /**
     * Create a fully defined resource
     *
     * @param triple single RDF triple statement
     * @param name object name
     * @param subject rdf subject
     * @param predicate rdf predicate
     * @param object rdf object
     */
    public Resource(final String triple,
                    final String name,
                    final String subject,
                    final String predicate,
                    final String object) {
        this.triple = triple;
        this.name = name;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     * Get triple
     *
     * @return triple
     */
    public String getTriple() {
        return triple;
    }

    /**
     * Set new triple value
     *
     * @param triple
     */
    public void setTriple(final String triple) {
        this.triple = triple;
    }

    /**
     * Get object name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set new object name
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get rdf subject
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set rdf subject value
     *
     * @param subject
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Get rdf predicate
     *
     * @return predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * Set rdf predicate value
     *
     * @param predicate
     */
    public void setPredicate(final String predicate) {
        this.predicate = predicate;
    }

    /**
     * Get rdf object
     *
     * @return object
     */
    public String getObject() {
        return object;
    }

    /**
     * Set rdf object value
     *
     * @param object
     */
    public void setObject(final String object) {
        this.object = object;
    }

}
