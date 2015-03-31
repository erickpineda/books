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
	 * Variable que se encarga de ir lÌnea a lÌnea del fichero existente.
	 */
	private static BufferedReader in = null;
	/**
	 * ExpresiÛn regular para validar diferentes idiomas.
	 */
	private static final String REGEXP = "[^A-Za-zÒ—Á«‰ˆ¸ﬂ]";

	/**
	 * Mapa que almacenar· la letra y la cantidad de veces que repite.
	 */
	private Map<Character, Integer> mapa;
	/**
	 * Conector a la BDD por medio de la clase {@code ConectionDB}.
	 */
	private ConectionDB conecta;

	/**
	 * Constructor de acciones de fichero para leer.
	 * 
	 * @param f
	 *            Fichero que se leer·.
	 */
	public Acciones(File f, ConectionDB c) {
		fichero = f;
		mapa = new HashMap<Character, Integer>();
		this.conecta = c;
	}

	/**
	 * MÈtodo que borra los acentos para el idioma EspaÒol.
	 * 
	 * @param textoEntrada
	 *            Par·metro que ser·, el texto a borrarle los acentos, si los
	 *            tiene.
	 * @return Retorna un String que estar· limpio de acentos.
	 */
	private static String borrarAcentosEsp(String textoEntrada) {
		// String conAcentos =
		// "·‡‰ÈËÎÌÏÔÛÚˆ˙˘uÒ¡¿ƒ…»ÀÕÃœ”“÷⁄Ÿ‹—Á«";([A-ZÒ—a-z¡…Õ”⁄·ÈÌÛ˙])\w*
		// String sinAcentos = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";

		String conAcentos = "·ÈÌÛ˙¡…Õ”⁄";
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

	/**
	 * MÈtodo que pasa por par·metro un n˙mero, para calcular la frecuencia de
	 * la letra en entre el alfabeto.
	 * 
	 * @param individual
	 *            N˙mero que ser·n las veces que repite la letra en el texto.
	 * @return Retorna un double, que ser· el porcentaje.
	 */
	private double calcularFrecuencia(double individual) {
		double nTotalLetras = 0.0;

		for (Entry<Character, Integer> m : mapa.entrySet()) {
			nTotalLetras += m.getValue();
		}

		return (individual * 100.0) / nTotalLetras;
	}

	/**
	 * MÈtodo que inserta datos en la tabla, iterando un mapa con valores.
	 * 
	 * @param idioma
	 *            Ser· el idioma, con el que se har· la consulta.
	 */
	public void insertarDatosTabla(String idioma) {

		for (Entry<Character, Integer> m : mapa.entrySet()) {

			try {
				// Si el idioma no existe hace un insert
				conecta.insertarDatos("insert into Books values(default,'"
						+ m.getKey() + "','" + idioma + "'," + m.getValue()
						+ ",'" + calcularFrecuencia(m.getValue()) + "%'),");

			} catch (Exception e) {
				// Hace un update si el idioma existe, para reemplazar los
				// nuevos valores
				conecta.actualizarDatos("UPDATE Books SET vecesRepetida = "
						+ m.getValue() + ", frecuencia = '"
						+ calcularFrecuencia(m.getValue())
						+ "%' WHERE idioma = '" + idioma + "';");
			}
		}
	}

	/**
	 * MÈtodo que lee el fichero de texto y rpocesa los datos.
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

	/**
	 * MÈtodo que limpia de todo mal, el texto procesado.
	 * 
	 * @param textoLimpio
	 *            Par·metro que se procesa, para luego retornar un nuevo String
	 *            limpio.
	 * @return Retorna un String limpio de todo mal.
	 */
	private String limpiarTexto(String textoLimpio) {
		return noQuieroCaracteresRaros(borrarAcentosEsp(textoLimpio));
	}

	/**
	 * MÈtodo que recibir· un texto y validar· la expresiÛn regular, devolviendo
	 * un nuevo String sin caracteres raros, por Ej: comas ',', puntos'.' etc.
	 * 
	 * @param texto
	 *            Par·metro de String a validar, por cada caracter diferente lo
	 *            borrar·.
	 * @return Retorna un nuevo String limpio de caracteres raros.
	 */
	private String noQuieroCaracteresRaros(String texto) {
		return texto.replaceAll(REGEXP, "").toLowerCase().trim();
	}

	/**
	 * Rellena un mapa, que tendr· el caracter procesado y la cantidad de veces
	 * que repite.
	 * 
	 * @param texto
	 *            Par·metro String que procesar· el mapa, para luego almacenar
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
