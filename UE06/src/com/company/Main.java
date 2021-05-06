package com.company;

public class Main {

    public static void main(String[] args) {
    	Changes changes = new Changes();
	    FileWatcher fileWatcher = new FileWatcher("C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Watch", changes);
	    FileSaver fileSaver = new FileSaver("C:\\Users\\haslh\\Documents\\JKU\\UniversityJava\\Save", changes);

	    fileWatcher.startWatching();
	    fileSaver.startSaving();
    }
}
