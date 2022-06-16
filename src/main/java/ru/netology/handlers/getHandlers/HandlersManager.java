package ru.netology.handlers.getHandlers;

import lombok.Getter;
import ru.netology.Request;
import ru.netology.handlers.Handler;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HandlersManager {

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

    private HandlersManager(){
        handlers = new ArrayList<>(4);
        handlers.add(new Classic());
        handlers.add(new HandlerMessages());
        handlers.add(new Handler404());
        handlers.add(new StandartHandler());
        // сохранил, чтобы потом можно было сюда вернуться и вспомнить рефлексию, если понадобится
//            Map<Integer, Handler> handlers = new TreeMap<>();
//            Set<Class<? extends Handler>> subTypes = new Reflections("ru.netology.handlers").getSubTypesOf(Handler.class);
//            for (Class clazz : subTypes) {
//                try {
//                    Handler handler = (Handler) clazz.getDeclaredConstructor().newInstance();
//                    handlers.put(handler.priority(), handler);
//                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
    }

    private static class Holder {
        public static final HandlersManager HANDLERS_MANAGER = new HandlersManager();
    }

    public static HandlersManager get()  {
        return HandlersManager.Holder.HANDLERS_MANAGER;
    }

    // SingleTone --

}
