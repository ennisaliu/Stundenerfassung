<%@ page import="teko.ea.SessionUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%
//Sessionmanagement
if (!SessionUtil.isValidUserSession(request)) {
	response.sendRedirect("index.html"); //Rückleitung auf index, wenn Session nicht valid ist
	return;
}

%> 
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="./css/styles.css">
<title>Benutzer erstellen</title>
</head>
<body>

	<div class="topnav" id="myTopnav">
		<a href="home.jsp">Home</a>
		<a href="ViewUsersServlet">Benutzerübersicht</a>
		<a href="ViewMyStundenServlet">Meine Stunden</a>
		<a href="ViewStundenServlet">Mitarbeiterstunden</a>
		<a href="LogoutServlet" style="color:red;">Logout</a>
	</div>

	<h1>Benutzer erstellen</h1>
	<form action="SaveServlet" method="post">
		<table>
			<tr>
				<td>Name:</td>
				<td><input type="text" name="name" /></td>
			</tr>
			<tr>
				<td>Passwort:</td>
				<td><input type="password" name="password" /></td>
			</tr>
			<tr>
				<td>Email:</td>
				<td><input type="email" name="email" /></td>
			</tr>
			<tr>
				<td>Land:</td>
				<td><select name="country" style="width: 150px">
						<option>Schweiz</option>
						<option>Deutschland</option>
						<option>anderes</option>
				</select></td>
			</tr>
			<tr>
				<td><input type="submit" value="Benutzer speichern" /></td>
				<td><a href='ViewUsersServlet' class="btn btn-danger navbar-btn">Abbrechen</a></td>
			</tr>
		</table>
	</form>

</body>
</html>