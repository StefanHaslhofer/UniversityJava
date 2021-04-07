package gradeTable.model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GradeTableModel {

    private final ObservableList<Results> results;

    public GradeTableModel() {
        this.results = FXCollections.observableArrayList(TestData.createData());
    }

    public ObservableList<Results> getResults() {
        return results;
    }

    public void addResult(Student student) {
        this.results.add(new Results(student));
    }
}
