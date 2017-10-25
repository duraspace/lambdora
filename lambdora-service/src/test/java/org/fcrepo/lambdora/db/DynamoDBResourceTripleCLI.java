package org.fcrepo.lambdora.db;


import org.fcrepo.lambdora.dao.DynamoDBResourceTripleDao;
import org.fcrepo.lambdora.dao.ResourceTripleDao;

import java.util.List;

/**
 * A test CLI class for ResourceTriple
 *
 * @author tdonohue
 * @author bbranan
 */
public class DynamoDBResourceTripleCLI {

    private DynamoDBResourceTripleCLI() {
    }

    /**
     * CLI implementation
     * @param args arguments
     */
    public static void main(final String[] args) {

        final ResourceTripleDao resourceTripleDao = DynamoDBResourceTripleDao.getInstance();

        resourceTripleDao.addResourceTriple("resource1", "subject1 predicate1 object1", "subject1",
            "predicate1", "object1");
        resourceTripleDao.addResourceTriple("resource1", "subject2 predicate2 object2", "subject2",
            "predicate2", "object2");

        final List<ResourceTriple> resourceTripleList = resourceTripleDao.findByResourceName("resource1");

        for (ResourceTriple rt: resourceTripleList) {
            System.out.println(rt);
        }
    }
}
