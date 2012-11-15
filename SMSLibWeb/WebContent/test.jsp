<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%-- jsf:pagecode language="java" location="/src/pagecode/Test.java" --%><%-- /jsf:pagecode --%><%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<html>
<head>
<title>test</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="GENERATOR"
	content="Rational® Application Developer™ for WebSphere® Software">
<link rel="stylesheet" type="text/css" title="Style"
	href="theme/stylesheet.css">
</head>
<f:view>
	<body>
	<hx:scriptCollector id="collector1">

		<h:form styleClass="form" id="form1">
			<h:messages styleClass="messages" id="messages1"></h:messages>
			<hx:commandExButton styleClass="commandExButton" type="submit"
				value="Submit" id="startServer1"
				action="#{startSmsServerBean.startServer}">
			</hx:commandExButton>
		</h:form>
	</hx:scriptCollector>
	</body>
</f:view>
</html>