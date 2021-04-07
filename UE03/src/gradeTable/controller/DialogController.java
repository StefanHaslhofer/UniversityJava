package gradeTable.controller;

import gradeTable.model.GradeTableModel;
import gradeTable.model.Student;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class DialogController {
    private GradeTableModel model;
    private Stage stage;

    public DialogController() {

    }

    public void setModel(GradeTableModel model) {
        this.model = model;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField studentIdInput;

    @FXML
    private TextField studentNameInput;

    @FXML
    private TextField studentFirstNameInput;

    @FXML
    private ChoiceBox<Integer> studyNumberInput;

    @FXML
    private Button addBtn;

    @FXML
    private Label errorMsg;

    @FXML
    public void initialize() {
        errorMsg.setVisible(false);
        studyNumberInput.setItems(FXCollections.observableArrayList(420, 521, 569));

        addBtn.setOnAction(e -> {
            // display error message if one of the fields is empty
            // otherwise add student to result list
            if (this.studentIdInput.textProperty().isEmpty().get() ||
                    this.studentNameInput.textProperty().isEmpty().get() ||
                    this.studentFirstNameInput.textProperty().isEmpty().get() ||
                    this.studyNumberInput.getValue() == null) {
                errorMsg.setVisible(true);
            } else {
                Student student = new Student(this.studentIdInput.textProperty().get(),
                        this.studentNameInput.textProperty().get(),
                        this.studentFirstNameInput.textProperty().get(),
                        this.studyNumberInput.getValue());
                model.addResult(student);

                // close popup after insert
                this.stage.close();
            }
        });
    }
}
