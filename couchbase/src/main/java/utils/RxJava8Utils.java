package utils;

import rx.Observable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RxJava8Utils {
    public static <T> CompletableFuture<List<T>> fromMultiple(Observable<T> observable) {
        final CompletableFuture<List<T>> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .toList()
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<T> fromSingle(Observable<T> observable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .single()
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<Optional<T>> fromNullable(Observable<T> observable) {
        final CompletableFuture<Optional<T>> future = new CompletableFuture<>();
        observable
                .map(Optional::ofNullable)
                .doOnError(future::completeExceptionally)
                .singleOrDefault(Optional.empty())
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<Optional<T>> fromOptional(Observable<Optional<T>> observable) {
        final CompletableFuture<Optional<T>> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .singleOrDefault(Optional.empty())
                .forEach(future::complete);
        return future;
    }
}
