package com.makariev.examples.core;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Crawler {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Path CRAWL_DIR = Path.of("crawled-pages");
    private final LinkExtractor linkExtractor;

    public Crawler(LinkExtractor linkExtractor) {
        this.linkExtractor = linkExtractor;
    }

    public void crawlSynchronously(URI startUrl, int depth) {
        crawlRecursivelySynchronously(startUrl, depth);
    }

    public void crawlAsynchronously(URI startUrl, int depth) {
        crawlRecursivelyAsynchronously(startUrl, depth);
    }

    private void crawlRecursivelySynchronously(URI url, int depth) {
        if (depth <= 0) {
            return;
        }

        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .build();
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                final List<URI> links = extractLinks(response.body());
                savePageContent(url, response.body());

                for (URI link : links) {
                    crawlRecursivelySynchronously(link, depth - 1);
                }
            }
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void crawlRecursivelyAsynchronously(URI url, int depth) {
        if (depth <= 0) {
            return;
        }

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .build();

        final CompletableFuture<HttpResponse<String>> responseFuture
                = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenAccept(response -> {
            if (response.statusCode() == 200) {
                final List<URI> links = extractLinks(response.body());
                savePageContent(url, response.body());

                for (URI link : links) {
                    crawlRecursivelyAsynchronously(link, depth - 1);
                }
            }
        });
    }

    private List<URI> extractLinks(String pageContent) {
        return linkExtractor.extractLinks(pageContent)
                .stream()
                .map(link -> {
                    try {
                        return URI.create(link);
                    } catch (Exception e) {
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void savePageContent(URI url, String content) {
        final String fileName = (url.getHost() + url.getPath() + ".html").replace("/.html", "/index.html");
        final Path filePath = CRAWL_DIR.resolve(fileName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
