package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.PersistentMBean;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teko.ea.db.EmpDao;
import teko.ea.db.PersInfoDao;
import teko.ea.db.StundenDao;

/**
 * Servlet implementation class ViewArbeitStundenServlet
 */
@WebServlet("/ViewArbeitStundenServlet")
public class ViewArbeitStundenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Sessionmanangement und Prüfung valider Session
		if (!SessionUtil.isValidUserSession(request)) {
			RequestDispatcher rd = request.getRequestDispatcher("LogoutServlet");
			rd.forward(request, response);
		}
		
		// Error StringBuffer für Validation der Benutzereingabe
		StringBuffer errorBuf = new StringBuffer();
		
		int uid;
		try {
			uid = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			errorBuf.append("Ungültige Zeit. Muss im 2400-Format sein.");
			errorBuf.append("<br>");

			request.setAttribute("errorMessage", errorBuf.toString());

			RequestDispatcher rd = request.getRequestDispatcher("ViewStundenServlet");
			rd.forward(request, response);
			return;
		}
		//
		Emp emp = EmpDao.getEmployeeById(uid);
		// Fehlermeldung wenn Emp.id nicht vorhanden ist
		if (emp == null) {
			errorBuf.append("Kein Mitarbeiter mit " + uid + "gefunden.");
			errorBuf.append("<br>");

			request.setAttribute("errorMessage", errorBuf.toString());

			RequestDispatcher rd = request.getRequestDispatcher("ViewStundenServlet");
			rd.forward(request, response);
			return;
		}

		List<Stunden> list;
		try {
			list = StundenDao.getStundenForEmp(uid);
		} catch (Exception e) {
			e.printStackTrace();
			errorBuf.append("Fehler beim Verbinden zur Datenbank.");
			errorBuf.append("<br>");

			request.setAttribute("errorMessage", errorBuf.toString());

			RequestDispatcher rd = request.getRequestDispatcher("ViewStundenServlet");
			rd.forward(request, response);
			return;
		}

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/styles.css\">");
		
		// Navigationsleiste
		out.println("	<nav class=\"navbar fixed-top navbar-expand-lg navbar-light bg-light\">\r\n"
				+ "		<div class=\"container-fluid\">\r\n"
				+ "			<a class=\"navbar-brand\" href=\"home.jsp\"><i class=\"bi bi-house\"></i></a>\r\n"
				+ "			<a class=\"navbar-brand\">Stunden von " + emp.getName() +"</a>"
				+ "			<button class=\"navbar-toggler\" type=\"button\"\r\n"
				+ "				data-bs-toggle=\"collapse\" data-bs-target=\"#navbarSupportedContent\"\r\n"
				+ "				aria-controls=\"navbarSupportedContent\" aria-expanded=\"false\"\r\n"
				+ "				aria-label=\"Toggle navigation\">\r\n"
				+ "				<span class=\"navbar-toggler-icon\"></span>\r\n"
				+ "			</button>\r\n"
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
				+ "							<li class=\"nav-item\"><a class=\"nav-link\"\r\n"
				+ "								href=\"ViewMyPersInfo\">meine Personalinfos</a></li>\r\n"
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
				+ "					</li>\r\n"
				+ "\r\n"
				+ "				</ul>\r\n"
				+ "				<form class=\"d-flex\">\r\n"
				+ "					<input class=\"form-control me-2\" type=\"search\" placeholder=\"Suche\"\r\n"
				+ "						aria-label=\"Search\">\r\n"
				+ "					<button class=\"btn btn-outline-success\" type=\"submit\">Suche</button>\r\n"
				+ "				</form>\r\n"
				+ "			</div>\r\n"
				+ "		</div>\r\n"
				+ "	</nav>");
		
		// Personeninfos von Bean
		PersInfo beanPersInfo = null;
		try {
			beanPersInfo = PersInfoDao.getPersInfobyID(uid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Stunden nach Date gruppieren
		TreeMap<Date, List<Stunden>> dateTimeMap = new TreeMap<>();
		TreeMap<Date, Float> dateTotalStundenap = new TreeMap<>();
		TreeSet<Date> dateSet = new TreeSet<>();
		for (Stunden bean : list) {
			Date date = bean.getDate();

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

			dateByList.add(bean);
			totalStunden += bean.getTotalStunden();
			dateTotalStundenap.put(date, totalStunden);
		}

		// Datum Mapping
		dateTimeMap.keySet();
		
		// Totalstunden formatieren
		for (Date date : dateSet) {
			float totalStunden = 0;
			
			DateFormat formatter = new SimpleDateFormat("EEEE, dd.MM.yyyy",Locale.GERMAN);
			
			out.print("<div class=\"container\"><h3 class=\"text-center text-primary\">"+formatter.format(date)+"</h3></div>");
			out.print("<div class=\"container\">");

			// Stunden pro Tag auflisten
			List<Stunden> stundenList = dateTimeMap.get(date);
			for (Stunden bean : stundenList) {
				
				//Format für von und bis Zeit
				//Formatter formatZeit = new Formatter();
				//Formatter vonZeit = formatZeit.format("%tR", bean.getFromTime());
				//Formatter bisZeit = formatZeit.format("%tR", bean.getFromTime());
				//String vonZeit = String.format("%04d", bean.getFromTime());
				//String bisZeit = String.format("%04d", bean.getToTime());
				
				DecimalFormat df = new DecimalFormat("00,00");
				String vonZeit = df.format(bean.getFromTime());
				String bisZeit = df.format(bean.getToTime());
				out.print("<div class=\"d-flex flex-row bg-light text-dark\">");
				out.print("<div class=\"p-2\">"+ vonZeit +" - "+ bisZeit + " Uhr"+ "</div>");
				out.print("<div class=\"p-2\">"+ bean.getTotalStunden() + " h"+ "</div>");
				out.print("<div class=\"p-2\">"+ bean.getStundenTyp() + "</div>");
				out.print("</div>");
				// Totalstunden zusammenrechnen
				totalStunden += bean.getTotalStunden();
				
			}
			out.print("</div>");

			float sollZeitTag = beanPersInfo.getWochenArbZeit()/5;
			float diffZeit = totalStunden - sollZeitTag;
			DecimalFormat df = new DecimalFormat("00.00");
			
			//Zusammenfassung des Tages
			out.print("<div class=\"container\">");
			out.print("<div class=\"d-flex flex-row justify-content-around text-primary\">");
			out.print("<div class=\"p-2\">  Soll  </div>");
			out.print("<div class=\"p-2\">  Ist  </div>");
			out.print("<div class=\"p-2\">  Saldo  </div>");
			out.print("</div>");
			out.print("<div class=\"d-flex flex-row justify-content-around bg-light text-dark\">");
			out.print("<div class=\"p-2\">"+df.format(sollZeitTag)+ " h"+ "</div>");
			out.print("<div class=\"p-2\">"+df.format(totalStunden)+ " h"+"</div>");
			out.print("<div class=\"p-2\">"+df.format(diffZeit)+ " h"+"</div>");
			out.print("</div>");
			out.print("</div><br>");
			
			
			}
		
		out.print("	<nav class=\"navbar fixed-bottom navbar-light bg-light\">\r\n"
				+ "		<a class=\"navbar-brand\"> © 2021 Ennis Aliu, TEKO Basel</a>\r\n" + "	</nav>");
		
		out.println("<script\r\n"
				+ "		src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js\"\r\n"
				+ "		integrity=\"sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ\"\r\n"
				+ "		crossorigin=\"anonymous\"></script>");
		out.close();
	}
}