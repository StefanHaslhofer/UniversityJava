package gradeTable.model;

import java.util.ArrayList;
import java.util.List;

public class TestData {

	public static List<Results> createData() {
		List<Results> data = new ArrayList<>();
		
		Results r1 = new Results(new Student("k01245678", "Mia", "Musterstudentin", 521));
		r1.setPoints(0, 24);
		r1.setPoints(1, 24);
		r1.setPoints(2, 24);
		r1.setPoints(3, 24);
		r1.setPoints(4, 24);
		r1.setPoints(5, 24);
		r1.setPoints(6, 24);
		r1.setPoints(7, 24);
		r1.setPoints(8, 23);
		data.add(r1);
		
		Results r2 = new Results(new Student("k01778901", "Max", "Mustermann", 521));
		r2.setPoints(0, 23);
		r2.setPoints(1, 19);
		r2.setPoints(2, 24);
		r2.setPoints(3, 17);
		r2.setPoints(4, 18);
		r2.setPoints(5, 21);
		r2.setPoints(6, 21);
		r2.setPoints(7, 18);
		r2.setPoints(8, 20);
		data.add(r2);
		
		Results r3 = new Results(new Student("k01612345", "Fred", "Faul", 521));
		r3.setPoints(0, 4);
		r3.setPoints(1, 12);
		r3.setPoints(2, 13);
		r3.setPoints(3, 9);
		r3.setPoints(4, 0);
		r3.setPoints(5, 11);
		r3.setPoints(6, 8);
		r3.setPoints(7, 14);
		r3.setPoints(8, 11);
		data.add(r3);
		
		Results r4 = new Results(new Student("k01612345", "Ulrike", "Unfertig", 521));
		r4.setPoints(0, 18);
		r4.setPoints(1, -1);
		r4.setPoints(2, -1);
		r4.setPoints(3, -1);
		r4.setPoints(4, -1);
		r4.setPoints(5, -1);
		r4.setPoints(6, -1);
		r4.setPoints(7, -1);
		r4.setPoints(8, -1);
		data.add(r4);
		
		return data;
	}
}
