package teko.ea;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teko.ea.db.EmpDao;

/**
 * Servlet implementation class EditServlet2
 */
//Speichern der Benutzerdaten gem. Userinput
@WebServlet("/EditServlet2")
public class EditServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String sid = request.getParameter("id");
		int id = Integer.parseInt(sid);
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String country = request.getParameter("country");

		Emp bean = new Emp();
		bean.setId(id);
		bean.setName(name);
		bean.setPassword(password);
		bean.setEmail(email);
		bean.setCountry(country);

		int status = EmpDao.update(bean);
		if (status > 0) {
			response.sendRedirect("ViewUsersServlet");
		} else {
			out.println("Speichern nicht möglich.");
		}

		out.close();
	}

}
