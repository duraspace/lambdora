package org.fcrepo.lambdora.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Defines a DynamoDB data table for a Fedora resource triples
 *
 * @author bbranan
 * @author tdonohue
 */
@DynamoDBTable(tableName = "RESOURCE_TRIPLE")
public class ResourceTriple {

    private String resourceName;
    private String rdfTriple;
    private String rdfSubject;
    private String rdfPredicate;
    private String rdfObject;

    /**
     * Create an empty resource
     */
    public ResourceTriple() {
    }

    /**
     * Create a fully defined resource
     *
     * @param resourceName resource name
     * @param rdfTriple single RDF triple statement
     * @param rdfSubject rdf subject
     * @param rdfPredicate rdf predicate
     * @param rdfObject rdf object
     */
    public ResourceTriple(final String resourceName,
                          final String rdfTriple,
                          final String rdfSubject,
                          final String rdfPredicate,
                          final String rdfObject) {
        this.resourceName = resourceName;
        this.rdfTriple = rdfTriple;
        this.rdfSubject = rdfSubject;
        this.rdfPredicate = rdfPredicate;
        this.rdfObject = rdfObject;
    }

    /**
     * Get resourceName
     *
     * @return resourceName
     */
    @DynamoDBHashKey(attributeName = "resource_name")
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set new resourceName
     *
     * @param resourceName
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get rdfTriple
     *
     * @return rdfTriple
     */
    @DynamoDBRangeKey(attributeName = "rdf_triple")
    public String getRdfTriple() {
        return rdfTriple;
    }

    /**
     * Set new rdfTriple value
     *
     * @param rdfTriple
     */
    public void setRdfTriple(final String rdfTriple) {
        this.rdfTriple = rdfTriple;
    }

    /**
     * Get rdfSubject
     *
     * @return rdfSubject
     */
    @DynamoDBAttribute(attributeName = "rdf_subject")
    public String getRdfSubject() {
        return rdfSubject;
    }

    /**
     * Set rdfSubject value
     *
     * @param rdfSubject
     */
    public void setRdfSubject(final String rdfSubject) {
        this.rdfSubject = rdfSubject;
    }

    /**
     * Get rdfPredicate
     *
     * @return rdfPredicate
     */
    @DynamoDBAttribute(attributeName = "rdf_predicate")
    public String getRdfPredicate() {
        return rdfPredicate;
    }

    /**
     * Set rdfPredicate value
     *
     * @param rdfPredicate
     */
    public void setRdfPredicate(final String rdfPredicate) {
        this.rdfPredicate = rdfPredicate;
    }

    /**
     * Get rdfObject
     *
     * @return rdfObject
     */
    @DynamoDBAttribute(attributeName = "rdf_object")
    public String getRdfObject() {
        return rdfObject;
    }

    /**
     * Set rdfObject value
     *
     * @param rdfObject
     */
    public void setRdfObject(final String rdfObject) {
        this.rdfObject = rdfObject;
    }

    @Override
    public String toString() {
        return "ResourceTriple{" +
            "resourceName='" + resourceName + '\'' +
            ", rdfTriple='" + rdfTriple + '\'' +
            ", rdfSubject='" + rdfSubject + '\'' +
            ", rdfPredicate='" + rdfPredicate + '\'' +
            ", rdfObject='" + rdfObject + '\'' +
            '}';
    }
}
