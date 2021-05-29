package com.company.client;

import com.company.saver.Changes;
import com.company.saver.FileSaver;
import com.company.saver.FileWatcher;


public class Client {

    private final FileWatcher fileWatcher;
    private final FileSaver fileSaver;
    private final String name;

    public Client(String watchDir, String saveDir, String serverSaveDir) {
        Changes changes = new Changes();

        fileWatcher = new FileWatcher(watchDir, changes);
        fileSaver = new FileSaver(saveDir, serverSaveDir, changes);
        this.name = serverSaveDir;
    }

    public void start() throws InterruptedException {
        System.out.println("Starting client...");
        fileWatcher.startWatching();
        fileSaver.startSaving();
    }
}
