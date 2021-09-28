<%@page import="teko.ea.DateHelper"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Date"%>
<%@page import="teko.ea.db.StundenDao"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="teko.ea.Emp" import="teko.ea.SessionUtil"%>

<%
// Sessionmanangement und Prüfung valider Session
if (!SessionUtil.isValidUserSession(request)) {
	RequestDispatcher rd = request.getRequestDispatcher("LogoutServlet");
	rd.forward(request, response);
}
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Home</title>
<link rel="stylesheet" type="text/css" href="./css/styles.css">
<script src="https://cdn.jsdelivr.net/npm/chart.js@3.5.1/dist/chart.min.js"></script>

</head>
<body>
	<nav class="navbar fixed-top navbar-expand-lg navbar-light bg-light">
		<div class="container-fluid">
			<a class="navbar-brand" href="home.jsp"><i class="bi bi-house"></i></a>
			<a class="navbar-brand">Home</a>
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						role="button" data-bs-toggle="dropdown" aria-expanded="false">
							Stundenübersicht </a>
						<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
							<li><a class="dropdown-item"
								href="ViewMyArbeitStundenServlet">meine Stunden</a></li>
							<li><a class="dropdown-item" href="ViewMyStundenBilanz">meine
									Stundenbilanz</a></li>
							<li><a class="dropdown-item"
							href="ViewMyFerienServlet">meine Ferienbilanz</a></li>
							<li><hr class="dropdown-divider"></li>
							<li><a class="dropdown-item" href="ViewStundenServlet">alle
									Mitarbeiterstunden</a></li>
						</ul>
					<li class="nav-item"><a class="nav-link"
						href="ViewMyPersInfo">meine Personalinfos</a></li>
					<li class="nav-item"><a class="nav-link"
						href="ViewUsersServlet">Benutzerverwaltung</a></li>
					<li class="nav-item"><a class="nav-link disabled">Termine</a>
					</li>
					<li class="nav-item"><a class="nav-link disabled">Aufträge</a>
					</li>
					<li class="nav-item"><a class="nav-link" style="color: red"
						href="LogoutServlet">Abmelden</a></li>

				</ul>
				<form class="d-flex">
					<input class="form-control me-2" type="search" placeholder="Suche"
						aria-label="Search">
					<button class="btn btn-outline-success" type="submit">Suche</button>
				</form>
			</div>
		</div>
	</nav>
	<br><br><br><br><br>
	<div>
		<%
		Emp currentUser = (Emp) session.getAttribute("currentSessionUser");
		String userName = currentUser.getName();
		%>
		
		<h2 class="text-center">Hallo <%=userName%></h2>
	</div>
	<br><br>
	<%
	DateFormat formatter = new SimpleDateFormat("EEEE",Locale.GERMAN);
	String tag = formatter.format(System.currentTimeMillis());
	%>
	<div class="text-center"><p>Heute ist <%=tag%>
	<%
	DateFormat formatter2 = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMAN);
	String datum = formatter2.format(System.currentTimeMillis());
	%>
	, der <%=datum %>.
	</p>
	</div>
	<br>
	<br>
	<div class="d-flex justify-content-around"><a href='AddStunden' class="btn btn-primary">Stunden erfassen</a></div>
		
	<nav class="navbar fixed-bottom navbar-light bg-light">
		<a class="navbar-brand"> © 2021 Ennis Aliu, TEKO Basel</a>
	</nav>

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.1/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.0/jquery.min.js"></script>
</body>
</html>