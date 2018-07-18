package com.lightbend.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.mock.Bucket;
import com.couchbase.mock.BucketConfiguration;
import com.couchbase.mock.CouchbaseMock;
import com.couchbase.mock.client.MockClient;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

abstract public class CouchbaseMockTest extends TestCase {
    private final BucketConfiguration bucketConfiguration = new BucketConfiguration();
    private MockClient mockClient;
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

    private void createMock(@NotNull String name, @NotNull String password) throws Exception {
        bucketConfiguration.numNodes = 1;
        bucketConfiguration.numReplicas = 1;
        bucketConfiguration.numVBuckets = 1024;
        bucketConfiguration.name = name;
        bucketConfiguration.type = Bucket.BucketType.COUCHBASE;
        bucketConfiguration.password = password;
        ArrayList<BucketConfiguration> configList = new ArrayList<BucketConfiguration>();
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createMock("default", "");
        getPortInfo("default");
        createClient();
        couchbase = () -> bucket.async();
    }

    @Override
    protected void tearDown() throws Exception {
        if (cluster != null) {
            cluster.disconnect();
        }
        if (couchbaseMock != null) {
            couchbaseMock.stop();
        }
        if (mockClient != null) {
            mockClient.shutdown();
        }
        super.tearDown();
    }

}
