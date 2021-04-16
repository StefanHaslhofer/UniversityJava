package gradeTable.model;


import backend.DemoDBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeTableModel {

    private final ObservableList<Results> results;
    private final DemoDBManager dbManager;

    public GradeTableModel() {
        this.dbManager = new DemoDBManager();
        this.results = this.initData();
    }

    public ObservableList<Results> getResults() {
        return results;
    }

    public void addResult(Student student) {
        this.results.add(new Results(student));
        try {
            dbManager.openConnection(false);
            this.dbManager.addStudent(student.getId(), student.getFirstName(), student.getLastName(), student.getSkz());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dbManager.closeConnection();
    }

    public void removeResult(int index) {
        Results r = results.remove(index);
        try {
            dbManager.openConnection(false);
            this.dbManager.deleteStudent(r.student.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dbManager.closeConnection();
    }

    /**
     * Opens the DB-connection and imports results already in database
     *
     * @return Observable list of results
     */
    private ObservableList<Results> initData() {
        List<Results> results = new ArrayList<>();

        try {
            dbManager.openConnection(false);
            dbManager.printMetadata();

            results = dbManager.importResults();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        dbManager.closeConnection();
        return FXCollections.observableArrayList(results);
    }
}
