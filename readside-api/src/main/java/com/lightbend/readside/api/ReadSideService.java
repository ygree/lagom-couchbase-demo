package com.lightbend.readside.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

public interface ReadSideService extends Service {

  /**
   * Example: curl http://localhost:9000/api/readside/hello/Alice
   */
  ServiceCall<NotUsed, String> hello(String id);

  ServiceCall<GreetingMessage, Done> useGreeting(String id);

    @Override
  default Descriptor descriptor() {
    return named("readside")
            .withCalls(
                pathCall("/api/readside/hello/:id",  this::hello),
                pathCall("/api/readside/hello/:id", this::useGreeting)
            ).withAutoAcl(true);
  }
}
