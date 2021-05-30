package com.company.sync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.company.Protocol.*;

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

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String clientName = receive(in);
                clientName = clientName.substring(LOGIN.length());
                String msg;
                while (!(msg = receive(in)).equals(EOT)) {
                    if (!msg.startsWith(SOF)) {
                        System.out.println(SOF + " expected but received " + msg);
                        return;
                    } else {
                        // the line after SOF needs to be the name
                        Path filePath = Paths.get(clientName, receive(in));
                        // delete file because we can assume the directory is empty when we start saving
                        Files.deleteIfExists(filePath);
                        Files.createFile(filePath);
                        BufferedWriter bw = Files.newBufferedWriter(filePath);
                        // append lines
                        while (!(msg = receive(in)).equals(EOF)) {
                            bw.write(msg+LINE_SEP);
                        }
                        bw.flush();
                        bw.close();
                        System.out.println(filePath.getFileName() + " saved");
                    }
                }
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
