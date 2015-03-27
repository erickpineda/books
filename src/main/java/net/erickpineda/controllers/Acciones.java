package net.erickpineda.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Acciones {
	/**
	 * Fichero a leer.
	 */
	private static File fichero;
	/**
	 * Abre el fichero a leer.
	 */
	private static FileReader ficheroALeer = null;
	/**
	 * Variable que se encarga de ir l�nea a l�nea del fichero existente.
	 */
	private static BufferedReader in = null;
	/**
	 * Expresi�n regular para validar diferentes idiomas.
	 */
	private static final String REGEXP = "[^A-Za-z��]";
	private boolean todoOk = false;

	/**
	 * Mapa que almacenar� la letra y la cantidad de veces que repite.
	 */
	private Map<Character, Integer> mapa;
	private ConectionDB conecta;

	/**
	 * Constructor de acciones de fichero para leer.
	 * 
	 * @param f
	 *            Fichero que se leer�.
	 */
	public Acciones(File f, ConectionDB c) {
		fichero = f;
		mapa = new HashMap<Character, Integer>();
		this.conecta = c;
	}

	/**
	 * M�todo que borra los acentos para el idioma Espa�ol.
	 * 
	 * @param textoEntrada
	 *            Par�metro que ser�, el texto a borrarle los acentos, si los
	 *            tiene.
	 * @return Retorna un String que estar� limpio de acentos.
	 */
	private static String borrarAcentosEsp(String textoEntrada) {
		// String conAcentos =
		// "��������������u�������������������";([A-Z��a-z����������])\w*
		// String sinAcentos = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";

		String conAcentos = "����������";
		String sinAcentos = "aeiouAEIOU";

		for (int i = 0; i < conAcentos.length(); i++) {
			textoEntrada = textoEntrada.replace(conAcentos.charAt(i),
					sinAcentos.charAt(i));
		}
		return textoEntrada.toLowerCase().trim();
	}

	/**
	 * 
	 * @return Retorna un mapa, con el caracter y la cantidad de veces que
	 *         repite.
	 */
	public Map<Character, Integer> getMapa() {
		return mapa;
	}

	public void insertarDatosTabla(String idioma) {

		for (Entry<Character, Integer> m : mapa.entrySet()) {

			conecta.insertarDatos("insert into Books values(default,'"
					+ m.getKey() + "','" + idioma + "'," + m.getValue()
					+ ",'100%'),");

			// conecta.insertarDatos("insert into Books values('" + idioma +
			// "','"
			// + m.getKey() + "'," + m.getValue() + ",'100%')");
		}
	}

	/**
	 * M�todo que lee el fichero de texto y rpocesa los datos.
	 */
	public void leerFichero() {

		String linea = null;
		try {

			ficheroALeer = new FileReader(fichero);
			in = new BufferedReader(ficheroALeer);

			while ((linea = in.readLine()) != null) {
				rellenarMapaCaracteres(linea);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTodoOk() {
		return todoOk;
	}

	public void setTodoOk(boolean todoOk) {
		this.todoOk = todoOk;
	}

	/**
	 * M�todo que limpia de todo mal, el texto procesado.
	 * 
	 * @param textoLimpio
	 *            Par�metro que se procesa, para luego retornar un nuevo String
	 *            limpio.
	 * @return Retorna un String limpio de todo mal.
	 */
	private String limpiarTexto(String textoLimpio) {
		return noQuieroCaracteresRaros(borrarAcentosEsp(textoLimpio));
	}

	/**
	 * M�todo que recibir� un texto y validar� la expresi�n regular, devolviendo
	 * un nuevo String sin caracteres raros, por Ej: comas ',', puntos'.' etc.
	 * 
	 * @param texto
	 *            Par�metro de String a validar, por cada caracter diferente lo
	 *            borrar�.
	 * @return Retorna un nuevo String limpio de caracteres raros.
	 */
	private String noQuieroCaracteresRaros(String texto) {
		return texto.replaceAll(REGEXP, "").toLowerCase().trim();
	}

	/**
	 * Rellena un mapa, que tendr� el caracter procesado y la cantidad de veces
	 * que repite.
	 * 
	 * @param texto
	 *            Par�metro String que procesar� el mapa, para luego almacenar
	 *            cada caracter.
	 */
	private void rellenarMapaCaracteres(String texto) {
		String textoLimpio = limpiarTexto(texto);

		for (int i = 0; i < textoLimpio.length(); i++) {
			if (mapa.containsKey(textoLimpio.charAt(i))) {
				mapa.put(textoLimpio.charAt(i),
						mapa.get(textoLimpio.charAt(i)) + 1);
			} else {
				mapa.put(textoLimpio.charAt(i), 1);
			}
		}
	}
}
