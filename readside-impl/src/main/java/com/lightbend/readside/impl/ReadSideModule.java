package com.lightbend.readside.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.lightbend.hello.api.HelloService;
import com.lightbend.readside.api.ReadSideService;

/**
 * The module that binds the ReadSideService so that it can be served.
 */
public class ReadSideModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    // Bind the ReadSideService service
    bindService(ReadSideService.class, ReadSideServiceImpl.class);

    // Bind the HelloService client
    bindClient(HelloService.class);
    
    // Bind the subscriber eagerly to ensure it starts up
    bind(ReadSideSubscriber.class).asEagerSingleton();
  }
}
