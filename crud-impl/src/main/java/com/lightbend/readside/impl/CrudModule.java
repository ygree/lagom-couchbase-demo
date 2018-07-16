package com.lightbend.readside.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.lightbend.readside.api.ReadSideService;

/**
 * The module that binds the ReadSideService so that it can be served.
 */
public class CrudModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {

    bind(CrudRepository.class).to(CrudAsyncRepository.class);
//    bind(CrudRepository.class).to(CrudBlockingRepository.class);

    // Bind the ReadSideService service
    bindService(ReadSideService.class, CrudServiceImpl.class);

  }
}
