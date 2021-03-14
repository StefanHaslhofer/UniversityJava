package schedule;

public enum Unit {
	
	Mon1(Day.Mon, 1), Mon2(Day.Mon, 2), Mon3(Day.Mon, 3), Mon4(Day.Mon, 4), 
	Mon5(Day.Mon, 5), Mon6(Day.Mon, 6), Mon7(Day.Mon, 7), Mon8(Day.Mon, 8), 
	
	Tue1(Day.Tue, 1), Tue2(Day.Tue, 2), Tue3(Day.Tue, 3), Tue4(Day.Tue, 4), 
	Tue5(Day.Tue, 5), Tue6(Day.Tue, 6), Tue7(Day.Tue, 7), Tue8(Day.Tue, 8), 
	
	Wen1(Day.Wen, 1), Wen2(Day.Wen, 2), Wen3(Day.Wen, 3), Wen4(Day.Wen, 4), 
	Wen5(Day.Wen, 5), Wen6(Day.Wen, 6), Wen7(Day.Wen, 7), Wen8(Day.Wen, 8),
	
	Thu1(Day.Thu, 1), Thu2(Day.Thu, 2), Thu3(Day.Thu, 3), Thu4(Day.Thu, 4), 
	Thu5(Day.Thu, 5), Thu6(Day.Thu, 6), Thu7(Day.Thu, 7), Thu8(Day.Thu, 8), 
	
	Fri1(Day.Fri, 1), Fri2(Day.Fri, 2), Fri3(Day.Fri, 3), Fri4(Day.Fri, 4), 
	Fri5(Day.Fri, 5), Fri6(Day.Fri, 6), Fri7(Day.Fri, 7), Fri8(Day.Fri, 8);  
	
	public final Day day; 
	public final int hour; 
	
	private Unit(Day day, int hour) {
		this.day = day;
		this.hour = hour;
	}

	public Day getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	
}
