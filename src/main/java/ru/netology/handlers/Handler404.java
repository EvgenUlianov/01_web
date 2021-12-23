package ru.netology.handlers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Handler404 implements Handler{

    private final List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js");

    @Override
    public boolean isSuitableCase(String path) {
        return !validPaths.contains(path);
    }

    @Override
    public void handle(String path, Path filePath, BufferedOutputStream out){
        try {
            out.write((
                    """
                            HTTP/1.1 404 Not Found\r
                            Content-Length: 0\r
                            Connection: close\r
                            \r
                            """
            ).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
