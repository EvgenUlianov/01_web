package ru.netology.handlers.getHandlers;

import ru.netology.Request;
import ru.netology.handlers.Handler;
import ru.netology.handlers.postHandlers.PostStandartHandler;

public class HandlerMessages extends PostStandartHandler implements Handler {
    @Override
    public boolean isSuitableCase(Request request) {
        return request.getPath().equals("/messages");
    }


//    @Override
//    public void handle(Request request, BufferedOutputStream out) {
//
//    }
}
