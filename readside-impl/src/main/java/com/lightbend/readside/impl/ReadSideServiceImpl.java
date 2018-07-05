package com.lightbend.readside.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.hello.api.HelloService;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.readside.api.ReadSideService;

import javax.inject.Inject;

/**
 * Implementation of the HelloString.
 */
public class ReadSideServiceImpl implements ReadSideService {

    private final HelloService helloService;
    private final ReadSideRepository repository;

    @Inject
    public ReadSideServiceImpl(HelloService helloService,
                               ReadSideRepository repository,
                               ReadSide readSide) {
        this.helloService = helloService;
        this.repository = repository;
//TODO        readSide.register(HelloEventProcessor.class);
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
