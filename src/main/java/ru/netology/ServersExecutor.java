package ru.netology;

import ru.netology.handlers.getHandlers.HandlersManager;
import ru.netology.handlers.postHandlers.PostHandlersManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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

            final Request request = Request.getRequest(in);

            if (request != null && request.getMethod().equals(GET)) {
                HandlersManager.get().handle(request, out);
            } else if (request != null && request.getMethod().equals(POST)){

                PostHandlersManager.get().handle(request, out);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
