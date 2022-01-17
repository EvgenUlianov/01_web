package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.netology.handlers.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

public class ServersExecutor implements Runnable{

    private final Socket socket;

    public ServersExecutor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                socket;
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final String requestLine = in.readLine();
            if (requestLine == null)
                return;

            char [] separators = {' ', '?'};
            List<NameValuePair> nameValuePairs =  URLEncodedUtils.parse(requestLine, Charset.defaultCharset(), separators);//forName("UTF-8"));

            if (nameValuePairs.size() < 2) {
                // just close socket
                return;
            }
            final String path = nameValuePairs.get(1).getName();

            final Path filePath = Path.of(".", "public", path);

            HandlersManager.get().handle(path, filePath, out);

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
