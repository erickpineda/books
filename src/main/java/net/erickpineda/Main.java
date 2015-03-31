package net.erickpineda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.erickpineda.controllers.BooksController;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// BorderPane root = FXMLLoader.load(getClass().getResource(
			// "views/Books.fxml"));

			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"views/Books.fxml"));

			Parent root = loader.load();
			
			Scene scene = new Scene(root, Color.BLACK);
			scene.getStylesheets().add(
					getClass().getResource("content/css/application.css")
							.toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("¡Books Funcionando!");

			primaryStage.getIcons().add(
					new Image(getClass().getResource(
							"content/images/mantis.png").toExternalForm()));
			
			
			BooksController mc = loader.getController();
			mc.setStage(primaryStage);

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}