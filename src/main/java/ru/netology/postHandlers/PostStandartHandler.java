package ru.netology.postHandlers;

import ru.netology.Request;

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
    public void handle(Request request, BufferedOutputStream out) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("HTTP/1.1 200 OK");
        resultBuilder.append("\r\n");
        resultBuilder.append("Content-Type: text");
        resultBuilder.append("\r\n");
        resultBuilder.append("\r\n");
        resultBuilder.append("============ Querry params: ============");
        resultBuilder.append("\r\n");
        for (Map.Entry<String, String> header: request.getQuerryParams().entrySet()) {
            resultBuilder.append(header.getKey());
            resultBuilder.append(": ");
            resultBuilder.append(header.getValue());
            resultBuilder.append("\r\n");
        }
        resultBuilder.append("\r\n");
        resultBuilder.append("=============== Headers: ===============");
        resultBuilder.append("\r\n");
        for (Map.Entry<String, String> header: request.getHeaders().entrySet()) {
            resultBuilder.append(header.getKey());
            resultBuilder.append(": ");
            resultBuilder.append(header.getValue());
            resultBuilder.append("\r\n");
        }
        //        resultBuilder.append("test");
        //        resultBuilder.append("\r\n");
        if(!request.bodyIsEmpty()){
            resultBuilder.append("\r\n");
            resultBuilder.append("================= Body: =================");
            resultBuilder.append("\r\n");
            resultBuilder.append(request.getBody());
        }
        try {
            out.write(resultBuilder.toString().getBytes());//
            System.out.println(resultBuilder.toString());
            out.flush();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
