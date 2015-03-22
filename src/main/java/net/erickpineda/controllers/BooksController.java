package net.erickpineda.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class BooksController implements Initializable {
	@SuppressWarnings("rawtypes")
	@FXML
	private ComboBox libroExistenteID;
	@FXML
	private Label pantallaResultadoID;

	// Event Listener on Button.onAction
	@FXML
	public void insertarLibro(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir un fichero de texto plano");

		ExtensionFilter soloTXT = new ExtensionFilter(
				"Solo ficheros TXT (*.txt)", "*.txt");

		fileChooser.getExtensionFilters().add(soloTXT);
		fileChooser.setInitialDirectory(new File("."));
		File textoPlano = fileChooser.showOpenDialog(new Stage());

		if (textoPlano != null) {
			pantallaResultadoID.setText(textoPlano.getName());
		}
	}

	// Event Listener on ComboBox[#libroExistenteID].onAction
	@FXML
	public void leerExistente(ActionEvent event) {

	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		// ConectionDB conecta = new ConectionDB("books.db", "admin", "");
		// conecta.conectarBD();

		// Siempre que la tabla no exista, creará una nueva
		// conecta.insertarDatos("CREATE TABLE IF NOT EXISTS TEST("
		// + "ID INT PRIMARY KEY, NAME VARCHAR)");

		// conecta.consultarDatos("SHOW TABLES;");
		// conecta.consultarDatos("SELECT * FROM TEST;");

		// conecta.desconectarBD();

	}
}
