package com.lightbend.readside.impl;

import akka.Done;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CrudRepository {
    CompletionStage<Done> updateMessage(String name, String message);

    CompletionStage<Optional<String>> getMessage(String name);
}
