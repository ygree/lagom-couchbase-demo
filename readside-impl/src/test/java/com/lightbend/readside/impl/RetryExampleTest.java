package com.lightbend.readside.impl;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class RetryExampleTest {


    public <T> CompletionStage<T> retry(Supplier<CompletionStage<T>> call, int numberOfRetries) {
        if (numberOfRetries > 0) {
            CompletionStage<CompletionStage<T>> completionStageCompletionStage = call.get()
                    .handleAsync((v, e) -> e != null ? retry(call, numberOfRetries - 1)
                                                     : CompletableFuture.completedFuture(v));
            return completionStageCompletionStage.thenCompose(Function.identity());
        }
        return call.get();
    }

    private static class Failer {
        private final AtomicInteger failuresCounter;

        public Failer(int failuresCounter) {
            this.failuresCounter = new AtomicInteger(failuresCounter);
        }

        public int execute() {
            if (failuresCounter.decrementAndGet() > 0) {
                throw new RuntimeException("failed");
            }
            return 42;
        }
    }

    @Test
    public void testWillRetryAndSuccess() {
        Failer failer = new Failer(3);

        Supplier<CompletionStage<Integer>> failing = () -> CompletableFuture.supplyAsync(() -> failer.execute());
        CompletionStage<Integer> result = retry(failing, 2);

        assertEquals(42, (int) result.toCompletableFuture().join());
    }

    @Test(expected = RuntimeException.class)
    public void testWhenItStillFail() {
        Failer failer = new Failer(3);

        Supplier<CompletionStage<Integer>> failing = () -> CompletableFuture.supplyAsync(() -> failer.execute());
        CompletionStage<Integer> result = retry(failing, 1);

        result.toCompletableFuture().join();
    }


}
