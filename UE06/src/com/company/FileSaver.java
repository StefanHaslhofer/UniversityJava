package com.company;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileSaver {

    private final Changes changes;
    public final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final String savePath;


    public FileSaver(String savePath, Changes changes) {
        this.changes = changes;
        this.savePath = savePath;
    }

    public void startSaving() {
        saveDirectoryAtStart();
        try {
            final ScheduledFuture<?> saveFuture =
                    executor.scheduleAtFixedRate(new SaveTask(this.changes, this.savePath), 1, 10, TimeUnit.SECONDS);
        } catch (IOException e) {
            System.out.println("Could not create destination byte channel!");
        }
    }

    /**
     * backup the whole folder at the start of the application
     */
    public void saveDirectoryAtStart() {
        try {
            List<Path> paths = Files.walk(Paths.get(this.changes.fullFilePath), 1).collect(Collectors.toList());
            paths.forEach(p -> {
                try {
                    if (p.getFileName().toString().contains(".txt") || p.getFileName().toString().contains(".xml") ||
                            p.getFileName().toString().contains(".java")) {
                        Files.copy(p, Paths.get(this.savePath + "\\" + p.getFileName()),
                                StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException | InvalidPathException e) {
                    System.out.println("File " + p.toString() + " could not be copied!");
                }
            });
        } catch (Exception ex) {
            System.out.println("Initial directory backup failed!");
        }


    }

    private class SaveTask implements Runnable {
        private Changes syncChanges;
        private final String savePath;

        private SaveTask(Changes changes, String savePath) throws IOException {
            syncChanges = new Changes();
            this.savePath = savePath;
        }

        @Override
        public void run() {
            synchronized (changes.getEvents()) {
                syncChanges.clear();
                Iterator<WatchEvent<Path>> i = changes.getEvents().iterator(); // Must be in synchronized block
                while (i.hasNext()) {
                    syncChanges.addEvent(i.next());
                }
            }
            syncChanges.getEvents().forEach(event -> {
                try {
                    ByteChannel srcChnl = null;
                    try {
                        srcChnl = Files.newByteChannel(Paths.get(changes.fullFilePath + "\\" + event.context().toString()), StandardOpenOption.READ);
                    } catch (NoSuchFileException ex) {
                        System.out.println("File not found. Deleting events for " + event.context().toString());
                        changes.deleteEventsByContext(null, event.context(), null);
                    }

                    ByteChannel destChnl = Files.newByteChannel(Paths.get(savePath + "\\" + event.context().toString()), StandardOpenOption.WRITE,
                            StandardOpenOption.CREATE);

                    if (event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int nRead = srcChnl.read(buffer);
                        while (nRead >= 0) {
                            buffer.flip();
                            destChnl.write(buffer);
                            buffer.clear();
                            nRead = srcChnl.read(buffer);
                        }

                        changes.deleteEventsByContext(ENTRY_CREATE, event.context(), syncChanges.getEvents());
                        // delete all ENTRY_MODIFY events that were already in our local change list
                        // in case of an ENTRY_CREATE or an ENTRY_MODIFY
                        changes.deleteEventsByContext(ENTRY_MODIFY, event.context(), syncChanges.getEvents());
                    } else if (event.kind() == ENTRY_DELETE) {
                        changes.deleteEventsByContext(null, event.context(), null);
                    }
                } catch (Exception e) {
                    System.out.println("Could not process event!");
                    changes.deleteEventsByContext(null, event.context(), null);
                }
            });
        }
    }
}
