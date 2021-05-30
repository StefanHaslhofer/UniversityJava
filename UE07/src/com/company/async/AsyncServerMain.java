package com.company.async;

import java.io.IOException;

public class AsyncServerMain {
    public static void main(String[] args) {
        AsyncServer asyncServer;
        try {
            asyncServer = new AsyncServer();
            asyncServer.start();
        } catch (IOException e) {
            System.out.println("Server could not be started: " + e.getMessage());
        }
    }
}
