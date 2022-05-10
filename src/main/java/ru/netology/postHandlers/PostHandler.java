package ru.netology.postHandlers;

import ru.netology.Request;

import java.io.BufferedOutputStream;
import java.nio.file.Path;
import java.util.Map;


public interface PostHandler {
    boolean isSuitableCase(String path);
    void handle(Request request, BufferedOutputStream out);
}
