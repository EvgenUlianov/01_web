package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.netology.handlers.*;
import ru.netology.postHandlers.PostHandlersManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServersExecutor implements Runnable{

    private final Socket socket;
    private static final String POST = "POST";
    private static final String GET = "GET";

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

            char [] separators = {' ', '?', '&'};
            List<NameValuePair> nameValuePairs =  URLEncodedUtils.parse(requestLine, Charset.defaultCharset(), separators);//forName("UTF-8"));

            if (nameValuePairs.size() < 2) {
                // just close socket
                return;
            }

            final String method = nameValuePairs.get(0).getName();
            final String path = nameValuePairs.get(1).getName();
            if (method.equals(GET)) {
                final Path filePath = Path.of(".", "public", path);
                HandlersManager.get().handle(path, filePath, out);
            } else if (method.equals(POST)){


                Map<String, String> pathParams = new HashMap<>();
                for (int i = 2; i < nameValuePairs.size() - 1; i++) {
                    pathParams.put(nameValuePairs.get(i).getName(),nameValuePairs.get(i).getValue());
                }

                StringBuilder builderHeaders = new StringBuilder();
                StringBuilder builderBody = new StringBuilder();
                boolean addingToHeaders = true;
                char [] separatorsForHeadersBody = {'\n'};
                String requestBody = in.readLine();
                int a =1;
                while(requestBody != null && !requestBody.isEmpty()){
                        if (addingToHeaders && requestBody.isEmpty())
                            addingToHeaders = false;
                        List<NameValuePair> headersBodyValuePairs =  URLEncodedUtils.parse(requestBody, Charset.defaultCharset(), separatorsForHeadersBody);
                        if (addingToHeaders){
                            for (NameValuePair headersBodyValuePair:headersBodyValuePairs) {
                                builderHeaders.append(headersBodyValuePair.toString());
                                builderHeaders.append('\n');
                            }
                        } else {
                            for (NameValuePair headersBodyValuePair:headersBodyValuePairs) {
                                builderBody.append(headersBodyValuePair.toString());
                                builderBody.append('\n');
                            }
                        }
                        if(!socket.isClosed()){
                            requestBody = in.readLine();
                            System.out.println(++a);
                        } else{
                            break;
                        }

                    }


                PostHandlersManager.get().handle(path, pathParams, builderHeaders.toString(), builderBody.toString(), out);
            }

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
