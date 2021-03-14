package schedule;

import java.util.Objects;

public class Lesson implements Comparable<Lesson> {

	public final SchoolClass schoolClass;
	public final Subject subject;
	// not final because of possible timetable changes
	private Unit unit;

	public Lesson(SchoolClass schoolClass, Unit unit, Subject subject) {
		this.schoolClass = schoolClass;
		this.unit = unit;
		this.subject = subject;
	}

	@Override
	public int compareTo(Lesson o) {
		if(this.schoolClass.compareTo(o.schoolClass) != 0) {
			return this.schoolClass.compareTo(o.schoolClass);
		}

		return this.unit.compareTo(o.unit);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Lesson lesson = (Lesson) o;
		return this.schoolClass.equals(lesson.schoolClass) &&
				this.subject.equals(lesson.subject) &&
				this.unit.equals(lesson.unit);
	}

	@Override
	public String toString() {
		String teacherName = this.schoolClass.subjectTeacherMap
				.get(this.subject).name;
		return "(Class: " + this.schoolClass.name +
				", Subject: " + this.subject.toString() +
				", Unit: " + this.unit.toString() +
				", Teacher: "+ teacherName + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(schoolClass, subject, unit);
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}
