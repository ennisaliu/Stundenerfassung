package teko.ea;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;


//Date Helfer Klasse
public class DateHelper {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	//Help Methoden für Kompatibilität von Java util date und sql date
	public static Date getSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
	
	public static String toDisplayFormat(java.util.Date date) {
		return sdf.format(date);
	}
	
	
	public static Date convertToDateViaSqlDate(LocalDate dateToConvert) {
	    return java.sql.Date.valueOf(dateToConvert);
	}
	
}
