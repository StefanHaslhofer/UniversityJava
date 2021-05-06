package com.company;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {
    private AtomicBoolean stop = new AtomicBoolean(false);
    private final String path;
    private final Changes changes;

    public FileWatcher(String path, Changes changes) {
        this.path = path;
        this.changes = changes;
    }

    public void startWatching() {
        try {
            final Path path = Paths.get(this.path);
            // set the directory in changes object
            this.changes.fullFilePath = this.path;
            final WatchService watcherService = FileSystems.getDefault().newWatchService();
            path.register(watcherService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            Thread watcherThread = new Thread(() -> {
                WatchKey key = null;
                while (!this.stop.get()) {
                    try {
                        key = watcherService.poll(3, TimeUnit.SECONDS);
                        if (key != null) {
                            // we cast events to WatchEvents and add them to our Changes datastructure
                            // if they are coming from a txt, xml or java file
                            changes.getEvents().addAll(key.pollEvents()
                                    .stream()
                                    .filter(evt -> evt.context().toString().contains(".txt") ||
                                            evt.context().toString().contains(".xml") ||
                                            evt.context().toString().contains(".java"))
                                    .map(evt -> (WatchEvent<Path>)evt).collect(Collectors.toList()));
                        }
                    } catch (InterruptedException ie) {
                        System.out.println("WatchService interrupted while waiting!");
                    } finally {
                        if (key != null) key.reset();
                    }
                }
            });
            watcherThread.setDaemon(true);
            watcherThread.start();
        } catch (IOException e) {
            System.out.println("WatchService could not be created!");
        }
    }

    public void stopWatching() {
        this.stop.set(true);
    }
}
