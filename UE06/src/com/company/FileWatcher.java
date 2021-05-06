package com.company;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {
    private boolean stopWatcher = false;
    private final String path;

    public FileWatcher(String path) {
        this.path = path;
    }

    public void startWatching() {
        this.stopWatcher = false;
        try {
            final WatchService watcherService = FileSystems.getDefault().newWatchService();

            Thread watcherThread = new Thread(() -> {
                WatchKey key = null;
                while (!stopWatcher) {
                    try {
                        key = watcherService.take();
                        for (WatchEvent<?> evt : key.pollEvents()) {
                            WatchEvent<Path> pevt = (WatchEvent<Path>) evt;
                            Path relPath = pevt.context();
                            Path dirPath = (Path) key.watchable();
                            Path absPath = dirPath.resolve(relPath);
                            if (pevt.kind() == ENTRY_CREATE) {
                                if (Files.isDirectory(absPath)) {
                                    try {
                                        WatchKey k = absPath.register(watcherService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                                    } catch (Exception e) {

                                    }
                                }
                            } else if (pevt.kind() == ENTRY_MODIFY) {
                            } else if (pevt.kind() == ENTRY_DELETE) {
                            }
                        }
                    } catch(InterruptedException ie) {
                        System.out.println("WatchService interrupted while waiting!");
                    } finally {
                        if (key != null) key.reset();
                    }
                }
            });
            watcherThread.start();
        } catch (IOException e) {
            System.out.println("WatchService could not be created!");
        }
    }

    public void stopWatching() {
        this.stopWatcher = true;
    }
}
