package com.company;

public class Main {

    public static void main(String[] args) {
    	Changes changes = new Changes();
    	// for test purposes:
		// C:\Users\haslh\Documents\JKU\UniversityJava\Watch
		// C:\Users\haslh\Documents\JKU\UniversityJava\Save
	    FileWatcher fileWatcher = new FileWatcher(args[0], changes);
	    FileSaver fileSaver = new FileSaver(args[1], changes);

	    fileWatcher.startWatching();
	    fileSaver.startSaving();
    }
}
