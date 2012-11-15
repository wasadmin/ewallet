<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%-- jsf:pagecode language="java" location="/src/pagecode/Ussd.java" --%><%-- /jsf:pagecode --%><%@page
	language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<html>
<head>
<title>USSD Simulator</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="GENERATOR"
	content="Rational® Application Developer™ for WebSphere® Software">
<link rel="stylesheet" type="text/css" title="Style"
	href="theme/stylesheet.css">
</head>
<f:view>
	<body>
	<hx:scriptCollector id="scriptCollector1">
		<h:form styleClass="form" id="form1">
			<hx:panelFormBox helpPosition="over" labelPosition="left"
				styleClass="panelFormBox" id="formBox1" label="USSD Simulator">


				<hx:formItem styleClass="formItem" id="formItem3"
					label="Transaction ID">
					<h:outputText styleClass="outputText" id="text1" value="#{ussdBean.transactionIdLabel}"></h:outputText>
				</hx:formItem>
				
				<hx:formItem styleClass="formItem" id="formItem5" label="Source Mobile">
					<h:inputText styleClass="inputText" id="text2"
						value="#{ussdBean.sourceMobile}"></h:inputText>
				</hx:formItem>
				<hx:formItem styleClass="formItem" id="formItem1" label="New Session">
					<h:selectBooleanCheckbox styleClass="selectBooleanCheckbox"
						id="checkbox1" value="#{ussdBean.newSession}"></h:selectBooleanCheckbox>
				</hx:formItem>
				<hx:formItem styleClass="formItem" id="formItem6" label="Screen">
					<h:inputTextarea styleClass="inputTextarea" id="textarea1"
						cols="50" rows="15" readonly="true" value="#{ussdBean.menu}"></h:inputTextarea>
				</hx:formItem>
				<hx:formItem styleClass="formItem" id="formItem2"
					label="USSD Request Message  : ">
					<h:inputText styleClass="inputText" id="text4"
						value="#{ussdBean.message}"></h:inputText>
				</hx:formItem>

				<f:facet name="bottom">
					<h:panelGrid styleClass="panelGrid" id="grid1">
						<hx:commandExButton  value="Submit"
							styleClass="commandExButton" id="button1"
							action="#{ussdBean.submitRequest}" type="submit"></hx:commandExButton>
					</h:panelGrid>
				</f:facet>
			</hx:panelFormBox>

			<h:inputHidden value="#{ussdBean.transactionId}" id="Id1"></h:inputHidden>
		</h:form>
	</hx:scriptCollector>
	</body>
</f:view>
</html>