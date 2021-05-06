package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileSaver {
    List<Changes> changeList = new ArrayList<>();

    public void save() {
        // we have to hold the lock during traversal
        // to ensure the list is not locked during the entire backup process we first copy the elements to another local
        // change list
        synchronized (Changes.files) {
            Iterator i = Changes.files.iterator(); // Must be in synchronized block
            while (i.hasNext()){

            }
        }
    }


}
