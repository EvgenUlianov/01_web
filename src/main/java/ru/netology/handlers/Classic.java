package ru.netology.handlers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Classic implements Handler{


    @Override
    public boolean isSuitableCase(String path) {
        return path.equals("/classic.html");
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public void handle(String path, Path filePath, BufferedOutputStream out) {
        String mimeType = null;
        String template = null;
        try {
            mimeType = Files.probeContentType(filePath);
            template = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final byte[] content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        try {
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
