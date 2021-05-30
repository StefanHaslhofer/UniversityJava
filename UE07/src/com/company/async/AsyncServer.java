package com.company.async;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static com.company.Protocol.*;

public class AsyncServer {
    private static final int TIMEOUT = 10000;
    private static Charset CSET = Charset.forName("UTF-8");
    private volatile boolean terminate = false;
    private final ServerSocketChannel server;

    private Selector selector;
    private Thread selectorThread;

    public AsyncServer() throws IOException {
        server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(PORT));
        selector = Selector.open();
    }

    void start() {

        System.out.println("Starting server...");

        // selector
        this.selectorThread = new Thread(new SelectorRunnable());
        selectorThread.start();

        // accept client loop
        while (!terminate) {
            SocketChannel clientChannel = null;
            try {
                System.out.println("Server waiting to accept client");
                clientChannel = server.accept(); // blocking
                System.out.println("Server accepted client");
                clientChannel.configureBlocking(false);
                ClientHandler handler = new ClientHandler(clientChannel);
                SelectionKey key = clientChannel.register(selector, SelectionKey.OP_READ);
                key.attach(handler);
                selector.wakeup();
            } catch (AsynchronousCloseException ace) {
                terminate = true;
            } catch (IOException e) {
                System.out.println("ERROR in accepting client " + e.toString());
            }
        }
    }

    private class SelectorRunnable implements Runnable {

        @Override
        public void run() {
            while (!terminate) {
                try {
                    selector.select(TIMEOUT);
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keysIt = selectedKeys.iterator();
                    while (keysIt.hasNext()) {
                        SelectionKey key = keysIt.next();
                        if (key.isReadable()) {
                            ClientHandler handler = (ClientHandler) key.attachment();
                            handler.handleMessage(key);
                        }
                        keysIt.remove();
                    }
                } catch (Exception e) {
                    System.out.println("Exception in select " + e.toString());
                }
            }
        }
    }

    enum State {
        START, LOGGED_IN
    }

    private class ClientHandler {

        private final SocketChannel channel;
        private final ByteBuffer buffer;
        private State state;
        private String clientName;

        ClientHandler(SocketChannel channel) {
            super();
            this.channel = channel;
            this.buffer = ByteBuffer.allocate(100000);
            this.state = State.START;
        }

        private void handleMessage(SelectionKey key) throws IOException {
            String msg = readMessage();
            if (msg.startsWith(LOGOUT)) {
                key.cancel();
                channel.close();
                System.out.println("Logout");
                return;
            }
            switch (state) {
                case START:
                    if (!msg.startsWith(LOGIN)) {
                        channel.close();
                        return;
                    }
                    clientName = msg.substring(LOGIN.length());

                    state = State.LOGGED_IN;
                    break;
                case LOGGED_IN:
                    if (!msg.isEmpty()) {
                        Iterator<String> iterator = Arrays.asList(msg.split(LINE_SEP)).iterator();
                        while (!(msg = iterator.next()).equals(EOT)) {
                            if (!msg.startsWith(SOF)) {
                                System.out.println(SOF + " expected but received " + msg);
                                return;
                            } else {
                                // the line after SOF needs to be the name
                                Path filePath = Paths.get(clientName, iterator.next());
                                // delete file because we can assume the directory is empty when we start saving
                                Files.deleteIfExists(filePath);
                                Files.createFile(filePath);
                                BufferedWriter bw = Files.newBufferedWriter(filePath);
                                // append lines
                                while (!(msg = iterator.next()).equals(EOF)) {
                                    bw.write(msg + LINE_SEP);
                                }
                                bw.flush();
                                bw.close();
                                System.out.println(filePath.getFileName() + " saved");
                            }
                        }
                    }
            }
        }

        private String readMessage() throws IOException {
            buffer.clear();
            channel.read(buffer);
            buffer.flip();
            String data = CSET.decode(buffer).toString();
            return data;
        }
    }
}
