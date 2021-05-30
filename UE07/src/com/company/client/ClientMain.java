package com.company.client;

public class ClientMain {

    public static void main(String[] args) {
		// for test purposes:
		// C:\Users\haslh\Documents\JKU\UniversityJava\Watch
		// C:\Users\haslh\Documents\JKU\UniversityJava\Save
        // C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\ServerSave
        // true
        boolean isAsync = false;
        // the forth argument indicates if client is async or not
        if(args[3].equals("1")) {
            isAsync = true;
        }
        Client client = new Client(args[0], args[1], args[2], isAsync);
        client.start();
    }
}
