package teko.ea;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.time.*;
import teko.ea.DateHelper;
import teko.ea.db.EmpDao;
import teko.ea.db.StundenDao;
public class Zeitrechner {

	public static void main(String[] args) {

		//---Tests--- diese main methode wird nur für Tests verwendet.
		System.out.println("--- Java Time Stunden-Rechner ---");
		
		System.currentTimeMillis();
		int stundenEingabe = 800;
		
		DecimalFormat df = new DecimalFormat("00,00");
		String vonZeit = df.format(stundenEingabe);

		System.out.println("vonZeit: " + vonZeit);
		System.out.println("bisZeit: " + vonZeit);
	
		System.out.println("--- Java Time Date-Rechner ---");
		
		// Enddatum für Tagberechnung
		LocalDate end = LocalDate.now();
		// Eintrittsdatum des Mitarbeiters
		LocalDate start = LocalDate.of(2018, 07, 01);
		// Alle Tage inkl. Wochenende zwischen Daten ausschliesslich heute (-1 Tag)
		long daysBetween = ChronoUnit.DAYS.between(start, end) - 1;
		
		System.out.println("Eintritt am: " + start);
		System.out.println("Heute: " + LocalDate.now());
		
		System.out.println("Tage vergangen: " + daysBetween);
		
		Date start1 = DateHelper.convertToDateViaSqlDate(start);
		Date end1 = DateHelper.convertToDateViaSqlDate(end);
		
		System.out.println("Arbeitstage vergangen: " + arbTageZwischen(start1, end1));
		
		//Total Sollzeit mit vergangenen Arbeitstagen berechen
		
		arbTageZwischen(start1, end1);
		
		System.out.println("Ferien bis heute: " + berechneFerienSoll(start1, 25.00, 32.00));
		

	}
	
	// User: 卢声远 Shengyuan Lu, Stackoverflow, 15.09.2021
	// https://stackoverflow.com/a/4600534
	static long arbTageZwischen(Date start, Date end){

	    Calendar c1 = Calendar.getInstance();
	    c1.setTime(start);
	    int w1 = c1.get(Calendar.DAY_OF_WEEK);
	    c1.add(Calendar.DAY_OF_WEEK, -w1);

	    Calendar c2 = Calendar.getInstance();
	    c2.setTime(end);
	    int w2 = c2.get(Calendar.DAY_OF_WEEK);
	    c2.add(Calendar.DAY_OF_WEEK, -w2);

	    //Ende Samstag zu Anfang Samstag
	    long tage = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
	    long tageOhneWochenendtage = tage-(tage*2/7);

	    // Tage anpassen und zu (w2) hinzufügen und Tage von (w1) abziehen,
	    // sodass Samstag und Sonntag nicht berücksichtig sind
	    if (w1 == Calendar.SUNDAY && w2 != Calendar.SATURDAY) {
	        w1 = Calendar.MONDAY;
	    } else if (w1 == Calendar.SATURDAY && w2 != Calendar.SUNDAY) {
	        w1 = Calendar.FRIDAY;
	    } 

	    if (w2 == Calendar.SUNDAY) {
	        w2 = Calendar.MONDAY;
	    } else if (w2 == Calendar.SATURDAY) {
	        w2 = Calendar.FRIDAY;
	    }

	    return tageOhneWochenendtage-w1+w2;
	}
	
	//Feriensoll anhand des Eintrittdatums in Stunden berechnen
	static double berechneFerienSoll(Date eintritt, double ferien, double pensum) {
		
		double standardPensum = 40; 
		int jahr = Calendar.getInstance().get(Calendar.YEAR);
		Date endeDiesesJahr = DateHelper.convertToDateViaSqlDate(LocalDate.of(jahr, 12, 31));
		
		// Daten konvertieren
		//LocalDate start = eintritt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//LocalDate start = LocalDate.ofInstant(eintritt.toInstant(), ZoneId.systemDefault());
		//LocalDate ende = endeDiesesJahr.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//LocalDate ende = LocalDate.ofInstant(endeDiesesJahr.toInstant(), ZoneId.systemDefault());
		
		LocalDate start = Instant.ofEpochMilli(eintritt.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		
		LocalDate ende = Instant.ofEpochMilli(endeDiesesJahr.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		
		// Alle Tage inkl. Wochenende zwischen Daten +1 für Start
		double vergangeneTage = ChronoUnit.DAYS.between(start, ende)+1;
		
		//Tage im Kalenderjahr inkl. Schaltjahr
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int calJahrTage = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
		
		double pensumProzent = pensum/standardPensum;
		double sollTag = standardPensum/5*pensumProzent;
		double ferienSoll = ferien/calJahrTage*vergangeneTage*sollTag;
		
		// --- Tests ---
		/*
		System.out.println("start: " + start);
		System.out.println("ende: " + ende);
		System.out.println("vergangeneTage: " + vergangeneTage);
		System.out.println();
		
		System.out.println("ferien");
		System.out.println("pensumProzent: " + pensumProzent);
		System.out.println("sollTag: " + sollTag);
		System.out.println("ferienStundenJahr: " + ferienStundenJahr);
		System.out.println("ferienSoll: " + ferienSoll);
		*/
		
		return ferienSoll;
		
	}

}
