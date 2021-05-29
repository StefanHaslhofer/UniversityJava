package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT = 9876;

    public static final String OK_RESPONSE = "Files saved on server!";

    // StartOfFile flag: used to tell server that a new File has to be created
    public static final String SOF = "SOF";
    // EndOfFile flag: tells the server that it received all file data
    public static final String EOF = "EOF";
    // EndOfTransmission: the client tells the server that the transmission has ended
    public static final String EOT = "EOT";

    public static String receive(BufferedReader in) throws IOException {
        StringBuilder b = new StringBuilder();
        b.append(in.readLine());
        return b.toString();
    }

    public static void send(PrintWriter out, String str) throws IOException {
        out.println(str);
        out.flush();
    }
}
