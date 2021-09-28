package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
 * Servlet implementation class ViewMyFerienServlet
 */
@WebServlet("/ViewMyFerienServlet")
public class ViewMyFerienServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
						+ "			<a class=\"navbar-brand\">meine Ferienbilanz</a>"
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
						+ "							<li><hr class=\"dropdown-divider\"></li>\r\n"
						+ "							<li><a class=\"dropdown-item\" href=\"ViewStundenServlet\">alle\r\n"
						+ "									Mitarbeiterstunden</a></li>\r\n"
						+ "						</ul>\r\n"
						+ "					<li class=\"nav-item\"><a class=\"nav-link\"\r\n"
						+ "						href=\"ViewUsersServlet\">Benutzerverwaltung</a></li>\r\n"
						+ "					<li class=\"nav-item\"><a class=\"nav-link\"\r\n"
						+ "						href=\"ViewMyPersInfo\">meine Personalinfos</a></li>\r\n"
						+ "					</li>\r\n"
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

				// ID des Benutzers  holen
				int uid = SessionUtil.getCurrentUser(request).getId();

				//Dezimal Formatierung
				DecimalFormat df = new DecimalFormat("0.00");
				
				try {
					// Personeninfos von Bean
					PersInfo beanPersInfo = PersInfoDao.getPersInfobyID(uid);
					
					// Alternative Startdatum für Berechnung
					int jahr = Calendar.getInstance().get(Calendar.YEAR);
					int woche = Calendar.getInstance().get(Calendar.MONTH);
					int wochentag = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
					Date startDiesesJahr = DateHelper.convertToDateViaSqlDate(LocalDate.of(jahr, 01, 01));
					Date endeDiesesJahr = DateHelper.convertToDateViaSqlDate(LocalDate.of(jahr, 12, 31));
					
					//Feriensoll mit Anfang Jahr oder Eintrittsdatum Mitarbeiter berechnen
					double ferienSoll = Zeitrechner.berechneFerienSoll(beanPersInfo.getEintritt(), beanPersInfo.getFerienSoll(), beanPersInfo.getWochenArbZeit());
					
					
					//Wann beginnen mit Rechnen (Eintrittsdatum oder Anfang Jahr)
					double wannAnfang = Zeitrechner.arbTageZwischen(startDiesesJahr, beanPersInfo.getEintritt());
						//wenn Mitarbeiter nach Beginn des aktuellen Jahres eingetreten ist, wird sein Eintrittsdarum verwendet
						if(wannAnfang>0) {
							startDiesesJahr =  beanPersInfo.getEintritt();
						}
						
					//bezogene Ferien
					double bezogeneFerien = StundenDao.getFerienForEmp(uid, DateHelper.getSqlDate(startDiesesJahr), DateHelper.getSqlDate(endeDiesesJahr));
					
					//übrige Ferien (bezogene Ferien - (Ferien100%*Pensum)
					//double uebrigeFerien =  (beanPersInfo.getFerienSoll()*tagesSoll)-(StundenDao.getFerienForEmp(uid, DateHelper.getSqlDate(startDiesesJahr), DateHelper.getSqlDate(endeDiesesJahr)));
					double uebrigeFerien = ferienSoll - bezogeneFerien;
					
					// Falls mehr als das maximum verfügbarer Ferien bezogen werden, werden dies mit 0 dargestellt.
					// somit sind verfügbare Ferien = 0
					if (uebrigeFerien<0) {
						uebrigeFerien = 0.00;
				
		};
		
		//Auflistung der Informationen
		out.print("   <div class=\"container\">\r\n"
				+ "        <div class=\"row\">\r\n"
				+ "            <div class=\"col-8 p-3\">"
				+ "                Ferienguthaben"
				+ "            </div>"
				+ "            <div class=\"col-4 p-3\">"
				+ "            		"+ df.format(ferienSoll) +" h"
				+ "            </div>"
				+ "        </div>\r\n"
				+ "        <div class=\"row\">"
				+ "            <div class=\"col-8 p-3\">"
				+ "                bezogene Ferien"
				+ "            </div>\r\n"
				+ "            <div class=\"col-4 p-3\">"
				+ "            		"+ df.format(bezogeneFerien) + " h"
				+ "            </div>\r\n"
				+ "        </div>\r\n"
				+ "        <div class=\"row\">"
				+ "            <div class=\"col-8 p-3\">"
				+ "                verfügbare Ferien"
				+ "            </div>\r\n"
				+ "            <div class=\"col-4 p-3\">"
				+ "					"+ df.format(uebrigeFerien) +" h"
				+ "            </div>"
				+ "        </div>\r\n"
				+ "        <div class=\"row\">"
				+ "            <div class=\"col-8 p-3\">"
				+ "                Feriensaldo"
				+ "            </div>\r\n"
				+ "            <div class=\"col-4 p-3\">"
				+ "					"+ df.format(ferienSoll-bezogeneFerien) +" h"
				+ "            </div>"
				+ "        </div>\r\n"
				+ "    </div>");
		
		//Pie Chart
		out.print("<div class=\"chart-container\">");
		out.print("<canvas id=\"myChart\" width=\"200\" height=\"400\"></canvas>\r\n"
				+ "<script>\r\n"
				+ "var ctx = document.getElementById('myChart').getContext('2d');\r\n"
				+ "var myChart = new Chart(ctx, {\r\n"
				+ "    type: 'pie',\r\n"
				+ "    data: {\r\n"
				+ "        labels: [\"bezogene Ferien \", \"übrige Ferien\"],"
				+ "        datasets: [{\r\n"
				+ "            label: 'heutige Stunden',\r\n"
				+ "            data: ["+df.format(bezogeneFerien)+", "+df.format(uebrigeFerien)+"],\r\n"
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
				+ "        }\r\n"
				+ "    }\r\n"
				+ "});\r\n"
				+ "</script>");
		out.print("</div>");
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				out.print("	<nav class=\"navbar fixed-bottom navbar-light bg-light\">\r\n"
						+ "		<a class=\"navbar-brand\"> © 2021 Ennis Aliu, TEKO Basel</a>\r\n"
						+ "	</nav>");
				
				out.print(
						"<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>");
				out.print("<script\r\n"
						+ "		src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js\"\r\n"
						+ "		integrity=\"sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ\"\r\n"
						+ "		crossorigin=\"anonymous\"></script>");
				out.close();
			}


}
