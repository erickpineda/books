package net.erickpineda.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConectionDB {
	/**
	 * Conector hacia el SGBDD.
	 */
	private static Connection conexion;
	/**
	 * Driver de la base de datos a conectar.
	 */
	private static final String DRIVER = "org.h2.Driver";
	/**
	 * Contrase�a del usuario de la base de datos.
	 */
	private String password;
	/**
	 * Enlaza la conexi�n del host y bdd hacia el servidor. Ej:
	 * "jdbc:h2:~/ruta/actual/".
	 */
	private String server = "jdbc:h2:";
	/**
	 * Nombre del usuario de la base de datos.
	 */
	private String user;
	private String bd;
	/**
	 * Lista que usar� para rellenar los resultados de una SELECT.
	 */
	private List<String> lista = new ArrayList<String>();
	/**
	 * Mapa a rellenar a partir de una SELECT.
	 */
	private Map<Character, Integer> mapa = new HashMap<Character, Integer>();

	/**
	 * Constructor que se le pasa como par�metro el nombre de la base de datos,
	 * el usuario y constrase�a a conectarse, para luego en el m�todo
	 * {@code conectarBD()} efectuar la conexi�n.
	 * 
	 * @param bdd
	 *            Nombre de la base de datos a conectar.
	 * @param usuario
	 *            Nombre del usuario de la base de datos.
	 * @param pass
	 *            Contrase�a de la base de datos.
	 */
	public ConectionDB(final String bdd, final String usuario, final String pass) {
		this.user = usuario;
		this.password = pass;
		this.bd = bdd;

		File fichero = new File(bd);
		this.server += fichero.getAbsolutePath() + ";IFEXISTS=FALSE";

		try {
			if (File.createTempFile("books", "mv").exists())
				restaurarBDD();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que efect�a la conexi�n con la BDD.
	 * 
	 * @return
	 */
	public ConectionDB conectarBD() {
		try {

			Class.forName(DRIVER);
			conexion = DriverManager.getConnection(server, user, password);
			// backupBDD();
			System.out.println("-> Conexi�n a base de datos " + server
					+ " ... OK\n");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

			System.out.println("-> Error cargando el Driver " + DRIVER
					+ " ... FAIL");

		} catch (SQLException e) {
			e.printStackTrace();

			System.out.println("-> Imposible realizar conexi�n con " + server
					+ " ... FAIL");
		}
		return this;
	}

	/**
	 * M�todo para restaurar la BDD.
	 */
	private void restaurarBDD() {

		try {
			// String path = getClass().getResource("../books.zip").toString();

			String[] bkp = { "-url", server, "-user", user, "-password",
					password, "-script", "src/main/resources/books.zip",
					"-options", "compression", "zip" };

			org.h2.tools.RunScript.main(bkp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�todo para hacer una copia a la BDD en un ZIP.
	 */
	@SuppressWarnings("unused")
	private void backupBDD() {

		try {
			String[] bkp = { "-url", server, "-user", user, "-password",
					password, "-script", "books.zip", "-options",
					"compression", "zip" };

			org.h2.tools.Script.main(bkp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return Retorna una lista con los valores de una consulta SELECT.
	 */
	public List<String> getLista() {
		return lista;
	}

	/**
	 * 
	 * @param lista
	 *            Lista a cambiar.
	 */
	public void setLista(List<String> lista) {
		this.lista = lista;
	}

	/**
	 * 
	 * @return Retorna el mapa.
	 */
	public Map<Character, Integer> getMapa() {
		return mapa;
	}

	/**
	 * 
	 * @param mapa
	 *            Mapa a cambiar.
	 */
	public void setMapa(Map<Character, Integer> mapa) {
		this.mapa = mapa;
	}

	/**
	 * M�todo que pasa por par�metro un String, que ser� una consulta SELECT.
	 * 
	 * @param miConsulta
	 *            Consulta SELECT a efectuar.
	 */
	public boolean consultarDatos(final String miConsulta, boolean rellenarMapa) {
		boolean hayValores = false;
		java.sql.Statement s;
		ResultSet rs; // Puntero o cursor a la fila actual

		try {
			s = conexion.createStatement();
			rs = s.executeQuery(miConsulta);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// System.out.print(" " + rs.getString(i) + " |");
					lista.add(new String(rs.getString(i)));

					if (rellenarMapa == true) {
						mapa.put(rs.getString("letra").charAt(0),
								rs.getInt("vecesRepetida"));
					}

					hayValores = true;
				}
				// System.out.println();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hayValores;
	}

	/**
	 * M�todo que desconecta de la base de datos.
	 */
	public void desconectarBD() {
		try {
			conexion.close();
		} catch (SQLException e) {
			System.out.println("-> Cerrar conexi�n con " + server + " ... OK");
			e.printStackTrace();
			System.out.println("-> Imposible cerrar conexi�n ... FAIL");
		}
	}

	/**
	 * M�todo para insertar datos en la BDD sean, INSERT, CREATE etc.
	 * 
	 * @param miConsulta
	 *            Consulta SQL.
	 * @return Retorna un String de la consulta efectuada.
	 */
	public void insertarDatos(final String miConsulta) {

		// java.sql.PreparedStatement s;

		try {
			// s = conexion.prepareStatement(miConsulta);
			// s.execute(miConsulta);
			conexion.createStatement().execute(miConsulta);

		} catch (SQLException e) {
			try {
				conexion.rollback(); // Rollback si el INSERT es err�neo
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que hace UPDATEs a la BDD.
	 * 
	 * @param miConsulta
	 *            Par�metro que ser� la consulta SQL.
	 */
	public void actualizarDatos(final String miConsulta) {
		try {

			java.sql.PreparedStatement actualiza = conexion
					.prepareStatement(miConsulta);

			actualiza.executeUpdate(miConsulta);

		} catch (SQLException e) {
		}
	}
}
