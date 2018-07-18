package com.lightbend.couchbase;

import akka.actor.ActorSystem;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.auth.PasswordAuthenticator;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.typesafe.config.Config;

import java.util.List;
import java.util.Optional;

public class CouchbaseConfig {
    private final Config config;
    private final Optional<String> username;
    private final Optional<String> bucketPassword;
    private final String bucketName;
    private final List<String> nodes;

    public CouchbaseConfig(ActorSystem system) {
        this(system.settings().config().getConfig("couchbase"));
    }

    protected CouchbaseConfig(Config config) {
        this.config = config;
        this.nodes = config.getStringList("nodes");
        this.username = Optional.ofNullable(config.getString("username"));
        this.bucketPassword = Optional.ofNullable(config.getString("password"));
        this.bucketName = config.getString("bucket");
    }

    protected Bucket openBucket(Cluster cluster) {
        if (username.isPresent()) {
            cluster.authenticate(new PasswordAuthenticator(username.get(), bucketPassword.orElse(null)));
            return cluster.openBucket(bucketName);
        }
        return cluster.openBucket(bucketName, bucketPassword.orElse(null));
    }

    protected CouchbaseCluster createCluster(CouchbaseEnvironment environment) {
        return CouchbaseCluster.create(environment, nodes);
    }
}
