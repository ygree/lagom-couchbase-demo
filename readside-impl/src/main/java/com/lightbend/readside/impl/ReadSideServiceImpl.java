package com.lightbend.readside.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.readside.api.ReadSideService;

import javax.inject.Inject;

public class ReadSideServiceImpl implements ReadSideService {

    private final ReadSideRepository repository;

    @Inject
    public ReadSideServiceImpl(ReadSideRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceCall<NotUsed, String> hello(String name) {
        return request -> {
            return repository.getMessage(name).thenApply(message ->
                    String.format("%s, %s!", message.orElse("Hello (default)"), name)
            );
        };
    }
}
