package ru.netology.handlers;

import java.io.BufferedOutputStream;
import java.nio.file.Path;


public interface Handler {
    boolean isSuitableCase(String path);
    int priority();
    void handle(String path, Path filePath, BufferedOutputStream out);
}
