<%--

    Copyright (C) ${inceptionYear}-${latestYearOfContribution} 52°North Initiative for Geospatial Open Source
    Software GmbH

    This program is free software; you can redistribute it and/or modify it under
    the terms of the GNU General Public License version 2 as publishedby the Free
    Software Foundation.

    If the program is linked with libraries which are licensed under one of the
    following licenses, the combination of the program with the linked library is
    not considered a "derivative work" of the program:

        - Apache License, version 2.0
        - Apache Software License, version 1.0
        - GNU Lesser General Public License, version 3
        - Mozilla Public License, versions 1.0, 1.1 and 2.0
        - Common Development and Distribution License (CDDL), version 1.0

    Therefore the distribution of the program linked with libraries licensed under
    the aforementioned licenses, is permitted by the copyright holders if the
    distribution is compliant with both the GNU General Public License version 2
    and the aforementioned licenses.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
    PARTICULAR PURPOSE. See the GNU General Public License for more details.

--%>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="keywords" lang="en" content="Sensor Web, SWE, OGC, SOS, SES, Data">
<meta name="gwt:property" content="locale=<%=request.getLocale()%>">
<meta name="author" content="52&deg;North - http://52north.org/">
<meta name="DC.title" content="Sensor Web Client ${project.version}">
<meta name="DC.creator" content="52&deg;North - http://52north.org/">
<meta name="DC.subject" content="Sensor Web Client including SOS and SES support">

<title>${application.title}</title>

<link rel="shortcut icon" href="img/fav.png" type="image/x-icon">
<link rel="icon" href="img/fav.png" type="image/x-icon">
<link rel="schema.DC" href="http://purl.org/dc/elements/1.1/">
<link rel="shortcut icon" href="img/fav.png" type="image/x-icon">
<link rel="icon" href="img/fav.png" type="image/x-icon">
<link href="css/layout.css" rel="stylesheet">
</head>

<body>
	<div id="loadingWrapper">
		<div id="spacer"></div>
		<div class="loadingIndicator">
			<img src="img/loader.gif" width="32" height="32" style="margin-right: 8px; float: left; vertical-align: middle;" />
			<div id="operator">52&deg;North</div>
			<span id="loadingMsg">Loading ${application.title}</span>
		</div>
	</div>
    <script type="text/javascript" src="client/client.nocache.js"></script>
    <script type="text/javascript" src="js/OpenLayers/OpenLayers.js"></script>
    <script type="text/javascript" src="js/proj4js-compressed.js"></script>
    <script type="text/javascript" src="js/OpenStreetMap.js"></script>
    <script type="text/javascript" src="js/bookmark.js"></script>
    <script type="text/javascript" src="js/json2.js"></script>
</body>
</html>
