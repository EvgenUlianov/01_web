package ru.netology.handlers;

import ru.netology.Request;

import java.io.BufferedOutputStream;
import java.nio.file.Path;


public interface Handler {
    boolean isSuitableCase(String path);
    int priority();
    void handle(String path, Path filePath, BufferedOutputStream out);
}
