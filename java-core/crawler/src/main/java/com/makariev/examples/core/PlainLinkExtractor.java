package com.makariev.examples.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlainLinkExtractor implements LinkExtractor {

    @Override
    public List<String> extractLinks(String htmlContent) {
        final List<String> links = new ArrayList<>();

        // Regular expression to find HTML anchor tags
        final String regex = "<a\\s+href\\s*=\\s*\"([^\"]+)\"[^>]*>";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {
            final String link = matcher.group(1);
            links.add(link);
        }

        return links;
    }

}
