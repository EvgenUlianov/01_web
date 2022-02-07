package ru.netology.postHandlers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class PostStandartHandler implements PostHandler {

    @Override
    public boolean isSuitableCase(String path) {
        return true;
    }

    @Override
    public void handle(String path, Map<String, String> pathParams, String headers, String requestBody, BufferedOutputStream out) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("HTTP/1.1 200 OK");
        resultBuilder.append("\r\n");
        resultBuilder.append("Content-Type: text");
        resultBuilder.append("\r\n");
        resultBuilder.append("\r\n");
        //        for (Map.Entry<String, String> header: headers.entrySet()) {
//            resultBuilder.append(header.getKey());
//            resultBuilder.append(": ");
//            resultBuilder.append(header.getValue());
//            resultBuilder.append("\r\n");
//
//        }
//        resultBuilder.append("test");
//        resultBuilder.append("\r\n");
        resultBuilder.append(requestBody);
        try {
            out.write(resultBuilder.toString().getBytes());
            out.flush();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
