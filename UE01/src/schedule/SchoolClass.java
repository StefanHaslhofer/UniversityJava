package schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchoolClass implements Comparable<SchoolClass> {

	public final Integer level;
	public final String name;
	// sets because we don´t want duplicates
	public final Map<Subject, Teacher> subjectTeacherMap;

	public SchoolClass(int n, String name) {
		this.level = n;
		this.name = name;

		this.subjectTeacherMap = new HashMap<>();
	}

	public void defineSubject(Subject subject, Teacher teacher) {
		this.subjectTeacherMap.put(subject, teacher);
	}

	@Override
	public int compareTo(SchoolClass schoolClass) {
		if(this.level.compareTo(schoolClass.level) != 0) {
			return this.level.compareTo(schoolClass.level);
		}

		if(this.name.compareToIgnoreCase(schoolClass.name) != 0) {
			return this.name.compareToIgnoreCase(schoolClass.name);
		}

		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SchoolClass)) return false;
		SchoolClass that = (SchoolClass) o;
		return level.equals(that.level) &&
				this.name.equals(that.name) &&
				this.subjectTeacherMap.equals(that.subjectTeacherMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, name, subjectTeacherMap);
	}

	@Override
	public String toString() {
		String s = "(Name: " + this.name + ", Teacher Subjects: [ ";

		StringBuilder builder = new StringBuilder().append(s);
		this.subjectTeacherMap.forEach((key, value) -> {
			builder.append(key.toString()).append("=").append(value.name).append(" ");
		});

		builder.append("])");
		return builder.toString();
	}
}
