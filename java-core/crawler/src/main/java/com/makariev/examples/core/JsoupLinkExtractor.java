package com.makariev.examples.core;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupLinkExtractor implements LinkExtractor {

    @Override
    public List<String> extractLinks(String htmlContent) {
        final List<String> links = new ArrayList<>();

        final Document document = Jsoup.parse(htmlContent);

        final Elements anchorTags = document.select("a[href]");

        for (Element anchorTag : anchorTags) {
            final String link = anchorTag.attr("href");
            links.add(link);
        }

        return links;
    }

}
