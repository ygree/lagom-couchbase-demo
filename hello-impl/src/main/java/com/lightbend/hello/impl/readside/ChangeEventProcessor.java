package com.lightbend.hello.impl.readside;

import akka.Done;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.lightbend.hello.impl.HelloEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.PSequence;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ChangeEventProcessor extends ReadSideProcessor<HelloEvent> {

    final private ReadSideRepository repository;

    @Inject
    public ChangeEventProcessor(ReadSideRepository repository) {
        this.repository = repository;
    }

    @Override
    public PSequence<AggregateEventTag<HelloEvent>> aggregateTags() {
        return HelloEvent.TAG.allTags();
    }

    @Override
    public ReadSideHandler<HelloEvent> buildHandler() {
        return new ReadSideHandler<HelloEvent>() {
            private AggregateEventTag<HelloEvent> tag;

            @Override
            public CompletionStage<Done> globalPrepare() {
                return super.globalPrepare();
            }

            @Override
            public CompletionStage<Offset> prepare(AggregateEventTag<HelloEvent> tag) {
                this.tag = tag;
                return repository.getOffset(tag);
            }

            @Override
            public Flow<Pair<HelloEvent, Offset>, Done, ?> handle() {
                return Flow.<Pair<HelloEvent, Offset>>create()
                        .mapAsync(1, eventAndOffset ->
                                updateByEvent(eventAndOffset.first(), eventAndOffset.second(), tag)
                        );
            }
        };
    }

    private CompletionStage<Done> updateByEvent(HelloEvent event, Offset offset, AggregateEventTag<HelloEvent> tag) {
        if (event instanceof HelloEvent.GreetingMessageChanged) {
            HelloEvent.GreetingMessageChanged evt = (HelloEvent.GreetingMessageChanged) event;
            return repository.updateMessage(evt.name, evt.message)
                    .thenCompose(v -> repository.updateOffset(tag, offset));
        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
