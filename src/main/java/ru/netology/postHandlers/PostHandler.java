package ru.netology.postHandlers;

import java.io.BufferedOutputStream;
import java.nio.file.Path;
import java.util.Map;


public interface PostHandler {
    boolean isSuitableCase(String path);
    void handle(String path, Map<String, String> pathParams, String headers, String requestBody, BufferedOutputStream out);
}
