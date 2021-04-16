package gradeTable.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Results {

    public static final int NR_ASSIGNMENTS = 9;
    public static final int NR_VALID = 8;
    public static final int POINTS_VALID = 8;
    public static final int MIN_POINTS = 0;
    public static final int MAX_POINTS = 24;
    public static final int UNDEFINED = -1;

    public final Student student;
    public final IntegerProperty[] points;

    public Results(Student student) {
        this.student = student;
        this.points = new SimpleIntegerProperty[NR_ASSIGNMENTS];
        for(int i = 0; i < points.length; i++) {
            this.points[i] = new SimpleIntegerProperty(UNDEFINED);
        }
    }

    public void setPoints(int idx, int ps) {
        this.points[idx] = new SimpleIntegerProperty(ps);
    }

    public void setPoints(Integer[] points) {
        for(int i = 0; i < points.length; i++) {
            this.points[i] = new SimpleIntegerProperty(points[i]);
        }
    }
}
