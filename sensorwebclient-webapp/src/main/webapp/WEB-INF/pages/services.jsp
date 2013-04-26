<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'services')}" />
</head>
<body>
	${serviceInstanceList[0]}
	<c:if test="${not empty serviceInstanceList}">
		<c:choose>
			<c:when test="${fn:length(serviceInstanceList) eq 1}">
				<!-- just one entry -->
				Genau 1
			</c:when>
			<c:otherwise>
				<!-- more than one entry -->
				<h3>Services:</h3>
				<ul>
					<c:forEach items="${serviceInstanceList}" var="service">
						<li><a href="${url}services/${service.itemName}">${service.itemName}</a></li>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>
	</c:if>

	<c:if test="${not empty serviceInstance}">
			<table>
				<tr>
					<td>Item</td>
					<td>${serviceInstance.itemName}</td>
				</tr>
				<tr>
					<td>Title</td>
					<td>${serviceInstance.title}</td>
				</tr>
				<tr>
					<td>Url</td>
					<td>${serviceInstance.url}</td>
				</tr>
				<tr>
					<td>Type</td>
					<td>${serviceInstance.type}</td>
				</tr>
				<tr>
					<td>Version</td>
					<td>${serviceInstance.version}</td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${serviceInstance.itemName}/offerings">offerings</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${serviceInstance.itemName}/features">features</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${serviceInstance.itemName}/procedures">procedures</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${serviceInstance.itemName}/phenomenons">phenomenons</a></td>
				</tr>
				<tr>
					<td colspan="2"><a href="${url}services/${serviceInstance.itemName}/stations">stations</a></td>
				</tr>
			</table>
		<a href="${url}services">back</a>
	</c:if>
</body>
</html>