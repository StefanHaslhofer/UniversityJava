package schedule;

import java.util.*;
import java.util.stream.Collectors;

public class School {

    // Sets beacause we don´t want duplicates
    public final Set<Lesson> lessons;
    public final Set<SchoolClass> schoolClasses;
    public final List<Teacher> teachers;


    public School() {
        lessons = new TreeSet<>();
        schoolClasses = new TreeSet<>();
        teachers = new ArrayList<>();
    }

    public void defineClasses(SchoolClass... cls) {
        this.schoolClasses.addAll(Arrays.asList(cls));
    }

    public void defineTeachers(Teacher... ts) {
        this.teachers.addAll(Arrays.asList(ts));
    }

    public void defineLesson(SchoolClass clss, Unit unit, Subject subj) {
        this.lessons.add(new Lesson(clss, unit, subj));
    }

    /**
     * @param scl: class we want to look at
     * @param day: day of week we want to count hours of
     * @return lessons students have to attend school on a certain day
     */
    public Set<Lesson> classUnitsPerDay(SchoolClass scl, Day day) {
        return this.lessons.stream()
                .filter(l -> l.schoolClass.equals(scl) &&
                        l.getUnit().day.equals(day))
                .collect(Collectors.toSet());
    }

    /**
     * @param scl:     class we want to look at
     * @param subject: subject we want to filter the lessons by
     * @return lessons students have to attend of a given subject
     */
    public Set<Lesson> classUnits(SchoolClass scl, Subject subject) {
        return this.lessons.stream()
                .filter(l -> l.schoolClass.equals(scl) &&
                        l.subject.equals(subject))
                .collect(Collectors.toSet());
    }

    /**
     * @param tch: teacher we want to look at
     * @param day: day of week we want to count hours of
     * @return lessons a teacher has to work on a certain day
     */
    public Set<Lesson> teacherUnitsPerDay(Teacher tch, Day day) {
        // The teacher´s subject has to be teached on a certain day and the class must have the teacher as the lecturer in his subject
        return this.lessons.stream()
                .filter(l -> l.getUnit().day.equals(day) &&
                        tch.subjects.contains(l.subject) &&
                        l.schoolClass.subjectTeacherMap.containsValue(tch))
                .collect(Collectors.toSet());
    }

    /**
     * @param tch: teacher we want to look at
     * @param scl: filter by class if given
     * @return lessons a teacher has to work on a certain day
     */
    public Set<Lesson> teacherUnitsPerClass(Teacher tch, SchoolClass scl) {
        // The teacher´s subject has to be teached on a certain day and the class must have the teacher as the lecturer in his subject
        return this.lessons.stream()
                .filter(l -> tch.subjects.contains(l.subject) &&
                        l.schoolClass.subjectTeacherMap.containsValue(tch) &&
                        l.schoolClass.equals(scl))
                .collect(Collectors.toSet());
    }

    /**
     * @param tch:     teacher we want to look at
     * @param subject: subject we want to count hours of
     * @return lessons a teacher has to work for a certain subject
     */
    public Set<Lesson> teacherUnitsPerLesson(Teacher tch, Subject subject) {
        // The teacher´s subject has to be teached on a certain day and the class must have the teacher as the lecturer in his subject
        return this.lessons.stream()
                .filter(l -> tch.subjects.contains(l.subject) &&
                        l.schoolClass.subjectTeacherMap.containsValue(tch) &&
                        l.schoolClass.subjectTeacherMap.containsKey(subject))
                .collect(Collectors.toSet());
    }

    /**
     * @return lessons sorted by teacher and unit --> we don´t use treeset
     * because we want to define a new sortorder
     */
    public List<Lesson> getTeacherLessons() {
        return this.lessons.stream()
                .sorted(Comparator.comparing((Lesson l) -> l.schoolClass
                        .subjectTeacherMap
                        .get(l.subject)
                        .name)
                        .thenComparing(Lesson::getUnit))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof School)) return false;
        School school = (School) o;
        return this.lessons.equals(school.lessons) &&
                this.schoolClasses.equals(school.schoolClasses) &&
                this.teachers.equals(school.teachers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessons, schoolClasses, teachers);
    }

    @Override
    public String toString() {
        return "School: \n" +
                "\tLessons: " + this.lessons.toString() + "\n" +
                "\tClasses:" + this.schoolClasses.toString() + "\n" +
                "\tTeachers:" + this.teachers.toString();
    }
}
