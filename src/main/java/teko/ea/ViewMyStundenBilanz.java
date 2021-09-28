package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import teko.ea.db.PersInfoDao;
import teko.ea.db.StundenDao;

/**
 * Servlet implementation class ViewMyStundenBilanz
 */
@WebServlet("/ViewMyStundenBilanz")
public class ViewMyStundenBilanz extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Sessionmanangement und Prüfung valider Session
		if (!SessionUtil.isValidUserSession(request)) {
			RequestDispatcher rd = request.getRequestDispatcher("LogoutServlet");
			rd.forward(request, response);
		}

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.print("<script src=\"https://cdn.jsdelivr.net/npm/chart.js@3.5.1/dist/chart.min.js\"></script>");

		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/styles.css\">");

		out.println("	<nav class=\"navbar fixed-top navbar-expand-lg navbar-light bg-light\">\r\n"
				+ "		<div class=\"container-fluid\">\r\n"
				+ "			<a class=\"navbar-brand\" href=\"home.jsp\"><i class=\"bi bi-house\"></i></a>\r\n"
				+ "			<a class=\"navbar-brand\">meine Stundenblianz</a>"
				+ "			<button class=\"navbar-toggler\" type=\"button\"\r\n"
				+ "				data-bs-toggle=\"collapse\" data-bs-target=\"#navbarSupportedContent\"\r\n"
				+ "				aria-controls=\"navbarSupportedContent\" aria-expanded=\"false\"\r\n"
				+ "				aria-label=\"Toggle navigation\">\r\n"
				+ "				<span class=\"navbar-toggler-icon\"></span>\r\n" + "			</button>\r\n"
				+ "			<div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">\r\n"
				+ "				<ul class=\"navbar-nav me-auto mb-2 mb-lg-0\">\r\n"
				+ "					<li class=\"nav-item dropdown\"><a\r\n"
				+ "						class=\"nav-link dropdown-toggle\" href=\"#\" id=\"navbarDropdown\"\r\n"
				+ "						role=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">\r\n"
				+ "							Stundenübersicht </a>\r\n"
				+ "						<ul class=\"dropdown-menu\" aria-labelledby=\"navbarDropdown\">\r\n"
				+ "							<li><a class=\"dropdown-item\"\r\n"
				+ "								href=\"ViewMyArbeitStundenServlet\">meine Stunden</a></li>\r\n"
				+ "							<li><a class=\"dropdown-item\" href=\"ViewMyStundenBilanz\">meine\r\n"
				+ "									Stundenbilanz</a></li>\r\n"
				+ "							<li><a class=\"dropdown-item\" href=\"ViewMyFerienServlet\">meine\r\n"
				+ "									Ferienbilanz</a></li>\r\n"
				+ "							<li><hr class=\"dropdown-divider\"></li>\r\n"
				+ "							<li><a class=\"dropdown-item\" href=\"ViewStundenServlet\">alle\r\n"
				+ "									Mitarbeiterstunden</a></li>\r\n"
				+ "						</ul>\r\n"
				+ "					<li class=\"nav-item\"><a class=\"nav-link\"\r\n"
				+ "						href=\"ViewUsersServlet\">Benutzerverwaltung</a></li>\r\n"
				+ "					</li>\r\n"
				+ "					<li class=\"nav-item\"><a class=\"nav-link disabled\">Termine</a>\r\n"
				+ "					</li>\r\n"
				+ "					<li class=\"nav-item\"><a class=\"nav-link disabled\">Aufträge</a>\r\n"
				+ "					</li>\r\n"
				+ "					<li class=\"nav-item\"><a class=\"nav-link\" href=\"LogoutServlet\" style=\"color: red\">Abmelden</a>\r\n"
				+ "					</li>\r\n" + "\r\n" + "				</ul>\r\n"
				+ "				<form class=\"d-flex\">\r\n"
				+ "					<input class=\"form-control me-2\" type=\"search\" placeholder=\"Suche\"\r\n"
				+ "						aria-label=\"Search\">\r\n"
				+ "					<button class=\"btn btn-outline-success\" type=\"submit\">Suche</button>\r\n"
				+ "				</form>\r\n" + "			</div>\r\n" + "		</div>\r\n" + "	</nav>");

		// ID und Name des Sessionbenutzers holen
		String userName = SessionUtil.getCurrentUser(request).getName();
		int uid = SessionUtil.getCurrentUser(request).getId();

		// Dezimal Formatierung
		DecimalFormat df = new DecimalFormat("0.00");

		// Liste für Stunden des Mitarbeiters (UID) erzeugen
		List<Stunden> list;
		try {
			list = StundenDao.getStundenForEmp(uid);
		} catch (Exception e) {
			e.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher("ViewStundenServlet");
			rd.forward(request, response);
			return;
		}

		// Erfasste Stunden des Benutzers
		// Datum Mapping
		TreeMap<Date, List<Stunden>> dateTimeMap = new TreeMap<>();
		TreeMap<Date, Float> dateTotalStundenap = new TreeMap<>();
		TreeSet<Date> dateSet = new TreeSet<>();

		// Stunden des Benutzers auflisten
		for (Stunden bean : list) {
			Date date = bean.getDate();

			// Stunden nach Datum mit Keymap auflisten
			List<Stunden> dateByList;
			Float totalStunden;

			if (dateTimeMap.containsKey(date)) {
				dateByList = dateTimeMap.get(date);
				totalStunden = dateTotalStundenap.get(date);
			} else {
				dateSet.add(date);
				dateByList = new ArrayList<>();
				dateTimeMap.put(date, dateByList);
				// Totalstunden als Float initialisieren
				totalStunden = 0.0F;
			}
			// Totalstunden des Tages berechnen
			dateByList.add(bean);
			totalStunden += bean.getTotalStunden();
			dateTotalStundenap.put(date, totalStunden);
		}

		// Datum Mapping
		dateTimeMap.keySet();

		try {

			// Variablen inizialisiern
			// Personeninfos von Bean
			PersInfo beanPersInfo = PersInfoDao.getPersInfobyID(uid);

			// Standard Wochenpensum der Firma bei 100% Anstellung in dezimal
			double standardPensum = 40.00;

			// Pensum des Mitarbeiters in dezimal
			double pensumWoche = beanPersInfo.getWochenArbZeit();

			// heutiges Datum
			LocalDate heute = LocalDate.now();
			Date heuteParsed = DateHelper.convertToDateViaSqlDate(heute);

			// Daten für Berechnung
			int jahr = Calendar.getInstance().get(Calendar.YEAR);
			int wochentag = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
			Date startDiesesJahr = DateHelper.convertToDateViaSqlDate(LocalDate.of(jahr, 01, 01));
			Date endeDiesesJahr = DateHelper.convertToDateViaSqlDate(LocalDate.of(jahr, 12, 31));

			// Start Diese Woche
			LocalDate ersterMontag = heute.with(DayOfWeek.MONDAY);
			Date startDieseWoche = DateHelper.convertToDateViaSqlDate(ersterMontag);
			
			// Wann beginnen mit Rechnen (Eintrittsdatum oder Anfang Jahr)
			double wannAnfang = Zeitrechner.arbTageZwischen(startDiesesJahr, beanPersInfo.getEintritt());
			// wenn Mitarbeiter nach Beginn des aktuellen Jahres eingetreten ist, wird sein
			// Eintrittsdarum verwendet
			if (wannAnfang > 0) {
				startDiesesJahr = beanPersInfo.getEintritt();
			}

			// --- Arbeitstage ---
			// Arbeitstage von Eintritt bis heute
			long arbTageSeitEintritt = Zeitrechner.arbTageZwischen(beanPersInfo.getEintritt(),
					DateHelper.convertToDateViaSqlDate(LocalDate.now()));

			// Arbeitstage von Anfang diesem Jahr bis heute
			long arbTageSeitDiesesJahr = Zeitrechner.arbTageZwischen(startDiesesJahr,
					DateHelper.convertToDateViaSqlDate(LocalDate.now()));

			// Arbeitstage von Anfang Woche bis heute
			long arbTageseitDieseWoche = Zeitrechner.arbTageZwischen(startDieseWoche,
					DateHelper.convertToDateViaSqlDate(LocalDate.now())) + 1;

			// ---Tag---
			out.print("<div><h1 class=\"text-center\">Tag</ph1></div>");
			// Soll
			double tagesSoll = (beanPersInfo.getWochenArbZeit() / 5);
			// Ist
			double IststundenTag = StundenDao.getIstStundenTag(uid, DateHelper.convertToDateViaSqlDate(heute));
			//Auflusting der Infos
			out.print("   <div class=\"container\">\r\n"
					+ "        <div class=\"row\">\r\n"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Soll"
					+ "            </div>"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		"+df.format(tagesSoll)+" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Ist"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		" +df.format(IststundenTag)+ " h"
					+ "            </div>\r\n"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Saldo"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "					"+ df.format(IststundenTag-tagesSoll) +" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "    </div>");
			//Pie Diagramm
			out.print("<canvas id=\"myChart\"></canvas>\r\n"
					+ "<script>\r\n"
					+ "var ctx = document.getElementById('myChart').getContext('2d');\r\n"
					+ "var myChart = new Chart(ctx, {\r\n"
					+ "    type: 'bar',\r\n"
					+ "    data: {\r\n"
					+ "        labels: [\"Soll\", \"Ist\"],"
					+ "        datasets: [{\r\n"
					+ "            label: 'heutige Stunden',\r\n"
					+ "            data: ["+df.format(tagesSoll)+", "+df.format(IststundenTag)+"],\r\n"
					+ "            backgroundColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderWidth: 1\r\n"
					+ "        }]\r\n"
					+ "    },\r\n"
					+ "    options: {\r\n"
					+ "        scales: {\r\n"
					+ "            y: {\r\n"
					+ "                display: false,\r\n"
					+ "                beginAtZero: true\r\n"
					+ "            }\r\n"
					+ "        },\r\n"
					+ "    	   plugins: {"
					+ "            legend: {"
					+ "          	display: false"
		            + "              }"
					+ "        }"	
					+ "    }\r\n"
					+ "});\r\n"
					+ "</script>");
			out.print("<hr>");
			
			// ---Woche---
			out.print("<div><h1 class=\"text-center\">Woche</ph1></div>");
			// Ist-Stunden
			double IstStundenDieserWoche = StundenDao.getIstStundenWoche(uid, DateHelper.getSqlDate(startDieseWoche),
					DateHelper.getSqlDate(heuteParsed)); 		
			//double IstStundenDieserWoche = StundenDao.getIstStundenWoche(uid,2021-09-20 ,2021-09-24);
			// Soll-Stunden der Woche bis zum heutigen Tag ohne Wochenende (Montag - Freitag)
			double sollStundenWocheBisHeute = wochentag * (beanPersInfo.getWochenArbZeit() / 5);
			df.format(sollStundenWocheBisHeute);
			
			//Auflistung der Infos
			out.print("   <div class=\"container\">\r\n"
					+ "        <div class=\"row\">\r\n"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Soll"
					+ "            </div>"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		"+df.format(pensumWoche)+" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Ist"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		" +df.format(IstStundenDieserWoche)+ " h"
					+ "            </div>\r\n"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Saldo"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "					"+ df.format(IstStundenDieserWoche-pensumWoche) +" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "    </div>");
			
			//Pie Diagramm
			out.print("<canvas id=\"myChart2\"></canvas>\r\n"
					+ "<script>\r\n"
					+ "var ctx = document.getElementById('myChart2').getContext('2d');\r\n"
					+ "var myChart2 = new Chart(ctx, {\r\n"
					+ "    type: 'bar',\r\n"
					+ "    data: {\r\n"
					+ "        labels: [\"Soll\", \"Ist\"],"
					+ "        datasets: [{\r\n"
					+ "            label: 'heutige Stunden',\r\n"
					+ "            data: ["+df.format(pensumWoche)+", "+df.format(IstStundenDieserWoche)+"],\r\n"
					+ "            backgroundColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderWidth: 1\r\n"
					+ "        }]\r\n"
					+ "    },\r\n"
					+ "    options: {\r\n"
					+ "        scales: {\r\n"
					+ "            y: {\r\n"
					+ "                display: false,\r\n"
					+ "                beginAtZero: true\r\n"
					+ "            }\r\n"
					+ "        },\r\n"
					+ "    	   plugins: {"
					+ "            legend: {"
					+ "          	display: false"
		            + "              }"
					+ "        }"	
					+ "    }\r\n"
					+ "});\r\n"
					+ "</script>");
			out.print("<hr>");

			// --- Jahr ---
			out.print("<div><h1 class=\"text-center\">Jahr</ph1></div>");
			
			// Soll-Stunden dieses Jahr bis heute
			double sollStundenDiesesJahr = Double.valueOf(arbTageSeitDiesesJahr)
					* (Double.valueOf(beanPersInfo.getWochenArbZeit()) / 5);
			df.format(sollStundenDiesesJahr);
			
			// Ist-Stunden des Jahres
			double IstStundenDiesesJahr = StundenDao.getIstStundenJahr(uid, DateHelper.getSqlDate(startDiesesJahr),
			DateHelper.getSqlDate(endeDiesesJahr));
			
			//Auflistung der Infos
			out.print("   <div class=\"container\">\r\n"
					+ "        <div class=\"row\">\r\n"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Soll"
					+ "            </div>"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		"+df.format(sollStundenDiesesJahr)+" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Ist"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "            		" +df.format(IstStundenDiesesJahr)+ " h"
					+ "            </div>\r\n"
					+ "        </div>\r\n"
					+ "        <div class=\"row\">"
					+ "            <div class=\"col-8 p-3\">"
					+ "                Saldo"
					+ "            </div>\r\n"
					+ "            <div class=\"col-4 p-3\">"
					+ "					"+ df.format(IstStundenDiesesJahr-sollStundenDiesesJahr) +" h"
					+ "            </div>"
					+ "        </div>\r\n"
					+ "    </div>");
			
			//Pie Diagramm
			out.print("<canvas id=\"myChart3\"></canvas>\r\n"
					+ "<script>\r\n"
					+ "var ctx = document.getElementById('myChart3').getContext('2d');\r\n"
					+ "var myChart3 = new Chart(ctx, {\r\n"
					+ "    type: 'bar',\r\n"
					+ "    data: {\r\n"
					+ "        labels: [\"Soll\", \"Ist\"],"
					+ "        datasets: [{\r\n"
					+ "            label: 'heutige Stunden',\r\n"
					+ "            data: ["+df.format(sollStundenDiesesJahr)+", "+df.format(IstStundenDiesesJahr)+"],\r\n"
					+ "            backgroundColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderColor: [\r\n"
					+ "                'rgba(255, 99, 132)',\r\n"
					+ "                'rgba(75, 192, 192)'\r\n"
					+ "            ],\r\n"
					+ "            borderWidth: 1\r\n"
					+ "        }]\r\n"
					+ "    },\r\n"
					+ "    options: {\r\n"
					+ "        scales: {\r\n"
					+ "            y: {\r\n"
					+ "                display: false,\r\n"
					+ "                beginAtZero: true\r\n"
					+ "            }\r\n"
					+ "        },\r\n"
					+ "    	   plugins: {"
					+ "            legend: {"
					+ "          	display: false"
		            + "              }"
					+ "        }"	
					+ "    }\r\n"
					+ "});\r\n"
					+ "</script>");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		out.print("	<nav class=\"navbar fixed-bottom navbar-light bg-light\">\r\n"
				+ "		<a class=\"navbar-brand\"> © 2021 Ennis Aliu, TEKO Basel</a>\r\n" + "	</nav>");

		out.print(
				"<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>");
		out.print("<script\r\n"
				+ "		src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js\"\r\n"
				+ "		integrity=\"sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ\"\r\n"
				+ "		crossorigin=\"anonymous\"></script>");
		out.close();
	}

}
