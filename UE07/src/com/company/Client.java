package com.company;

import com.company.saver.Changes;
import com.company.saver.FileSaver;
import com.company.saver.FileWatcher;

public class Client {

    private final FileWatcher fileWatcher;
    private final FileSaver fileSaver;

    public Client(String watchDir, String saveDir) {
        Changes changes = new Changes();

        fileWatcher = new FileWatcher(watchDir, changes);
        fileSaver = new FileSaver(saveDir, changes);
    }

    public void init() {
        fileWatcher.startWatching();
        fileSaver.startSaving();
    }
}
