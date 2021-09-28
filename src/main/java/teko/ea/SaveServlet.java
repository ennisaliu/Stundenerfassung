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
 * Servlet implementation class SaveServlet
 */
//Speichern des Benutzers
@WebServlet("/SaveServlet")
public class SaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String country = request.getParameter("country");

		Emp bean = new Emp();
		bean.setName(name);
		bean.setPassword(password);
		bean.setEmail(email);
		bean.setCountry(country);

		int status = EmpDao.save(bean);
		if (status > 0) {
			out.print("<p>Benutzer erfolgreich gepseichert.</p>");
			request.getRequestDispatcher("users.jsp").include(request, response);
		} else {
			out.println("Speichern nicht möglich.");
		}

		out.close();
	}

}
