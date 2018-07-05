package com.lightbend.readside.impl;

import akka.Done;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

@Singleton
public class ReadSideRepository {

  final Map<String, String> greetings;

  @Inject
  public ReadSideRepository() {
      greetings = new HashMap<>();
  }


  public CompletionStage<Done> updateMessage(String name, String message) {
      greetings.put(name, message);
      return CompletableFuture.completedFuture(Done.getInstance());
  }

  public CompletionStage<Optional<String>> getMessage(String name) {
      return CompletableFuture.completedFuture(Optional.ofNullable(greetings.get(name)));
  }
}
