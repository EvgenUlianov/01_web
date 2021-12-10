package ru.netology;

import lombok.Data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Data
public class Request {

    private String path;

    private final BufferedReader in;

    private Request(BufferedReader in){
        this.in = in;
    }

    static public Request getRequest(BufferedReader in){
        Request request = new Request(in);
        try (in;) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final String requestLine = in.readLine();
            final String[] parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return null;
            }

            request.path = parts[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }
}
