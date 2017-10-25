package org.fcrepo.lambdora.db;


import java.util.List;

import org.fcrepo.lambdora.dao.DynamoDBResourceTripleDao;
import org.fcrepo.lambdora.dao.ResourceTripleDao;

/**
 * A test CLI class for ResourceTriple
 *
 * Before execution, ensure these environment variables are defined:
 *   AWS_ACCESS_KEY_ID - The API access key, usually associated with an IAM user
 *   AWS_SECRET_ACCESS_KEY - The API secret key, usually associated with an IAM user
 *   AWS_REGION - The AWS regions, e.g. 'us-east-1'
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
