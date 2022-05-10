package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import ru.netology.handlers.*;
import ru.netology.postHandlers.PostHandlersManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

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


                Request request = new Request(path);

//                Map<String, String> pathParams = new HashMap<>();
                for (int i = 2; i < nameValuePairs.size() - 1; i++) {
                    request.setQuerryParam(nameValuePairs.get(i).getName(),nameValuePairs.get(i).getValue());
//                    System.out.printf("%d. %s \n",i, nameValuePairs.get(i).toString());
                }
                System.out.println("//");

//                StringBuilder builderHeaders = new StringBuilder();
                StringBuilder builderBody = new StringBuilder();
                boolean addingToHeaders = true;
                char [] separatorsForHeadersBody = {'\n'};
//                char [] separatorsForHeadersBody = {':'};
                NameValuePair requestNextHeader = inReadHeader(in);
                int a =1;
                while(requestNextHeader != null
                        && !requestNextHeader.getName().isEmpty()){// && !requestBody.isEmpty()
                    request.setHeader(requestNextHeader.getName(), requestNextHeader.getValue());
                    if(!socket.isClosed() && in.ready()){
                        try {
                            requestNextHeader = inReadHeader(in);
//                                requestBody = in.readLine();
                            if (requestNextHeader != null)
                                System.out.printf("%d. %s \n",a++, requestNextHeader.toString());
                        } catch  (IOException e) {
                            System.out.println("Exception readLine");
                            e.printStackTrace();
                            break;
                        }
                    } else{
                        System.out.println("Breacked");
                        break;
                    }

                }
                request.setBody(inReadBody(in, request.getContentLength()));

                PostHandlersManager.get().handle(request, out);
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

    private NameValuePair inReadHeader(BufferedReader in) throws IOException{
        String key;
        String value;

        ArrayList<Character> characterArrayKey = new ArrayList<>();
        ArrayList<Character> characterArrayValue = new ArrayList<>();
        boolean addToKey = true;
        boolean addToValue = false;
        final char separator = ':';

        char nextChar = (char) in.read();
        if (nextChar ==-1)
            return null;
        while (nextChar !=-1
                && nextChar!= '\n'){


            if (!addToKey && !addToValue && nextChar != separator)
                addToValue = true;
            if (addToKey && nextChar == separator)
                addToKey = false;

            if (addToKey)
                characterArrayKey.add(nextChar);
            if (addToValue)
                characterArrayValue.add(nextChar);
            nextChar = (char) in.read();
        }
        if (characterArrayKey.isEmpty()
                && characterArrayValue.isEmpty())
            return null;
        if (characterArrayKey.size() == 1
                && characterArrayKey.get(0) == '\r'
                && characterArrayValue.isEmpty())
            return null;

        char [] charArrayKey = new char[characterArrayKey.size()];
        for (int i = 0; i < characterArrayKey.size(); i++)
            charArrayKey[i] = characterArrayKey.get(i);
        key = String.valueOf(charArrayKey);
        char [] charArrayValue = new char[characterArrayValue.size()];
        for (int i = 0; i < characterArrayValue.size(); i++)
            charArrayValue[i] = characterArrayValue.get(i);
        value = String.valueOf(charArrayValue);
        return new BasicNameValuePair(key, parseURL(value));

    }

    private String inReadBody(BufferedReader in, int contentLength) throws IOException{
        String requestBody;
        if (contentLength == 0)
            return "";
        ArrayList<Character> characterArray = new ArrayList<>();
        char nextChar = (char) in.read();
        int index = 0;
        while (nextChar !=-1){
            characterArray.add(nextChar);
            index++;
            if(index == contentLength)
                break;
            nextChar = (char) in.read();
        }
        char [] charArray = new char[characterArray.size()];
        for (int i = 0; i < characterArray.size(); i++)
            charArray[i] = characterArray.get(i);
        requestBody = String.valueOf(charArray);
        return requestBody;
    }

    private String parseURL(String unparsed){
        final char [] separators = {'\n'};
        List<NameValuePair> headersBodyValuePairs =  URLEncodedUtils.parse(unparsed, Charset.defaultCharset(), separators);
        StringBuilder builder = new StringBuilder();

        boolean isFirst = true;
        for (NameValuePair headersBodyValuePair:headersBodyValuePairs) {
            if (isFirst){
                isFirst= false;
            }
            else{
                builder.append('\n');
            }
            builder.append(headersBodyValuePair.toString());
        }
        return  builder.toString();
    }


}
