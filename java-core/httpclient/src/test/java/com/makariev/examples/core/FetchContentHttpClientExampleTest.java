package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FetchContentHttpClientExampleTest {

    @Test
    void testFetchWebPageContentSynchronously() throws IOException, InterruptedException {
        String url = "https://example.com";
        String savePath = "example-sync.html";

        WebContentDownloader.downloadWebPageContentSynchronously(url, savePath);
        Path file = Path.of(savePath);

        assertThat(file.toFile().exists()).isTrue();
        assertThat(file.toFile().length()).isGreaterThan(0);
    }

    @Test
    void testFetchWebPageContentAsynchronously() throws ExecutionException, InterruptedException {
        String url = "https://example.com";
        String savePath = "example-async.html";

        CompletableFuture<Void> future = WebContentDownloader.downloadWebPageContentAsynchronously(url, savePath);
        future.get(); // Wait for the asynchronous operation to complete

        Path file = Path.of(savePath);

        assertThat(file.toFile().exists()).isTrue();
        assertThat(file.toFile().length()).isGreaterThan(0);
    }
}
