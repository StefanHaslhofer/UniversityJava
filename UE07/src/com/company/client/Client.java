package com.company.client;

import com.company.saver.Changes;
import com.company.saver.FileSaver;
import com.company.saver.FileWatcher;


public class Client {

    private final FileWatcher fileWatcher;
    private final FileSaver fileSaver;

    public Client(String watchDir, String saveDir, String serverSaveDir, boolean isAsync) {
        Changes changes = new Changes();

        fileWatcher = new FileWatcher(watchDir, changes);
        fileSaver = new FileSaver(saveDir, serverSaveDir, changes, isAsync);
    }

    public void start() {
        System.out.println("Starting client...");
        fileWatcher.startWatching();
        fileSaver.startSaving();
    }
}
