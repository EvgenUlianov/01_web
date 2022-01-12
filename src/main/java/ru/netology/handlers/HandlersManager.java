package ru.netology.handlers;

import lombok.Getter;

import java.io.BufferedOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HandlersManager {

    private final List<Handler> handlers;

    public void handle(String path, Path filePath, BufferedOutputStream out){
        for (Handler handler: handlers)
            if (handler.isSuitableCase(path)) {
                handler.handle(path, filePath, out);
                break;
            }
    }

    // SingleTone ++
    //multi tread safety

    private HandlersManager(){
        handlers = new ArrayList<>(3);
        handlers.add(new Classic());
        handlers.add(new Handler404());
        handlers.add(new StandartHandler());
    }

    private static class Holder {
        public static final HandlersManager HANDLERS_MANAGER = new HandlersManager();
    }

    public static HandlersManager get()  {
        return HandlersManager.Holder.HANDLERS_MANAGER;
    }

    // SingleTone --

}
