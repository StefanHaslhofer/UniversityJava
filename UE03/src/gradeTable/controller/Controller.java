package gradeTable.controller;

import gradeTable.model.GradeTableModel;
import gradeTable.model.Results;
import gradeTable.model.Student;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.util.Arrays;

public class Controller {
    private Stage primaryStage;
    private final GradeTableModel model;

    public Controller() {
        this.model = new GradeTableModel();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private TableView<Results> resultsTableView;

    @FXML
    private Button addBtn;

    @FXML
    private Button removeBtn;

    @FXML
    private void initialize() {
        /* set items as for ListView */
        /* setting items for display */
        ObjectProperty<ObservableList<Results>> items = resultsTableView.itemsProperty();
        items.setValue(this.model.getResults());

        /* create columns */
        ObservableList<TableColumn<Results, ?>> columns = resultsTableView.getColumns();
        TableColumn<Results, String> idColumn = new TableColumn<>("ID");

        /* manually define the property to use */
        idColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, String> param) -> {
                    Student student = param.getValue().student;
                    return student != null ?
                            student.idProperty() :
                            new ReadOnlyStringWrapper("");
                });

        TableColumn<Results, String> firstNameColumn = new TableColumn<>("First");
        firstNameColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, String> param) -> {
                    Student student = param.getValue().student;
                    return student != null ?
                            student.firstNameProperty() :
                            new ReadOnlyStringWrapper("");
                });

        TableColumn<Results, String> lastNameColumn = new TableColumn<>("Name");
        lastNameColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, String> param) -> {
                    Student student = param.getValue().student;
                    return student != null ?
                            student.lastNameProperty() :
                            new ReadOnlyStringWrapper("");
                });

        TableColumn<Results, Number> skzColumn = new TableColumn<>("SN");
        skzColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, Number> param) -> {
                    Student student = param.getValue().student;
                    return student != null ?
                            student.skzProperty() :
                            new SimpleIntegerProperty(0);
                });

        columns.addAll(idColumn, lastNameColumn, firstNameColumn, skzColumn);

        /* enable editing of items inline */
        BooleanProperty enableEditing = resultsTableView.editableProperty();
        enableEditing.setValue(true);


        for (int i = 0; i < Results.NR_ASSIGNMENTS; i++) {
            TableColumn<Results, Number> resultColumn = new TableColumn<>("A" + (i + 1));
            final int finalI = i;
            resultColumn.setCellValueFactory(
                    (TableColumn.CellDataFeatures<Results, Number> param) -> param.getValue().points[finalI]);

            resultColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));

            resultColumn.setOnEditCommit(e -> {
                // only change value if it is either between boundaries or -1
                if (e.getNewValue().intValue() == -1 ||
                        (e.getNewValue().intValue() <= Results.MAX_POINTS && e.getNewValue().intValue() >= Results.MIN_POINTS)) {
                    e.getTableView()
                            .getItems()
                            .get(e.getTablePosition().getRow())
                            .setPoints(e.getTablePosition().getColumn() - 4, e.getNewValue().intValue());
                }

                // refresh table
                resultsTableView.refresh();
            });

            columns.add(resultColumn);
        }

        TableColumn<Results, Number> sumColumn = new TableColumn<>("Sum");
        sumColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, Number> param) -> {
                    Integer sum = Arrays.stream(param.getValue().points)
                            .filter(p -> p.getValue() >= 0)
                            .mapToInt(IntegerExpression::getValue)
                            .sum();
                    return new SimpleIntegerProperty(sum);
                });
        columns.add(sumColumn);

        TableColumn<Results, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(
                (TableColumn.CellDataFeatures<Results, String> param) -> {
                    if (Arrays.stream(param.getValue().points).anyMatch(s -> s.getValue() == -1)) {
                        return new SimpleStringProperty("-");
                    }

                    // count amount of valid results
                    if (Arrays.stream(param.getValue().points)
                            .filter(s -> s.getValue() >= Results.POINTS_VALID)
                            .count() < Results.NR_VALID) {
                        return new SimpleStringProperty("Nicht Genügend");
                    }

                    Integer sum = Arrays.stream(param.getValue().points)
                            .filter(p -> p.getValue() >= 0)
                            .mapToInt(IntegerExpression::getValue)
                            .sum();
                    Double percentage = (double) sum * 100.0 / (Results.MAX_POINTS * Results.NR_ASSIGNMENTS);

                    if (percentage >= 87.5) {
                        return new SimpleStringProperty("Sehr Gut");
                    }
                    if (percentage >= 75.0) {
                        return new SimpleStringProperty("Gut");
                    }
                    if (percentage >= 62.5) {
                        return new SimpleStringProperty("Befriedigend");
                    }
                    if (percentage >= 50.0) {
                        return new SimpleStringProperty("Genügend");
                    }
                    return new SimpleStringProperty("Nicht Genügend");
                });
        columns.add(gradeColumn);

        addBtn.setOnAction(e -> {
            try {

                final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../dialog.fxml"));
                final Parent root1 = fxmlLoader.load();

                // we need to pass the same model in order to add a new result
                DialogController controller = fxmlLoader.getController();
                controller.setModel(this.model);

                Stage stage = new Stage();
                stage.setScene(new Scene(root1));
                stage.initOwner(primaryStage);

                controller.setStage(stage);
                stage.show();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        removeBtn.setOnAction(e -> {
            if (resultsTableView.getSelectionModel().getSelectedIndex() >= 0)
                model.getResults().remove(resultsTableView.getSelectionModel().getSelectedIndex());
        });
    }
}
