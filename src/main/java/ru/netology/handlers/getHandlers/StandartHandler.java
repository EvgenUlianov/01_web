package ru.netology.handlers.getHandlers;

import ru.netology.FileManager;
import ru.netology.Request;
import ru.netology.handlers.Handler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StandartHandler implements Handler {

    @Override
    public boolean isSuitableCase(Request request) {
        return true;
    }

    @Override
    public void handle(Request request, BufferedOutputStream out) {
        Path filePath = FileManager.get().getFilePath(request);

        long length = 0;
        try {
            length = Files.size(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
