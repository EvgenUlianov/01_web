package ru.netology.handlers;

import ru.netology.Request;

import java.io.BufferedOutputStream;


public interface Handler {
    boolean isSuitableCase(Request request);
    void handle(Request request, BufferedOutputStream out);
}
