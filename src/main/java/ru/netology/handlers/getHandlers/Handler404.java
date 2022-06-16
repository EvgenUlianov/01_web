package ru.netology.handlers.getHandlers;

import ru.netology.Request;
import ru.netology.handlers.Handler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

public class Handler404 implements Handler {

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
            "/events.js",
            "/messages");

    @Override
    public boolean isSuitableCase(Request request) {
        return !validPaths.contains(request.getPath());
    }

    @Override
    public void handle(Request request, BufferedOutputStream out){
//        Path filePath = FileManager.get().getFilePath(request);

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
