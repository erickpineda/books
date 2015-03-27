package net.erickpineda.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	 * Nombre que de la base de datos a conectar.
	 */
	private String bd;
	/**
	 * Nombre de host para el SGBDD.
	 */
	@SuppressWarnings("unused")
	private String host;
	/**
	 * Contraseña del usuario de la base de datos.
	 */
	private String password;
	/**
	 * Enlaza la conexión del host y bdd hacia el servidor.
	 */
	private String server = "jdbc:h2:~/workspace/Books/";
	/**
	 * Nombre del usuario de la base de datos.
	 */
	private String user;

	/**
	 * Constructor que se le pasa como parámetro el nombre de la base de datos,
	 * el usuario y constraseña a conectarse, para luego en el método
	 * {@code conectarBD()} efectuar la conexión.
	 * 
	 * @param bdd
	 *            Nombre de la base de datos a conectar.
	 * @param usuario
	 *            Nombre del usuario de la base de datos.
	 * @param pass
	 *            Contraseña de la base de datos.
	 */
	public ConectionDB(final String bdd, final String usuario, final String pass) {

		this.bd = bdd;
		this.user = usuario;
		this.password = pass;
		this.server += bd;
	}

	public void conectarBD() {
		try {
			Class.forName(DRIVER);
			// "jdbc:h2:~/books", "admin", ""
			conexion = DriverManager.getConnection(server, user, password);

			System.out.println("-> Conexión a base de datos " + server
					+ " ... OK\n");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

			System.out.println("-> Error cargando el Driver " + DRIVER
					+ " ... FAIL");

		} catch (SQLException e) {
			e.printStackTrace();

			System.out.println("-> Imposible realizar conexión con " + server
					+ " ... FAIL");

		}
	}

	List<String> lista = new ArrayList<String>();

	public List<String> consultarDatos(final String miConsulta) {

		java.sql.Statement s;
		ResultSet rs; // Puntero o cursor a la fila actual

		try {
			s = conexion.createStatement();
			rs = s.executeQuery(miConsulta);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					lista.add(new String(rs.getString(i)));
					System.out.print(" " + rs.getString(i) + " |");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	public void desconectarBD() {
		try {
			conexion.close();
		} catch (SQLException e) {
			System.out.println("-> Cerrar conexión con " + server + " ... OK");
			e.printStackTrace();
			System.out.println("-> Imposible cerrar conexión ... FAIL");
		}
	}

	public String insertarDatos(final String miConsulta) {

		// java.sql.PreparedStatement s;

		try {
			// s = conexion.prepareStatement(miConsulta);
			// s.execute(miConsulta);
			conexion.createStatement().execute(miConsulta);

		} catch (SQLException e) {
			try {
				conexion.rollback(); // Rollback si el INSERT es erróneo
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return miConsulta;
	}
}
