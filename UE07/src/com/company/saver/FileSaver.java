package com.company.saver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.company.Protocol.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileSaver {

    private final Changes changes;
    public final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final String savePath;
    private final String serverSaveDir;
    private final boolean isAsync;


    public FileSaver(String savePath, String serverSaveDir, Changes changes, boolean isAsync) {
        this.changes = changes;
        this.savePath = savePath;
        this.serverSaveDir = serverSaveDir;
        this.isAsync = isAsync;
    }

    public void startSaving() {
        saveDirectoryAtStart();
        try {
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


            // start saving to the server after the files have been written to the local directory
            try {
                if (isAsync) {
                    saveToServerAsync();
                } else {
                    saveToServer();
                }
            } catch (Exception ex) {
                System.out.println("Could not start communication with server " + SERVER + ":" + PORT);
            }
        }
    }


    private void saveToServer() {
        File[] files = new File(savePath).listFiles();

        try (Socket socket = new Socket(SERVER, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream())) {
            sendData(out, files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToServerAsync() {
        File[] files = new File(savePath).listFiles();
        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(SERVER, PORT));
            PrintWriter out = new PrintWriter(channel.socket().getOutputStream());
            channel.write(ByteBuffer.wrap((LOGIN + this.serverSaveDir).getBytes()));
            StringBuilder data = new StringBuilder();
            for (File file : files) {
                List<String> lines = Files.readAllLines(file.toPath());
                // the next line has to be the filename
                data.append(SOF).append(LINE_SEP).append(file.getName()).append(LINE_SEP);
                for (String line : lines) {
                    data.append(line).append(LINE_SEP);
                }
                data.append(EOF).append(LINE_SEP);
                System.out.println(file.getName() + " sent to " + SERVER + ":" + PORT);
            }
            data.append(EOT);
            channel.write(ByteBuffer.wrap(data.toString().getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(PrintWriter out, File[] files) throws IOException {
        // the clients name is the first message
        send(out, LOGIN + this.serverSaveDir);
        // iterate over files in directory
        for (File file : files) {
            List<String> lines = Files.readAllLines(file.toPath());
            send(out, SOF);
            // the next line has to be the filename
            send(out, file.getName());
            for (String line : lines) {
                send(out, line);
            }
            send(out, EOF);
            System.out.println(file.getName() + " sent to " + SERVER + ":" + PORT);
        }
        send(out, EOT);
    }
}
