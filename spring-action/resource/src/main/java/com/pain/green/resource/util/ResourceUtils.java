package com.pain.green.resource.util;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.io.Reader;

public class ResourceUtils {
    public static String getContent(Resource resource) {
        try {
            return getContent(resource, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static String getContent(Resource resource, String encoding) throws IOException {
        EncodedResource encodedResource = new EncodedResource(resource, encoding);

        try (Reader reader = encodedResource.getReader()) {
            return IOUtils.toString(reader);
        }
    }
}
