package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Changes {
    public static List<String> files = Collections.synchronizedList(new ArrayList<String>());
}
