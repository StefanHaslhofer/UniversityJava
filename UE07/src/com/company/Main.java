package com.company;

import com.company.sync.SyncServer;

public class Main {

    public static void main(String[] args) {
		// for test purposes:
		// C:\Users\haslh\Documents\JKU\UniversityJava\Watch
		// C:\Users\haslh\Documents\JKU\UniversityJava\Save
    	Client client = new Client("C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Watch", "C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Save");
		SyncServer syncServer = new SyncServer();
    }
}
