<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9">
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
			<img src="img/loader.gif" alt="loader" width="32" height="32" style="margin-right: 8px; float: left; vertical-align: middle;" />
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
    
    <script type="text/javascript" src="client/sc/modules/ISC_Core.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_Foundation.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_Containers.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_Grids.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_Forms.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_RichTextEditor.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_Calendar.js"></script>
    <script type="text/javascript" src="client/sc/modules/ISC_DataBinding.js"></script>
    <script type="text/javascript" src="client/sc/skins/Enterprise/load_skin.js"></script>
    
    <script type="text/javascript" src="client/js/gwt-openlayers/util.js"></script>
</body>
</html>
