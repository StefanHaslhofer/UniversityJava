package gradeTable;

import gradeTable.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("gradeTableUI.fxml"));
		final Parent root = loader.load();
		Controller controller = loader.getController();
		controller.setPrimaryStage(primaryStage);

		primaryStage.setTitle("Grade Table");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
