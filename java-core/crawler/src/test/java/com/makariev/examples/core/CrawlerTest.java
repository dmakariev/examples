package com.makariev.examples.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CrawlerTest {

    private static final Path CRAWL_DIR = Path.of("crawled-pages");

    @Test
    void testCrawlWebPagesSynchronouslyPlain() throws IOException {
        final Crawler crawler = new Crawler(new PlainLinkExtractor());
        crawler.crawlSynchronously(URI.create("https://example.com"), 2);

        // Verify that crawled pages are saved in the directory
        assertThat(Files.list(CRAWL_DIR).count()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCrawlWebPagesAsynchronouslyPlain() throws IOException {
        final Crawler crawler = new Crawler(new PlainLinkExtractor());
        crawler.crawlAsynchronously(URI.create("https://example.com"), 2);

        // Verify that crawled pages are saved in the directory
        assertThat(Files.list(CRAWL_DIR).count()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCrawlWebPagesSynchronouslyJSoup() throws IOException {
        final Crawler crawler = new Crawler(new JsoupLinkExtractor());
        crawler.crawlSynchronously(URI.create("https://example.com"), 2);

        // Verify that crawled pages are saved in the directory
        assertThat(Files.list(CRAWL_DIR).count()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCrawlWebPagesAsynchronouslyJSoup() throws IOException {
        final Crawler crawler = new Crawler(new JsoupLinkExtractor());
        crawler.crawlAsynchronously(URI.create("https://example.com"), 2);

        // Verify that crawled pages are saved in the directory
        assertThat(Files.list(CRAWL_DIR).count()).isGreaterThanOrEqualTo(1);
    }
}
