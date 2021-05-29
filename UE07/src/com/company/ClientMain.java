package com.company;

import com.company.sync.SyncServer;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
		// for test purposes:
		// C:\Users\haslh\Documents\JKU\UniversityJava\Watch
		// C:\Users\haslh\Documents\JKU\UniversityJava\Save
        // C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\ServerSave
    	Client client = new Client("C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Watch", "C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Save", "C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\ServerSave");
        try {
            client.start();
        } catch (InterruptedException e) {
            System.out.println("Could not start client");
        }
    }
}
