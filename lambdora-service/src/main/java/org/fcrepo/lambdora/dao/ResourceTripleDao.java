package org.fcrepo.lambdora.dao;

import java.util.List;

import org.fcrepo.lambdora.db.ResourceTriple;

/**
 * Data access object for ResourceTriples
 *
 * @author bbranan
 * @author tdonohue
 */
public interface ResourceTripleDao {

    /**
     * Add a new ResourceTriple to the database
     * @param triple ResourceTriple
     */
    public void addResourceTriple(ResourceTriple triple);

    /**
     * Add a new ResourceTriple to the database
     * @param resourceName Resource name
     * @param rdfTriple full triple
     * @param rdfSubject subject of triple
     * @param rdfPredicate predicate of triple
     * @param rdfObject object of triple
     */
    public void addResourceTriple(String resourceName, String rdfTriple, String rdfSubject, String rdfPredicate,
                                  String rdfObject);

    /**
     * Get resource based on the RDF object uri
     *
     * This is an eventually consistent read request
     *
     * @param rdfObject uri
     */
    public List<ResourceTriple> findByObject(String rdfObject);

    /**
     * Get resource based on the RDF object uri and predicate
     *
     * @param rdfObject uri
     * @param rdfPredicate predicate
     */
    public List<ResourceTriple> findByObjectAndPredicate(String rdfObject, String rdfPredicate);

    /**
     * Get resource based on the resource name
     *
     * @param resourceName resource name
     */
    public List<ResourceTriple> findByResourceName(String resourceName);

    /**
     * Get resource based on the resource name and predicate
     *
     * @param resourceName resource name
     * @param rdfPredicate predicate
     */
    public List<ResourceTriple> findByResourceNameAndPredicate(String resourceName, String rdfPredicate);

}
