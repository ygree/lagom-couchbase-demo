package com.lightbend.readside.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.readside.api.GreetingMessage;
import com.lightbend.readside.api.CrudService;

import javax.inject.Inject;

public class CrudServiceImpl implements CrudService {

    private final CrudRepository repository;

    @Inject
    public CrudServiceImpl(CrudBlockingRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceCall<NotUsed, String> hello(String name) {
        return request ->
            repository.getMessage(name).thenApply(message ->
                String.format("%s, %s!", message.orElse("Hello (default)"), name)
            );
    }

    @Override
    public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
        return request -> repository.updateMessage(id, request.getMessage());
    }
}
