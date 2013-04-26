<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'services')}" />
</head>
<body>
	<c:if test="${not empty services}">
		<c:choose>
			<c:when test="${fn:length(services) eq 1}">
				<!-- just one entry -->
				Genau 1
			</c:when>
			<c:otherwise>
				<!-- more than one entry -->
				<h3>Services:</h3>
				<ul>
					<c:forEach items="${services}" var="service">
						<li><a href="${url}services/${service.id}">${service.id}</a></li>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>
	</c:if>

	<c:if test="${not empty service}">
			<table>
				<tr>
					<td>ID</td>
					<td>${service.id}</td>
				</tr>
				<tr>
					<td>URL</td>
					<td>${service.url}</td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${service.id}/offerings">offerings</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${service.id}/features">features</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${service.id}/procedures">procedures</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${service.id}/phenomenons">phenomenons</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${service.id}/stations">stations</a></td>
				</tr>
			</table>
		<a href="${url}services">back</a>
	</c:if>
</body>
</html>