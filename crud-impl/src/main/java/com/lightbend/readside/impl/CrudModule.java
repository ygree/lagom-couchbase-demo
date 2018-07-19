package com.lightbend.readside.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.lightbend.readside.api.CrudService;

/**
 * The module that binds the CrudService so that it can be served.
 */
public class CrudModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {

    bind(CrudRepository.class).to(CrudAsyncRepository.class);

    // Bind the CrudService service
    bindService(CrudService.class, CrudServiceImpl.class);

  }
}
