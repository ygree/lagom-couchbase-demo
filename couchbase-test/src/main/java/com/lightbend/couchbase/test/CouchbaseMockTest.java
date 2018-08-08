package com.lightbend.couchbase.test;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.mock.Bucket;
import com.couchbase.mock.BucketConfiguration;
import com.couchbase.mock.CouchbaseMock;
import com.lightbend.couchbase.Couchbase;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;

/**
 * The base-class for testing data access services with Couchbase Mock.
 * For Lagom services test or Guice injection support see {@link CouchbaseMockLagomTest}.
 */
abstract public class CouchbaseMockTest {
    private final BucketConfiguration bucketConfiguration = new BucketConfiguration();
    private CouchbaseMock couchbaseMock;
    private Cluster cluster;
    private com.couchbase.client.java.Bucket bucket;
    private int carrierPort;
    private int httpPort;
    private Couchbase couchbase;

    private void getPortInfo(String bucket) {
        httpPort = couchbaseMock.getHttpPort();
        carrierPort = couchbaseMock.getCarrierPort(bucket);
    }

    private void createMock(String name, String password) throws Exception {
        bucketConfiguration.numNodes = 1;
        bucketConfiguration.numReplicas = 1;
        bucketConfiguration.numVBuckets = 1024;
        bucketConfiguration.name = name;
        bucketConfiguration.type = Bucket.BucketType.COUCHBASE;
        bucketConfiguration.password = password;
        ArrayList<BucketConfiguration> configList = new ArrayList<>();
        configList.add(bucketConfiguration);
        couchbaseMock = new CouchbaseMock(0, configList);
        couchbaseMock.start();
        couchbaseMock.waitForStartup();
    }

    private void createClient() {
        cluster = CouchbaseCluster.create(DefaultCouchbaseEnvironment.builder()
                .bootstrapCarrierDirectPort(carrierPort)
                .bootstrapHttpDirectPort(httpPort)
                .build() ,"couchbase://127.0.0.1");
        bucket = cluster.openBucket("default");
    }

    public Couchbase getCouchbase() {
        return couchbase;
    }

    @Before
    public void setUp() throws Exception {
        createMock("default", "");
        getPortInfo("default");
        createClient();
        couchbase = () -> bucket;
    }

    @After
    public void tearDown() {
        if (cluster != null) {
            cluster.disconnect();
        }
        if (couchbaseMock != null) {
            couchbaseMock.stop();
        }
    }

}
