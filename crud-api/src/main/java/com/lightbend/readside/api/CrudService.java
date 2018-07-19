package com.lightbend.readside.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

public interface CrudService extends Service {

  /**
   * Example: curl http://localhost:9000/api/readside/hello/Alice
   */
  ServiceCall<NotUsed, String> hello(String id);

  ServiceCall<GreetingMessage, Done> useGreeting(String id);

    @Override
  default Descriptor descriptor() {
    return named("crud")
            .withCalls(
                pathCall("/crud-api/hello/:id",  this::hello),
                pathCall("/crud-api/hello/:id", this::useGreeting)
            ).withAutoAcl(true);
  }
}
