package teko.ea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import teko.ea.DBConnection;
import teko.ea.PersInfo;

public class PersInfoDao {

	// Verbindung schliessen, falls offen
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

	// Persinfo für Mitarbeiter holen

	public static PersInfo getPersInfobyID(int uid) throws Exception {
		
		PersInfo bean = new PersInfo();
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement("select * from pers_info where uid=?");
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			if (rs.next()) {
				bean.setUid(rs.getInt("uid"));
				bean.setEintritt(rs.getDate("eintritt"));
				bean.setAustritt(rs.getDate("austritt"));
				bean.setRolle(rs.getString("rolle"));
				bean.setFerienSoll(rs.getFloat("ferien"));
				bean.setWochenArbZeit(rs.getFloat("wochenarbeitszeit"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(con, ps, rs);
		}

		return bean;
	}
	}
