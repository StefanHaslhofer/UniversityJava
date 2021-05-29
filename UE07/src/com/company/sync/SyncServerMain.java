package com.company.sync;

import java.io.IOException;

public class SyncServerMain {
    public static void main(String[] args) {
        SyncServer syncServer;
        try {
            syncServer = new SyncServer();
            syncServer.start();
        } catch (IOException e) {
            System.out.println("Server could not be started: " + e.getMessage());
        }
    }
}
