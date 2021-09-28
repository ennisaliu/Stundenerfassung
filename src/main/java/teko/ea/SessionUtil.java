package teko.ea;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


//Sessionhelfer
public class SessionUtil {

	public static boolean isValidUserSession(HttpServletRequest request) {
		HttpSession session = request.getSession(); //Session holen. (false) damit keine neue erzeugt wird
		Emp currentUser = (Emp) session.getAttribute("currentSessionUser");
		return (currentUser != null);
	}
	
	public static Emp getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (Emp) session.getAttribute("currentSessionUser");
	}

}
