package net.erickpineda.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class BooksController implements Initializable {
	/**
	 * Lista desplegable, de los idiomas existentes del programa.
	 */
	@FXML
	private ComboBox<String> escogerIdiomaExistenteID;
	/**
	 * Stage de la clase principal, no me acuerdo para que lo puse de esta
	 * forma.
	 */
	private Stage myStage;
	/**
	 * Gráfico Barchart a generar a partir de las letras insertadas.
	 */
	@FXML
	private BarChart<String, Integer> barChart;
	/**
	 * Categoria a la que pertenecerá en el barchart.
	 */
	@FXML
	private CategoryAxis xAxis;
	/**
	 * Recoge el idioma a almacenar en la base de datos.
	 */
	@FXML
	private TextField textIdiomaID;
	/**
	 * Colección de caracteres para mostrar luego en el barchart.
	 */
	private ObservableList<String> caracteres = FXCollections
			.observableArrayList();
	/**
	 * Conector hacia la BDD.
	 */
	private static ConectionDB conecta;
	/**
	 * Mapa que contiene los caracteres y las veces que se repiten.
	 */
	private Map<Character, Integer> mapa;
	/**
	 * Semáforo para comprobar que creará o mostrará un libro.
	 */
	private boolean Ok = false;

	/**
	 * Inicializador del controlador.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		conecta = new ConectionDB("books", "admin", "");
		conecta.conectarBD();
		CrearTablaBDD();
		mostrarIdiomas();
		xAxis.setCategories(caracteres);
	}

	/**
	 * Event Listener on MenuItem.onAction
	 * 
	 * @param event
	 *            Evento que se dispara cuando se hace click.
	 */
	@FXML
	public void acercaDe(ActionEvent event) {
		infoAcerca();
	}

	/**
	 * Método que rellenará el barchart a partir de unas coordenadas.
	 */
	public void rellenaChart() {
		if (isOk() == true) {
			barChart.getData().clear();
			caracteres.clear();

			for (Map.Entry<Character, Integer> m : mapa.entrySet()) {
				caracteres.add(new String(Character.toString(m.getKey())));
			}
			setCaracteres(caracteres);
			Ok = false;
		}
	}

	/**
	 * Método que permite cambiar la lista de caracteres mostrar.
	 * 
	 * @param carac
	 *            Parámetro de lista para cambiar.
	 */
	public void setCaracteres(List<String> carac) {
		List<Integer> enteros = new ArrayList<Integer>();

		for (Map.Entry<Character, Integer> m : mapa.entrySet()) {
			enteros.add(new Integer(m.getValue()));
		}

		// Create a XYChart.Data object for each month. Add it to the series.
		for (int i = 0; i < carac.size(); i++) {
			XYChart.Series<String, Integer> series = new XYChart.Series<>();

			series.getData().add(
					new XYChart.Data<>(caracteres.get(i), enteros.get(i)));

			barChart.getData().add(series);
		}

	}

	/**
	 * Event Listener on Button.onAction
	 * 
	 * @param event
	 *            Evento que se dispara cuando se hace click.
	 */
	@FXML
	public void botonAbrir(ActionEvent event) {
		if (textIdiomaID.getCharacters().length() > 0)
			abrirFichero();
		else
			mensajeError();
	}

	/**
	 * Event Listener on Button.onAction
	 * 
	 * @param event
	 *            Evento que se dispara cuando se hace click.
	 */
	@FXML
	public void botonMostrar(ActionEvent event) {

	}

	/**
	 * Método que abrirá el fichero de texto.
	 */
	private void abrirFichero() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir un fichero de texto plano");

		ExtensionFilter soloTXT = new ExtensionFilter(
				"Solo ficheros TXT (*.txt)", "*.txt");

		fileChooser.getExtensionFilters().add(soloTXT);
		fileChooser.setInitialDirectory(new File("."));
		File textoPlano = fileChooser.showOpenDialog(myStage);

		if (textoPlano != null) {
			Acciones actua = new Acciones(textoPlano, conecta);
			actua.leerFichero();
			actua.insertarDatosTabla(textIdiomaID.getText());
			mapa = actua.getMapa();
			Ok = true;
		}
		rellenaChart();
	}

	/**
	 * Método que creará la tabla en la BDD según exista o no.
	 * 
	 * @return Retorna un String con la tabla.
	 */
	private static String CrearTablaBDD() {
		return conecta.insertarDatos("CREATE TABLE IF NOT EXISTS Books("
				+ "id int PRIMARY KEY auto_increment,"
				+ "letra VARCHAR(1) NOT NULL," + "idioma VARCHAR(20) NOT NULL,"
				+ "vecesRepetida INT NULL," + "frecuencia VARCHAR(30) NULL);");
	}

	/**
	 * Método que hace consultas a la base de datos para desplegar los idiomas
	 * existentes.
	 */
	private void mostrarIdiomas() {
		List<String> idiomas = conecta
				.consultarDatos("SELECT distinct idioma FROM Books GROUP BY idioma;");

		ObservableList<String> opciones = FXCollections
				.observableArrayList(idiomas);

		escogerIdiomaExistenteID.setItems(opciones);
	}

	/**
	 * 
	 * @return Retorna cierto o falso según se haya rellenado los valores del
	 *         barchart.
	 */
	public boolean isOk() {
		return Ok;
	}

	/**
	 * Método que muestra Dialog de error.
	 */
	private void mensajeError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error de entrada");
		alert.setHeaderText("Error, revisa el código");
		alert.setContentText("Texto insertado inválido, o "
				+ "valores nulos no aceptados.");

		alert.showAndWait();
	}

	/**
	 * Método que mostrará un Dialog con la información de su todopoderoso
	 * creador.
	 */
	private void infoAcerca() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error de entrada");
		alert.setHeaderText("Hola a quien lea esto");

		alert.setContentText("Mi nombre es Erick Pineda, más conocido"
				+ " en los bajos mundos como Jonh y en otras asignaturas"
				+ " como Jonh Erick, casualmente llamado solo Pineda;"
				+ " Destacando el hecho de que uniendolos, es mi nombre"
				+ " casi completo.");

		alert.showAndWait();
	}

	/**
	 * Event Listener on MenuItem.onAction
	 * 
	 * @param event
	 *            Evento que se dispara cuando se hace click.
	 */
	@FXML
	public void salir(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * 
	 * @param s
	 *            Stage a cambiar.
	 */
	public void setStage(Stage s) {
		this.myStage = s;
	}
}
