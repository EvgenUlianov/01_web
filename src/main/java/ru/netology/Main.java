package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  final static  private int numberOfTreads = 64;

  public static void main(String[] args) {

    final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfTreads);


    try (final ServerSocket serverSocket = new ServerSocket(9999)) {
      while (true) {

        Socket socket = serverSocket.accept();
        Server server = new Server(socket);

        threadPool.execute(server);

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


