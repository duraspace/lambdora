package org.fcrepo.lambdora.service.db;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.fcrepo.lambdora.common.test.IntegrationTestBase;
import org.fcrepo.lambdora.service.dao.DynamoDBResourceTripleDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the interaction with DynamoDB through the DynamoDBResourceTripleDao
 *
 * @author bbranan
 */
public class DynamoDBResourceTripleDaoTest extends IntegrationTestBase {

    private DynamoDBResourceTripleDao resourceTripleDao;

    // Resources
    private final String resource1 = "resource1";
    private final String resource2 = "resource2";

    // Triples 1a & 1b associated with resource 1, triple 2 associated with resource 2
    private final String triple1a = resource1 + ":predicate1:object1";
    private final String triple1b = resource1 + ":predicate2:object2";
    private final String triple2 = resource2 + ":predicate3:object1";

    // Triple arrays where [0] = subject, [1] = predicate, [2] = object
    private final String[] t1a = triple1a.split(":");
    private final String[] t1b = triple1b.split(":");
    private final String[] t2 = triple2.split(":");

    @Before
    @Override
    public void setup() {
        super.setup();
        final DynamoDBMapper mapper = new DynamoDBMapper(dynamodbClient);
        resourceTripleDao = new DynamoDBResourceTripleDao(mapper);

        addResourceData();
    }

    private void addResourceData() {
        // Add as a ResourceTriple
        final ResourceTriple rTriple1a =
            new ResourceTriple(resource1, triple1a, t1a[0], t1a[1], t1a[2]);
        resourceTripleDao.addResourceTriple(rTriple1a);

        // Add as a set of Strings
        resourceTripleDao.addResourceTriple(resource1, triple1b, t1b[0], t1b[1], t1b[2]);
        resourceTripleDao.addResourceTriple(resource2, triple2, t2[0], t2[1], t2[2]);
    }

    /**
     * Tests finding an triples based on rdf object
     */
    @Test
    public void testFindByObject() {
        final List<ResourceTriple> resultList =
            resourceTripleDao.findByObject(t1a[2]);

        assertEquals(2, resultList.size());
        for (ResourceTriple triple : resultList) {
            assertEquals(t1a[2], triple.getRdfObject());
        }
    }

    /**
     * Tests finding triples based on rdf object and predicate
     */
    @Test
    public void testFindByObjectAndPredicate() {
        final List<ResourceTriple> resultList =
            resourceTripleDao.findByObjectAndPredicate(t2[2], t2[1]);

        assertEquals(1, resultList.size());
        for (ResourceTriple triple : resultList) {
            assertEquals(triple2, triple.getRdfTriple());
        }
    }

    /**
     * Tests finding triples based on resource name (uri)
     */
    @Test
    public void testFindByResourceName() {
        final List<ResourceTriple> resultList =
            resourceTripleDao.findByResourceName(resource1);

        assertEquals(2, resultList.size());
        for (ResourceTriple triple : resultList) {
            assertEquals(resource1, triple.getResourceName());
        }
    }

    /**
     * Tests finding triples based on resource name (uri) and rdf predicate
     */
    @Test
    public void testFindByResourceNameAndPredicate() {
        final List<ResourceTriple> resultList =
            resourceTripleDao.findByResourceNameAndPredicate(resource1, t1b[1]);

        assertEquals(1, resultList.size());
        for (ResourceTriple triple : resultList) {
            assertEquals(t1b[0], triple.getRdfSubject());
            assertEquals(t1b[1], triple.getRdfPredicate());
            assertEquals(t1b[2], triple.getRdfObject());
            assertEquals(resource1, triple.getResourceName());
            assertEquals(triple1b, triple.getRdfTriple());
        }

    }

}
