package org.fcrepo.lambdora.service.dao;

import java.util.List;

import org.fcrepo.lambdora.service.db.ResourceTriple;

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
     * Remove a ResourceTriple from the database
     * @param triple ResourceTriple
     */
    public void deleteResourceTriple(ResourceTriple triple);

    /**
     * Get list of triples based on the RDF object uri
     *
     * @param rdfObject uri
     */
    public List<ResourceTriple> findByObject(String rdfObject);

    /**
     * Get list of triples based on the RDF object uri and predicate
     *
     * @param rdfObject uri
     * @param rdfPredicate predicate
     */
    public List<ResourceTriple> findByObjectAndPredicate(String rdfObject, String rdfPredicate);

    /**
     * Get list of triples based on the resource name
     *
     * @param resourceName resource name
     */
    public List<ResourceTriple> findByResourceName(String resourceName);

    /**
     * Get list of triples on the resource name and predicate
     *
     * @param resourceName resource name
     * @param rdfPredicate predicate
     */
    public List<ResourceTriple> findByResourceNameAndPredicate(String resourceName, String rdfPredicate);

}
