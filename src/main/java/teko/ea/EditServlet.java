package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teko.ea.db.EmpDao;

/**
 * Servlet implementation class EditServlet
 */
//Editorform für die Bearbeitung eines bestehenden Benutzers.
@WebServlet("/EditServlet")
public class EditServlet extends HttpServlet {
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
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/styles.css\">");
		
		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);

		Emp bean = EmpDao.getEmployeeById(id);
		
		// Navigationsleiste
		out.println("	<nav class=\"navbar fixed-top navbar-expand-lg navbar-light bg-light\">\r\n"
				+ "		<div class=\"container-fluid\">\r\n"
				+ "			<a class=\"navbar-brand\" href=\"home.jsp\"><i class=\"bi bi-house\"></i></a>\r\n"
				+ "			<a class=\"navbar-brand\">Benutzer bearbeiten</a>"
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
				+ "							<li><a class=\"dropdown-item\" href=\"ViewMyFerienServlet\">meine\r\n"
				+ "									Ferienbilanz</a></li>\r\n"
				+ "							<li><hr class=\"dropdown-divider\"></li>\r\n"
				+ "							<li><a class=\"dropdown-item\" href=\"ViewStundenServlet\">alle\r\n"
				+ "									Mitarbeiterstunden</a></li>\r\n"
				+ "						</ul>\r\n"
				+ "					<li class=\"nav-item\"><a class=\"nav-link\"\r\n"
				+ "						href=\"ViewMyPersInfo\">meine Personalinfos</a></li>\r\n"
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
		
		out.println("<form action='EditServlet2' method='post'>\r\n"
				+ "  <div class=\"form-group\">\r\n"
				+ "      <input type=\"hidden\" class=\"form-control\" name='id' value='" + bean.getId() + "'/>\r\n"
				+ "    <div class=\"form-group\">\r\n"
				+ "      <label>Name</label>\r\n"
				+ "      <input type=\"text\" class=\"form-control\" name='name' value='" + bean.getName() + "'/>\r\n"
				+ "    </div>\r\n"
				+ "    <div class=\"form-group\">\r\n"
				+ "      <label>Passwort</label>\r\n"
				+ "      <input type=\"password\" class=\"form-control\" type='hidden' name='password' value='" + bean.getPassword() + "'/>\r\n"
				+ "    </div>\r\n"
				+ "    <div class=\"form-group\">\r\n"
				+ "      <label>Email</label>\r\n"
				+ "      <input type=\"email\" class=\"form-control\" name='email' value='" + bean.getEmail() + "'/>\r\n"
				+ "    </div>\r\n"
				+ "  </div>\r\n"
				+ "  <div class=\"form-row\">\r\n"
				+ "    <div class=\"form-group\">\r\n"
				+ "      <label>Land</label>\r\n"
				+ "      <select name='country' class=\"form-control\">\r\n"
				+ "        <option selected>Schweiz</option>\r\n"
				+ "        <option>Deutschland</option>\r\n"
				+ "        <option>anderes</option>\r\n"
				+ "      </select>\r\n"
				+ "    </div>\r\n"
				+ "  </div>\r\n"
				+ "  </br>\r\n"
				+ "  <div class=\"btn-group d-flex justify-content-around\" role=\"group\" aria-label=\"Speichern_und_Abbrechen\">"
				+ "    <button type=\"submit\" class=\"btn btn-primary\">Speichern</button>\r\n"
				+ "    <a href=\"ViewUsersServlet\" class=\"btn btn-danger\">Abbrechen</a>"
				+ "</form>");
		
		out.print("	<nav class=\"navbar fixed-bottom navbar-light bg-light\">\r\n"
				+ "		<a class=\"navbar-brand\"> © 2021 Ennis Aliu, TEKO Basel</a>\r\n</nav>");
		
		out.println("<script\r\n"
				+ "		src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js\"\r\n"
				+ "		integrity=\"sha384-/bQdsTh/da6pkI1MST/rWKFNjaCP5gBSY4sEBT38Q/9RBh9AH40zEOg7Hlq2THRZ\"\r\n"
				+ "		crossorigin=\"anonymous\"></script>");
		out.close();
	}

}
