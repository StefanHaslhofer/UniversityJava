package com.company.sync;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;

import static com.company.Constants.PORT;

public class SyncServer {
    private volatile boolean terminate = false;
    private final ServerSocket server;

    public SyncServer() throws IOException {
        server = new ServerSocket(PORT);
    }

    void start() throws IOException {
        System.out.println("Starting server...");
        while (!terminate) {
            try {
                System.out.println("Server waiting to accept client");
                Socket clientSocket = server.accept();   // Blocking
                System.out.println("Server accepted client");
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (SocketException se) {
                System.out.println("Server closed with " + se.toString());
            }
        }
    }

    public void terminate() throws IOException {
        terminate = true;
        server.close();
    }

    private class ClientHandler implements Runnable {

        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            } catch (IOException e) {
                System.out.println("Read/write failed: " + e.getMessage());
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
