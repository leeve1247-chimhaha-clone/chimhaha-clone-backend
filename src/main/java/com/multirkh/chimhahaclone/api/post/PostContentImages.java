package com.multirkh.chimhahaclone.api.post;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Extracts image fileNames from a post's content JSON. The post service no
 * longer owns image data — it derives the referenced fileNames here and ships
 * them in domain events so the image service can manage the association.
 */
final class PostContentImages {

    private PostContentImages() {
    }

    /** All image fileNames referenced in the content, in document order. */
    static Set<String> fileNames(JsonNode content) {
        Set<String> names = new LinkedHashSet<>();
        if (content == null) {
            return names;
        }
        content.findParents("type").stream()
                .filter(node -> node.get("type").asText().equals("image"))
                .map(node -> node.get("src").asText())
                .map(PostContentImages::fileNameFrom)
                .forEach(names::add);
        return names;
    }

    /** The first referenced image fileName, used as the list thumbnail; null if none. */
    static String firstFileName(JsonNode content) {
        Set<String> names = fileNames(content);
        return names.isEmpty() ? null : names.iterator().next();
    }

    private static String fileNameFrom(String imageSrcUrl) {
        try {
            Path path = Paths.get(new URI(imageSrcUrl).getPath());
            return path.getFileName().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
