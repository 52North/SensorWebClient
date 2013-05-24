<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
    value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'services')}" />
</head>
<body>


    <c:if test="${not empty service}">
        
        <!-- A SERVICE INDIVIDUUM -->
        
        <a href="${url}services">back</a>
    
        <h3>Service</h3>
        
        <table>
            <thead>
                <tr>
                    <th>Configured Name</th>
                    <th>Service Title</th>
                    <th>Service Url</th>
                    <th>Service Type</th>
                    <th>Service Version</th>
                </tr>
            </thead>
            <tr>
                <td>${service.itemName}</td>
                <td>${service.title}</td>
                <td><a href="${service.url}">${service.url}</a></td>
                <td>${service.type}</td>
                <td>${service.version}</td>
            </tr>
        </table>
        
        <h4>Available Parameters</h4>
        
        <ul>
            <li><a href="${url}services/${service.itemName}/offerings">offerings</a></li>
            <li><a href="${url}services/${service.itemName}/features">features</a></li>
            <li><a href="${url}services/${service.itemName}/procedures">procedures</a></li>
            <li><a href="${url}services/${service.itemName}/phenomenons">phenomenons</a></li>
            <li><a href="${url}services/${service.itemName}/stations">stations</a></li>
        </ul>
    </c:if>

    <c:if test="${not empty services}">
    
        <!-- ALL SERVICES IN A SINGLE LIST -->
        
        <h3>Services</h3>
        <ul>
            <c:forEach var="service" items="${services}" >
                <li><a href="${url}services/${service.itemName}">${service.itemName}</a></li>
            </c:forEach>
        </ul>
    </c:if>

</body>
</html>