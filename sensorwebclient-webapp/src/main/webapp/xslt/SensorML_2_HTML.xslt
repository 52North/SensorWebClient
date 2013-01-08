<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:gml="http://www.opengis.net/gml"
	xmlns:ism="urn:us:gov:ic:ism:v2"
	xmlns:sch="http://www.ascc.net/xml/schematron"
	xmlns:smil20="http://www.w3.org/2001/SMIL20/"
	xmlns:smil20lang="http://www.w3.org/2001/SMIL20/Language"
	xmlns:sml="http://www.opengis.net/sensorML/1.0"
	xmlns:swe="http://www.opengis.net/swe/1.0"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:altova="http://www.altova.com">
    <xsl:output method="html" encoding="utf-8" indent="yes" />
	<xsl:param name="SV_OutputFormat" select="'HTML'"/>
	<xsl:variable name="XML" select="/"/>
	<xsl:template match="/">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<title/>
				<script language="javascript">
					function linkTo() {
						var link = '<xsl:value-of select="sml:SensorML/sml:member/sml:System/sml:documentation/sml:Document/sml:onlineResource/@xlink:href"/>';
						window.open(link, '', '');
					}
				</script>
			</head>
			<body>
				<xsl:for-each select="$XML">
					<xsl:for-each select="sml:SensorML">
						<xsl:for-each select="sml:member">
							<xsl:for-each select="sml:System">
								<xsl:for-each select="sml:identification">
									<xsl:for-each select="sml:IdentifierList">
										<xsl:for-each select="sml:identifier">
											<xsl:if test="@name  = &quot;stationName&quot;">
												<span>
													<b>Station: </b>
												</span>
												<xsl:for-each select="sml:Term">
													<xsl:for-each select="sml:value">
														<xsl:apply-templates/>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:if>
											<br/>
											<xsl:if test="@name  = &quot;stationID&quot;">
												<span>
													<b>ID: </b>
												</span>
												<xsl:for-each select="sml:Term">
													<xsl:for-each select="sml:value">
														<xsl:apply-templates/>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
								<br/>
								<xsl:for-each select="sml:classification">
									<xsl:for-each select="sml:ClassifierList">
										<xsl:for-each select="sml:classifier">
											<xsl:if test="@name = &quot;sensorType&quot;">
												<span>
													<b>Sensor: </b>
												</span>
												<xsl:for-each select="sml:Term">
													<xsl:for-each select="sml:value">
														<xsl:apply-templates/>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
								<xsl:for-each select="sml:characteristics">
									<xsl:for-each select="swe:SimpleDataRecord">
										<xsl:for-each select="swe:field">
											<br/>
											<xsl:for-each select="swe:Text">
												<xsl:for-each select="gml:description">
													<b><xsl:apply-templates/>: </b>
												</xsl:for-each>
												<xsl:for-each select="swe:value">
													<xsl:apply-templates/>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
								<xsl:for-each select="sml:capabilities">
									<xsl:for-each select="swe:SimpleDataRecord">
										<xsl:for-each select="swe:field">
											<br/>
											<xsl:for-each select="swe:Text">
												<xsl:for-each select="gml:description">
													<b><xsl:apply-templates/>: </b>
												</xsl:for-each>
												<xsl:for-each select="swe:value">
													<xsl:apply-templates/>
												</xsl:for-each>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
								<br/>
								<xsl:for-each select="sml:documentation">
									<xsl:for-each select="sml:Document">
										<xsl:for-each select="gml:description">
											<b><xsl:apply-templates/>: </b>
										</xsl:for-each>
										<span onclick="linkTo();" style="cursor:pointer; color:#994433">
											<xsl:value-of select="./sml:onlineResource/@xlink:href"/>
										</span>
										<a>
											<xsl:attribute name="href">
												<xsl:value-of select="./sml:onlineResource/@xlink:href"/>
											</xsl:attribute>
											<xsl:attribute name="target">
												"_blank"
											</xsl:attribute>
											<xsl:value-of select="./sml:onlineResource/@xlink:href"/>
										</a>
										
									</xsl:for-each>
								</xsl:for-each>
								
								<br/>
								<xsl:for-each select="sml:positions">
									<xsl:for-each select="sml:PositionList">
										<xsl:for-each select="sml:position">
											<xsl:for-each select="swe:Position">
												<br/>
												<span>
													<b>Position: </b>(
												</span>
												<span>
													<xsl:value-of select="substring-after( @referenceFrame, &quot;urn:ogc:crs:&quot;)"/>
												</span>
												<span>
													<xsl:text> )</xsl:text>
												</span>
												<xsl:for-each select="swe:location">
													<xsl:for-each select="swe:Vector">
														<xsl:for-each select="swe:coordinate">
															<br/>
															<xsl:for-each select="@name">
																<span>
																	<xsl:value-of select="string(.)"/>
																</span>
																<span>
																	<xsl:text>: </xsl:text>
																</span>
															</xsl:for-each>
															<xsl:for-each select="swe:Quantity">
																<xsl:for-each select="swe:value">
																	<xsl:apply-templates/>
																</xsl:for-each>
																<xsl:for-each select="swe:uom">
																	<xsl:for-each select="@code">
																		<span>
																			<xsl:text>&#160;</xsl:text>
																		</span>
																		<span>
																			<xsl:value-of select="string(.)"/>
																		</span>
																	</xsl:for-each>
																</xsl:for-each>
															</xsl:for-each>
														</xsl:for-each>
													</xsl:for-each>
												</xsl:for-each>
												<br/>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:for-each>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:for-each>
					<br/>
				</xsl:for-each>
				<br/>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
