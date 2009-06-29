<%-- Ceci est un commentaire JSP --%>
<%@page contentType="text/html"%>
<%@page errorPage="erreur.jsp"%>
<%-- Importation d'un paquetage (package) --%>
<%@page import="java.util.*"%>
<html>
<head><title>Page JSP</title></head>
<body>

<%-- D�claration d'une variable globale � la classe --%>
<%! int nombreVisites = 0; %>

<%-- D�finition de code Java --%>
<% //Il est possible d'�crire du code Java ici
    Date date = new Date();
    // On peut incr�menter une variable globale pour compter le nombre
    // d'affichage, par exemple.
    nombreVisites++;
%>
<h1>Exemple de page JSP</h1>
<%-- Impression de variables --%>
<p>Au moment de l'ex�cution de ce script, nous sommes le <%= date %>.</p>
<p>Cette page a �t� affich�e <%= nombreVisites %> fois!</p>
</body>
</html>

