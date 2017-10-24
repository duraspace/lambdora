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
     * Get resource based on the RDF object uri
     *
     * This is an eventually consistent read request
     *
     * @param object uri
     */
    public List<ResourceTriple> findByObject(String object);

    /**
     * Get resource based on the RDF object uri and predicate
     *
     * @param object uri
     * @param predicate predicate
     */
    public List<ResourceTriple> findByObjectAndPredicate(String object, String predicate);

    /**
     * Get resource based on the resource name
     *
     * @param name object
     */
    public List<ResourceTriple> findByName(String name);

    /**
     * Get resource based on the resource name and predicate
     *
     * @param name object
     * @param predicate predicate
     */
    public List<ResourceTriple> findByNameAndPredicate(String name, String predicate);

}
