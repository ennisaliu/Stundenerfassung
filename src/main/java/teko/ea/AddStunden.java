package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teko.ea.db.StundenDao;

/**
 * Servlet implementation class AddStunden
 */
//Servlet für das einfügen der Stunden mit Exception Handling und Validation
@WebServlet("/AddStunden")
public class AddStunden extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Sessionmanangement
		if (!SessionUtil.isValidUserSession(request)) {
			RequestDispatcher rd = request.getRequestDispatcher("index.html");
			rd.forward(request, response);
			return;
		}
		
		//Validation der Benutzereingabe.
		//Fehlerrückgabe bei nicht validen Werten mit StringBuffer
		StringBuffer errorBuf = new StringBuffer();

		String description = request.getParameter("description");
		
		//Stunden pro Eintrag
		float totalHours = 0;
		try {
			totalHours = Float.parseFloat(request.getParameter("totalHours"));
		} catch (NumberFormatException e) {
			errorBuf.append("Ungültige Total Stunden. Muss Decimal-Punkt sein.");
			errorBuf.append("<br>");
		}
		
		//vonZeit
		int fromTime = 0;
		try {
			fromTime = Integer.parseInt(request.getParameter("fromTime"));
		} catch (NumberFormatException e) {
			errorBuf.append("Ungültige Zeit. Muss im 2400-Format sein.");
			errorBuf.append("<br>");
		}
		
		//bisZeit
		int toTime = 0;
		try {
			toTime = Integer.parseInt(request.getParameter("toTime"));
		} catch (NumberFormatException e) {
			errorBuf.append("Ungültige Zeit. Muss im 2400-Format sein.");
			errorBuf.append("<br>");
		}
		
		String dateStr = request.getParameter("date");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateStr); //Datum zu SimpleDateFormat parsen
		} catch (ParseException e) {
			errorBuf.append("Ungültiges Datum. Muss yyyy-MM-dd-Format sein.");
			errorBuf.append("<br>");
		}
		//Wenn Error vorhanden (länger >
		if (errorBuf.length() != 0) {
			writeForm(request, response, errorBuf.toString());
			return;
		}

		//Stunden des aktuell eingeloggten (Session) Benutzers
		Emp emp = SessionUtil.getCurrentUser(request); 
		Stunden stunden = new Stunden();
		stunden.setUid(emp.getId());
		stunden.setStundenTyp(description);
		stunden.setDate(date);
		stunden.setTotalHours(totalHours);
		stunden.setFromTime(fromTime);
		stunden.setToTime(toTime);

		//Stunden speichern mit save Methode inkl. Exception-Handling
		try {
			boolean success = StundenDao.save(stunden);
			if (success) {
				writeForm(request, response, null, "Stunden gespeichert.");

			} else {
				writeForm(request, response, "Fehler beim Speichern der Stunden.", null);
			}
		} catch (Exception e) {
			errorBuf.append("Fehler beim Verbindung zur Datenbank.");
			errorBuf.append("<br>");
			writeForm(request, response, errorBuf.toString(), null);
			return;
		}

	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Sessionamangement. Ist Session valid? ansonsten redirect zu index
		if (!SessionUtil.isValidUserSession(request)) {
			RequestDispatcher rd = request.getRequestDispatcher("index.html");
			rd.forward(request, response);
			return;
		}
		
		writeForm(request, response, null, null);

	}
	//Ausgabe bei Exception 
	private void writeForm(HttpServletRequest request, HttpServletResponse response, String errorMessage)
			throws IOException {

		writeForm(request, response, errorMessage, null);

	}
	
	private void writeForm(HttpServletRequest request, HttpServletResponse response, String errorMessage, String info)
			throws IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/styles.css\">\r\n"
				+ "<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css\" />\r\n"
				+ "<script src=\"http://code.jquery.com/jquery-1.8.3.js\"></script>\r\n"
				+ "<script src=\"http://code.jquery.com/ui/1.9.2/jquery-ui.js\"></script>");
		
		out.println("<script>\r\n"
				+ "$(function() {\r\n"
				+ "    $( \"#datepicker\" ).datepicker({\r\n"
				+ "    	dateFormat: 'yy-mm-dd',\r\n"
				+ "        dayNamesMin: ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'],\r\n"
				+ "        firstDay: 1,\r\n"
				+ "        monthNames: ['Januar', 'Februar', 'M&auml;rz', 'April', 'Mai', 'Juni',\r\n"
				+ "            'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],\r\n"
				+ "        monthNamesShort: ['Jan', 'Feb', 'M&auml;r', 'Apr', 'Mai', 'Jun',\r\n"
				+ "            'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez']\r\n"
				+ "    });\r\n"
				+ "});\r\n"
				+ "</script>\r\n");
		
		out.println("<script type=\"text/javascript\">\r\n"
				+ "$(document).ready( function() {\r\n"
				+ "    var now = new Date();\r\n"
				+ "    var day = (\"0\" + now.getDate()).slice(-2);\r\n"
				+ "    var month = (\"0\" + (now.getMonth() + 1)).slice(-2);\r\n"
				+ "    var today = now.getFullYear()+\"-\"+(month)+\"-\"+(day) ;\r\n"
				+ "   $('#datepicker').val(today);\r\n"
				+ "    });\r\n"
				+ "});\r\n"
				+ "</script>");
		
		
		// Navigationsleiste
		out.println("	<nav class=\"navbar fixed-top navbar-expand-lg navbar-light bg-light\">\r\n"
				+ "		<div class=\"container-fluid\">\r\n"
				+ "			<a class=\"navbar-brand\" href=\"home.jsp\"><i class=\"bi bi-house\"></i></a>\r\n"
				+ "			<a class=\"navbar-brand\">Stunden erfassen</a>"
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
				+ "					<li class=\"nav-item\"><a class=\"nav-link\" href=\"LogoutServlet\">Abmelden</a>\r\n"
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
		
		//Ausgabe von Info msg, wenn Info vorhanden
		if (info != null) {
			out.println("<font color=\"blue\">" + info + "</font>");
			out.println("<br><br>");
		}
		//Ausgabe der Fehlermeldung, wenn Fehler vorhanden
		if (errorMessage != null) {
			out.println("<font color=\"red\">" + errorMessage + "</font>");
			out.println("<br><br>");
		}
		
		LocalDateTime heute = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		out.print("		<form action='AddStunden' method='post'>\r\n"
				+ "			<div class=\"form-group\">\r\n"
				+ "				<label for=\"datepicker\">Datum</label>\r\n"
				+ "				<input class=\"form-control\" type=\"text\" name=\"date\" value='" + formatter.format(heute) + "'id=\"datepicker\">\r\n"
				+ "			</div>\r\n"
				+ "			<div class=\"form-group\">\r\n"
				+ "				<label for=\"fromTime\">von Zeit</label>\r\n"
				+ "				<input class=\"form-control\" type=\"text\" name=\"fromTime\" id=\"fromTime\" />\r\n"
				+ "			</div>\r\n"
				+ "			<div class=\"form-group\">\r\n"
				+ "				<label for=\"datepicker\">bis Zeit</label>\r\n"
				+ "				<input class=\"form-control\" type=\"text\" name=\"toTime\" id=\"toTime\"/>\r\n"
				+ "			</div>\r\n"
				+ "			<div class=\"form-group\">	\r\n"
				+ "				<label for=\"totalHours\">effektive Stunden</label>\r\n"
				+ "				<input class=\"form-control\" type=\"text\" name=\"totalHours\" id=\"totalHours\"/>\r\n"
				+ "			</div>\r\n"
				+ "			<div class=\"form-group\">\r\n"
				+ "				<label for=\"description\">Beschreibung</label>\r\n"
				+ "				<input class=\"form-control\" type=\"text\" name=\"description\" id=\"description\"/>\r\n"
				+ "			</div>\r\n"
				+ "			<br>\r\n"
				+ "			<div class=\"btn-group d-flex justify-content-around\">\r\n"
				+ "			<button type=\"submit\" class=\"btn btn-primary\" type=\"submit\" value=\"Speichern\">Speichern</button>\r\n"
				+ "			<a href='home.jsp' class=\"btn btn-danger\">Abbrechen</a>\r\n"
				+ "			</div>\r\n"
				+ "		</form>");
	
		out.print("	<nav class=\"navbar fixed-bottom navbar-light bg-light\">\r\n"
				+ "		<a class=\"navbar-brand\"> © 2021 Ennis Aliu, TEKO Basel</a>\r\n</nav>");
		
		out.println("<script\r\n"
				+ "		src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js\"\r\n"
				+ "		integrity=\"sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ\"\r\n"
				+ "		crossorigin=\"anonymous\"></script>");
		out.close();

	}

}
