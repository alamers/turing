package nl.aardbeitje.turing.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ProgramPane.fxml"));
	        Parent root = fxmlLoader.load();
	        MainController controller = fxmlLoader.getController();
	        controller.setStage(stage);
	        controller.initialize();
			
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	 
			stage.setTitle("FXML Welcome");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
