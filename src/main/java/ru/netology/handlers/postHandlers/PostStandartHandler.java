package ru.netology.handlers.postHandlers;

import ru.netology.Request;
import ru.netology.handlers.Handler;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class PostStandartHandler implements Handler {

    @Override
    public boolean isSuitableCase(Request request) {
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
        resultBuilder.append(request.toString());
        try {
            out.write(resultBuilder.toString().getBytes());//
            System.out.println(resultBuilder);
            out.flush();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
