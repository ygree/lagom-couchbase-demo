package com.lightbend.readside.couchbase;

import akka.actor.ActorSystem;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.AsyncCluster;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.auth.PasswordAuthenticator;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.typesafe.config.Config;
import rx.Observable;

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

    protected Observable<AsyncBucket> openBucket(AsyncCluster cluster) {
        if (username.isPresent()) {
            cluster.authenticate(new PasswordAuthenticator(username.get(), bucketPassword.orElse(null)));
            return cluster.openBucket(bucketName);
        }
        return cluster.openBucket(bucketName, bucketPassword.orElse(null));
    }

    protected CouchbaseAsyncCluster createCluster(CouchbaseEnvironment environment) {
        return CouchbaseAsyncCluster.create(environment, nodes);
    }
}
