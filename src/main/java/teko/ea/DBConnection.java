package teko.ea;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	private static Connection con;
	
	//Get Verbindung. Falls keine Verbindung besteht, Verbindung initialisieren und Verbindung zurückgeben
	public static Connection getConnection() throws Exception {
		if (con == null || con.isClosed()) {
			initConnection();
		}
		return con;
	}

	public static Connection initConnection() throws Exception {
		//DB Infos statisch
		String url = "jdbc:postgresql://localhost/u905";
		String user = "u905";
		String password = "upa905";
		//Verbindung mit DB Infos aufbauen
		try {
			con = DriverManager.getConnection(url, user, password);
			//System.out.println("Verbindung zum Server erfolgreich.");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return con;
	}

}
