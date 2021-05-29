package com.company;

import com.company.saver.Changes;
import com.company.saver.FileSaver;
import com.company.saver.FileWatcher;


import java.io.*;
import java.net.Socket;

import static com.company.Constants.PORT;
import static com.company.Constants.SERVER;


public class Client {

    private final FileWatcher fileWatcher;
    private final FileSaver fileSaver;
    private final String name;

    public Client(String watchDir, String saveDir, String serverSaveDir) {
        Changes changes = new Changes();

        fileWatcher = new FileWatcher(watchDir, changes);
        fileSaver = new FileSaver(saveDir, changes);
        this.name = serverSaveDir;
    }

    public void start() throws InterruptedException {
        System.out.println("Starting client...");
        fileWatcher.startWatching();
        fileSaver.startSaving();
    }
}
