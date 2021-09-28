package teko.ea.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import teko.ea.DBConnection;
import teko.ea.DateHelper;
import teko.ea.Stunden;

public class StundenDao {

	//Verbindung abbauen
	private static void close(Connection con, PreparedStatement ps, ResultSet rs) throws SQLException {
		if (con != null) {
			con.close();
		}
		if (ps != null) {
			ps.close();
		}
		if (rs != null) {
			rs.close();
		}
	}

	//Diese Methode wird hauptsächlich für die Auflistung der Stunden pro Benutzer verwendet
	//Liste aller Stunden für Benutzer (anhand uid) erzeugen. Query nach Datum und Startzeit sortieren
	public static List<Stunden> getStundenForEmp(int id) throws Exception {
		List<Stunden> list = new ArrayList<Stunden>();
		
		//Initialisierung und Deklaration mit null value, damit sichergestellt ist, dass Query funktioniert
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement("select * from stunden where uid = ? order by work_date desc, start_time asc");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				Stunden bean = new Stunden();
				bean.setId(rs.getLong("id"));
				bean.setUid(rs.getInt("uid"));
				bean.setFromTime(rs.getInt("start_time"));
				bean.setToTime(rs.getInt("end_time"));
				bean.setDate(rs.getDate("work_date"));
				bean.setTotalHours(rs.getFloat("total"));
				bean.setStundenTyp(rs.getString("notes"));

				list.add(bean);
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(con, ps, rs);
		}
	}
	
	public static List<Stunden> getStundenHeute(int id, Date date) throws Exception {
		List<Stunden> list = new ArrayList<Stunden>();
		
		//Initialisierung und Deklaration mit null value, damit sichergestellt ist, dass Query funktioniert
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement("select * from stunden where uid = ? and where work_date = ? order by start_time");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				Stunden bean = new Stunden();
				bean.setId(rs.getLong("id"));
				bean.setUid(rs.getInt("uid"));
				bean.setFromTime(rs.getInt("start_time"));
				bean.setToTime(rs.getInt("end_time"));
				bean.setDate(rs.getDate("work_date"));
				bean.setTotalHours(rs.getFloat("total"));
				bean.setStundenTyp(rs.getString("notes"));

				list.add(bean);
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(con, ps, rs);
		}
	}
	
	public static double getFerienForEmp(int uid, Date date, Date endJahr) throws Exception {
		double alleferien = 0.0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select SUM(total) as alleferien FROM stunden WHERE uid = ? and work_date >= ? and work_date <= ? and notes iLIKE '%ferien%'";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setDate(2, date);
			ps.setDate(3, endJahr);
			rs = ps.executeQuery();
			if (rs.next()) {
				alleferien = rs.getDouble("alleferien");
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(con, ps, rs);
		} return alleferien;
		
	}
	
	public static double getIstStundenJahr(int uid, Date startJahr, Date endJahr) throws Exception {
		double iststundenjahr = 0.0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select SUM(total) as iststundenjahr FROM stunden WHERE uid = ? and work_date >= ? and work_date <= ?";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setDate(2, startJahr);
			ps.setDate(3, endJahr);
			rs = ps.executeQuery();
			if (rs.next()) {
				iststundenjahr = rs.getDouble("iststundenjahr");
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(con, ps, rs);
		} return iststundenjahr;
		
	}
	
	public static double getIstStundenWoche(int uid, Date startWoche, Date endeWoche) throws Exception {
		
		double iststundenwoche = 0.0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select SUM(total) as iststundenwoche FROM stunden WHERE uid = ? and work_date >= ? and work_date <= ?";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setDate(2, startWoche);
			ps.setDate(3, endeWoche);
			rs = ps.executeQuery();
			if (rs.next()) {
				iststundenwoche = rs.getDouble("iststundenwoche");
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			
		} finally {
			close(con, ps, rs);
		} return iststundenwoche;
		
	}
	
	public static double getIstStundenTag(int uid, Date tag) throws Exception {
		
		double iststundentag = 0.0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select SUM(total) as iststundentag FROM stunden WHERE uid = ? and work_date = ?";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setDate(2, tag);
			rs = ps.executeQuery();
			if (rs.next()) {
				iststundentag = rs.getDouble("iststundentag");
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			
		} finally {
			close(con, ps, rs);
		} return iststundentag;
		
	}
	
	//Liste Stunden für Benutzer der aktuellen Woche
	public static List<Stunden> getWochenstundenforEmp(int id, Date startWoche, Date endeWoche) throws Exception {
		List<Stunden> list = new ArrayList<Stunden>();
		
		//Initialisierung und Deklaration mit null value, damit sichergestellt ist, dass Query funktioniert
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement("select * from stunden where uid = ? and work_date >= ? and work_date <= ? order by work_date, start_time");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				Stunden bean = new Stunden();
				bean.setId(rs.getLong("id"));
				bean.setUid(rs.getInt("uid"));
				bean.setFromTime(rs.getInt("start_time"));
				bean.setToTime(rs.getInt("end_time"));
				bean.setDate(rs.getDate("work_date"));
				bean.setTotalHours(rs.getFloat("total"));
				bean.setStundenTyp(rs.getString("notes"));

				list.add(bean);
			}
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(con, ps, rs);
		}
	}
	
	//Stunden in Datenbank speichern
	public static boolean save(Stunden bean) throws Exception {
		String sql = "insert into stunden(uid, work_date, start_time, end_time, total, notes) values (?,?,?,?,?,?)";
		int status = 0;
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, bean.getUid());
			ps.setDate(2, DateHelper.getSqlDate(bean.getDate()));
			ps.setInt(3, bean.getFromTime());
			ps.setInt(4, bean.getToTime());
			ps.setFloat(5, bean.getTotalStunden());
			ps.setString(6, bean.getStundenTyp());

			status = ps.executeUpdate();

			con.close();
			return status == 1;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

}
