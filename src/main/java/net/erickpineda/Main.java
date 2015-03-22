package net.erickpineda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = FXMLLoader.load(getClass().getResource(
					"views/Books.fxml"));

			Scene scene = new Scene(root, 470, 440, Color.BLACK);
			scene.getStylesheets().add(
					getClass().getResource("content/css/application.css")
							.toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("¡Books Funcionando!");

			primaryStage.getIcons().add(
					new Image(getClass().getResource(
							"content/images/mantis.png").toExternalForm()));

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}