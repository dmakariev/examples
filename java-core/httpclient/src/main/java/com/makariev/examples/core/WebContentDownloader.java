package com.makariev.examples.core;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class WebContentDownloader {

    public static void downloadWebPageContentSynchronously(String url, String savePath) throws IOException, InterruptedException {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        final HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            final byte[] responseBody = response.body();
            final Path file = Path.of(savePath);
            Files.write(file, responseBody);
        }
    }

    public static CompletableFuture<Void> downloadWebPageContentAsynchronously(String url, String savePath) {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        final byte[] responseBody = response.body();
                        final Path file = Path.of(savePath);
                        try {
                            Files.write(file, responseBody);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                });
    }

}
