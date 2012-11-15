<%-- jsf:pagecode language="java" location="/src/pagecode/teller/Index.java" --%><%-- /jsf:pagecode --%><%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%><%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<html>
<head />
<link rel="stylesheet" type="text/css" title="Style"
	href="../theme/stylesheet.css">
</head>
<f:view>
	<body>
	<hx:scriptCollector id="scriptCollector1">
		<jsp:forward page="ICEfacesPage1.iface" />
		<h:outputText styleClass="outputLabel" id="text1" value="XXXXX"
			style="color: green"></h:outputText>
	</hx:scriptCollector>
	</body>
</f:view>
</html>