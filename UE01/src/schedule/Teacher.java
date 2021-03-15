package schedule;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Teacher implements Comparable<Teacher> {

	public final String name;
	public final Set<Subject> subjects;
	
	public Teacher(String name, Subject...subjects) {
		this.name = name; 
		this.subjects = new TreeSet<>(Arrays.asList(subjects));
	}

	@Override
	public int compareTo(Teacher other) {
		return this.name.compareToIgnoreCase(other.name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Teacher)) return false;
		Teacher teacher = (Teacher) o;
		return this.name.equals(teacher.name) &&
				this.subjects.equals(teacher.subjects);
	}

	@Override
	public String toString() {
		return "(Name: " + this.name + ", Subjects: " + this.subjects.toString() + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, subjects);
	}
}
