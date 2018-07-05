package utils;

import rx.Observable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RxJava8Utils {
    public static <T> CompletableFuture<List<T>> fromObservable(Observable<T> observable) {
        final CompletableFuture<List<T>> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .toList()
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<T> fromSingleObservable(Observable<T> observable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .single()
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<Optional<T>> fromSingleOptObservable(Observable<T> observable) {
        final CompletableFuture<Optional<T>> future = new CompletableFuture<>();
        observable
                .map(Optional::ofNullable)
                .doOnError(future::completeExceptionally)
                .singleOrDefault(Optional.empty())
                .forEach(future::complete);
        return future;
    }

    public static <T> CompletableFuture<Optional<T>> fromSingleOptOptObservable(Observable<Optional<T>> observable) {
        final CompletableFuture<Optional<T>> future = new CompletableFuture<>();
        observable
                .doOnError(future::completeExceptionally)
                .singleOrDefault(Optional.empty())
                .forEach(future::complete);
        return future;
    }
}
