package com.company.client;

public class ClientMain {

    public static void main(String[] args) {
		// for test purposes:
		// C:\Users\haslh\Documents\JKU\UniversityJava\Watch
		// C:\Users\haslh\Documents\JKU\UniversityJava\Save
        // C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\ServerSave
    	Client client = new Client("D:\\JKU\\Semester4\\UniversityJava\\Watch", "D:\\JKU\\Semester4\\UniversityJava\\Save", "D:\\JKU\\Semester4\\UniversityJava\\ServerSave");
        try {
            client.start();
        } catch (InterruptedException e) {
            System.out.println("Could not start client");
        }
    }
}
