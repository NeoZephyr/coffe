package com.pain.green.resource;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.io.Reader;

public class EncodedFileSystemResourceLoaderDemo {
    public static void main(String[] args) throws IOException {
        String currentFilePath = "/" + System.getProperty("user.dir") + "/resource/src/main/java/com/pain/green/resource/EncodedFileSystemResourceLoaderDemo.java";
        FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
        Resource resource = fileSystemResourceLoader.getResource(currentFilePath);
        EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");

        try (Reader reader = encodedResource.getReader()) {
            System.out.println(IOUtils.toString(reader));
        }
    }
}
