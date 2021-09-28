package teko.ea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import teko.ea.DBConnection;
import teko.ea.Emp;

public class EmpDao {

	static Connection currentCon = null;
	static ResultSet rs = null;

	//Benutzerlogin
	public static Emp login(String username,
			String password ) throws Exception {

		//Statement für Datenbankverbindung
		Statement stmt = null;

		String searchQuery = "select * from user905 where name='" + username + "' AND password='" + password + "'";

		//Log auf Server
		LocalDateTime jetzt = LocalDateTime.now();
		System.out.println("Benutzer: " + username + ", hat sich angemeldet am: " + jetzt);

		try {
			// Datenbankverbindung aufbauen
			currentCon = DBConnection.getConnection();
			stmt = currentCon.createStatement();
			rs = stmt.executeQuery(searchQuery);

			// Wenn kein Benutzer gefunden wird, wird null zurückgegeben
			if (!rs.next()) {
				System.out.println("Benutzer nicht gefunden.");
				return null;
			}

			//Wenn Benutzer gefunden, wird Name und ID zurückgegeben
			else{
				
				Emp bean = new Emp();

				bean.setName(rs.getString("name"));
				bean.setId(rs.getInt("id"));
				return bean;
			}
		}
		//Fehlermeldung wenn login fehlschlägt
		catch (Exception ex) {
			System.out.println("Login fehlgeschlagen. Ein Fehler ist aufgetreten: " + ex);
			throw ex;
		}

	}

	//Benutzer in Datenbank speichern
	public static int save(Emp bean) {
		int status = 0;
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con
					.prepareStatement("insert into user905(name,password,email,country) values (?,?,?,?)");
			ps.setString(1, bean.getName());
			ps.setString(2, bean.getPassword());
			ps.setString(3, bean.getEmail());
			ps.setString(4, bean.getCountry());

			status = ps.executeUpdate();

			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return status;
	}

	//Benutzeränderungen in Datenbank updaten
	public static int update(Emp bean) {
		int status = 0;
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con
					.prepareStatement("update user905 set name=?,password=?,email=?,country=? where id=?");
			ps.setString(1, bean.getName());
			ps.setString(2, bean.getPassword());
			ps.setString(3, bean.getEmail());
			ps.setString(4, bean.getCountry());
			ps.setInt(5, bean.getId());

			status = ps.executeUpdate();

			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return status;
	}

	//Benutzer aus Datenbanklöschen
	public static int delete(int id) {
		int status = 0;
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("delete from user905 where id=?");
			ps.setInt(1, id);
			status = ps.executeUpdate();

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	//Benutzer anhand ID selektieren
	public static Emp getEmployeeById(int id) {
		Emp bean = new Emp();

		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from user905 where id=?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				bean.setId(rs.getInt(1));
				bean.setName(rs.getString(2));
				bean.setPassword(rs.getString(3));
				bean.setEmail(rs.getString(4));
				bean.setCountry(rs.getString(5));
			}
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return bean;
	}

	//Liste aller Benutzer ausgeben
	public static List<Emp> getAllEmployees() {
		List<Emp> list = new ArrayList<Emp>();

		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from user905");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Emp bean = new Emp();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString(2));
				bean.setPassword(rs.getString(3));
				bean.setEmail(rs.getString(4));
				bean.setCountry(rs.getString(5));
				list.add(bean);
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
