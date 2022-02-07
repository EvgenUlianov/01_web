package ru.netology.postHandlers;

import lombok.Getter;
import ru.netology.handlers.Classic;
import ru.netology.handlers.Handler404;

import java.io.BufferedOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class PostHandlersManager {

    private final List<PostHandler> handlers;

    public void handle(String path, Map<String, String> pathParams, String headers, String requestBody, BufferedOutputStream out){
        for (PostHandler handler: handlers)
            if (handler.isSuitableCase(path)) {
                handler.handle(path, pathParams, headers, requestBody, out);
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
