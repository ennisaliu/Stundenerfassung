package teko.ea;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import teko.ea.db.EmpDao;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		try {

			Emp user = EmpDao.login(request.getParameter("un"), request.getParameter("pw"));

			if (user != null) {

				HttpSession session = request.getSession(true);
				session.setAttribute("currentSessionUser", user);
				
//				response.sendRedirect("home.jsp");
				RequestDispatcher rd = request.getRequestDispatcher("home.jsp");
				rd.forward(request, response);
			}

			else {
//          response.sendRedirect("Login.jsp"); //Fehlerseite
				RequestDispatcher rd = request.getRequestDispatcher("index.html");
				rd.forward(request, response);
			}
		}

		catch (Throwable except) {
			System.out.println(except);
		}
	}
}
