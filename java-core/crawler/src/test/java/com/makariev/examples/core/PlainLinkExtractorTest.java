package com.makariev.examples.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PlainLinkExtractorTest {

    @Test
    void testExtractLinks() {
        final String htmlContent = """
            <a href=\"https://example.com\">Example</a> 
            <a href=\"https://example.org\">Another Example</a>
        """;

        final List<String> extractedLinks = new PlainLinkExtractor().extractLinks(htmlContent);

        // Verify that two links are extracted
        assertThat(extractedLinks).hasSize(2);

        // Verify the extracted links
        assertThat(extractedLinks).containsExactly("https://example.com", "https://example.org");
    }

    @Test
    void testExtractLinksNoLinks() {
        final String htmlContent = "This is a sample text without any links.";

        final List<String> extractedLinks = new PlainLinkExtractor().extractLinks(htmlContent);

        // Verify that no links are extracted
        assertThat(extractedLinks).isEmpty();
    }
}
