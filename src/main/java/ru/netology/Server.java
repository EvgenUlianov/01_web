package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

  final static  private int numberOfTreads = 64;

  public void listen() {

    final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfTreads);

    try (final ServerSocket serverSocket = new ServerSocket(9999)) {
      while (true) {

        Socket socket = serverSocket.accept();
        ServersExecutor serversExecutor = new ServersExecutor(socket);

        threadPool.execute(serversExecutor);

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // SingleTone ++

  private Server(){
  }

  private static class Holder {
    public static final Server SERVER = new Server();
  }

  public static Server get()  {
    return Server.Holder.SERVER;
  }

  // SingleTone --

}


