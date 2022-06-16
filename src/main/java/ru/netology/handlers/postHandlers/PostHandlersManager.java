package ru.netology.handlers.postHandlers;

import lombok.Getter;
import ru.netology.Request;
import ru.netology.handlers.Handler;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostHandlersManager {

    private final List<Handler> handlers;

    public void handle(Request request, BufferedOutputStream out){
        for (Handler handler: handlers)
            if (handler.isSuitableCase(request)) {
                handler.handle(request, out);
                break;
            }
    }

    // SingleTone ++
    //multi tread safety

    private PostHandlersManager(){
        handlers = new ArrayList<>(1);
        handlers.add(new PostStandartHandler());
    }

    private static class Holder {
        public static final PostHandlersManager HANDLERS_MANAGER = new PostHandlersManager();
    }

    public static PostHandlersManager get()  {
        return Holder.HANDLERS_MANAGER;
    }

    // SingleTone --

}
