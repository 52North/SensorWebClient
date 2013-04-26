<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:gml="http://www.opengis.net/gml" xmlns:ism="urn:us:gov:ic:ism:v2"
	xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:smil20="http://www.w3.org/2001/SMIL20/"
	xmlns:smil20lang="http://www.w3.org/2001/SMIL20/Language" xmlns:sml="http://www.opengis.net/sensorML/1.0"
	xmlns:swe="http://www.opengis.net/swe/1.0" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:altova="http://www.altova.com">
	<xsl:variable name="XML" select="/" />
	<xsl:output method="html" encoding="utf-8" indent="yes" />
	
	<xsl:param name="SV_OutputFormat" select="'HTML'" />
	<xsl:template match="/">
	   <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<title />
				<link rel="stylesheet" href="../css/layout.min.min.css" />
			</head>
			<body bgcolor="#CCFFFF" style="font-family: sans-serif;">
				<table border="1" width="100%">
					<xsl:for-each select="$XML">
						<xsl:for-each select="sml:SensorML">
							<xsl:variable name="sml_ident" select="sml:identification" />
							<xsl:choose>
								<xsl:when test="$sml_ident">
									<xsl:for-each select="sml:identification">
										<xsl:for-each select="sml:IdentifierList">
											<xsl:for-each select="sml:identifier">
												<xsl:if test="@name  = &quot;shortName&quot;">
													<tr class="sensorInfo">
														<span>
															<td>
																<b>Station: </b>
															</td>
														</span>
														<td>
															<xsl:for-each select="sml:Term">
																<xsl:for-each select="sml:value">
																	<xsl:apply-templates />
																</xsl:for-each>
															</xsl:for-each>
														</td>
													</tr>
												</xsl:if>
												<xsl:if test="@name  = &quot;uniqueID&quot;">
													<tr class="sensorInfo">
														<span>
															<td>
																<b>ID: </b>
															</td>
														</span>
														<td>
															<xsl:for-each select="sml:Term">
																<xsl:for-each select="sml:value">
																	<xsl:apply-templates />
																</xsl:for-each>
															</xsl:for-each>
														</td>
													</tr>
												</xsl:if>
												<xsl:if test="@name  = &quot;URN&quot;">
													<tr class="sensorInfo">
														<span>
															<td>
																<b>ID: </b>
															</td>
														</span>
														<td>
															<xsl:for-each select="sml:Term">
																<xsl:for-each select="sml:value">
																	<xsl:apply-templates />
																</xsl:for-each>
															</xsl:for-each>
														</td>
													</tr>
												</xsl:if>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
									<xsl:for-each select="sml:classification">
										<xsl:for-each select="sml:ClassifierList">
											<xsl:for-each select="sml:classifier">
												<xsl:if test="@name = &quot;sensorType&quot;">
													<tr class="sensorInfo">
														<span>
															<td>
																<b>Sensor: </b>
															</td>
														</span>
														<td>
															<xsl:for-each select="sml:Term">
																<xsl:for-each select="sml:value">
																	<xsl:apply-templates />
																</xsl:for-each>
															</xsl:for-each>
														</td>
													</tr>
												</xsl:if>
												<xsl:if test="@name = &quot;phenomenon&quot;">
													<tr class="sensorInfo">
														<span>
															<td>
																<b>Parameter: </b>
															</td>
														</span>
														<td>
															<xsl:for-each select="sml:Term">
																<xsl:for-each select="sml:value">
																	<xsl:apply-templates />
																</xsl:for-each>
															</xsl:for-each>
														</td>
													</tr>
												</xsl:if>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
									<xsl:for-each select="sml:characteristics">
										<xsl:for-each select="swe:SimpleDataRecord">
											<xsl:for-each select="swe:field">
												<tr class="sensorInfo">
													<td>
														<b>
															<xsl:value-of select="@name" />
															:
														</b>
													</td>
													<td>
														<xsl:for-each select="swe:Text">
															<xsl:for-each select="gml:description">
																<b>
																	<xsl:apply-templates />
																	:
																</b>
															</xsl:for-each>
															<xsl:for-each select="swe:value">
																<xsl:apply-templates />
															</xsl:for-each>
														</xsl:for-each>
													</td>
												</tr>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
									<xsl:for-each select="sml:capabilities">
										<xsl:for-each select="swe:SimpleDataRecord">
											<xsl:for-each select="swe:field">
												<tr class="sensorInfo">
													<td>
														<b>
															<xsl:value-of select="@name" />
															:
														</b>
													</td>
													<td>
														<xsl:for-each select="swe:Text">
															<xsl:for-each select="gml:description">
																<b>
																	<xsl:apply-templates />
																	:
																</b>
															</xsl:for-each>
															<xsl:for-each select="swe:value">
																<xsl:apply-templates />
															</xsl:for-each>
														</xsl:for-each>
													</td>
												</tr>
											</xsl:for-each>
										</xsl:for-each>
									</xsl:for-each>
									<!-- <xsl:for-each select="sml:positions"> <xsl:for-each select="sml:PositionList"> 
										<xsl:for-each select="sml:position"> <xsl:for-each select="swe:Position"> 
										<tr class="sensorInfo"> <td> <span> <b>Position: </b> </span> </td> <td> 
										<span>( <xsl:value-of select="substring-after( @referenceFrame, &quot;urn:ogc:crs:&quot;)"/> 
										</span> <span> <xsl:text> )</xsl:text> </span> <xsl:for-each select="swe:location"> 
										<xsl:for-each select="swe:Vector"> <xsl:for-each select="swe:coordinate"> 
										<xsl:for-each select="@name"> <span> <xsl:value-of select="string(.)"/> </span> 
										<span> <xsl:text>: </xsl:text> </span> </xsl:for-each> <xsl:for-each select="swe:Quantity"> 
										<xsl:for-each select="swe:value"> <xsl:apply-templates/> <span> <xsl:text>&#160;</xsl:text> 
										</span> </xsl:for-each> <xsl:for-each select="swe:uom"> <xsl:for-each select="@code"> 
										<span> <xsl:text>&#160;</xsl:text> </span> <span> <xsl:value-of select="string(.)"/> 
										</span> </xsl:for-each> <span> <xsl:text>&#160;</xsl:text> </span> </xsl:for-each> 
										</xsl:for-each> </xsl:for-each> </xsl:for-each> </xsl:for-each> </td> </tr> 
										</xsl:for-each> </xsl:for-each> </xsl:for-each> </xsl:for-each> -->
									<xsl:for-each select="sml:member">
										<xsl:for-each select="sml:System">
											<!-- <xsl:for-each select="sml:position"> <xsl:for-each select="swe:Position"> 
												<tr class="sensorInfo"> <td> <span> <b>Position: </b> </span> </td> <td> 
												<span>(<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:crs:&quot;)"/> 
												<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:def:crs:&quot;)"/> 
												</span> <span> <xsl:text>) </xsl:text> </span> <xsl:for-each select="swe:location"> 
												<xsl:for-each select="swe:Vector"> <xsl:for-each select="swe:coordinate"> 
												<xsl:for-each select="@name"> <span> <xsl:value-of select="string(.)"/> </span> 
												<span> <xsl:text>: </xsl:text> </span> </xsl:for-each> <xsl:for-each select="swe:Quantity"> 
												<xsl:for-each select="swe:value"> <xsl:apply-templates/> <span> <xsl:text>&#160;</xsl:text> 
												</span> </xsl:for-each> <xsl:for-each select="swe:uom"> <xsl:for-each select="@code"> 
												<span> <xsl:value-of select="string(.)"/> </span> </xsl:for-each> <span> 
												<xsl:text>&#160;</xsl:text> </span> </xsl:for-each> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> </td> </tr> </xsl:for-each> </xsl:for-each> -->
											<!-- <xsl:for-each select="sml:positions"> <xsl:for-each select="sml:PositionList"> 
												<xsl:for-each select="sml:position"> <xsl:for-each select="swe:Position"> 
												<tr class="sensorInfo"> <td> <span> <b>Position: </b> </span> </td> <td> 
												<span>(<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:crs:&quot;)"/> 
												<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:def:crs:&quot;)"/> 
												</span> <span> <xsl:text>) </xsl:text> </span> <xsl:for-each select="swe:location"> 
												<xsl:for-each select="swe:Vector"> <xsl:for-each select="swe:coordinate"> 
												<xsl:for-each select="@name"> <span> <xsl:value-of select="string(.)"/> </span> 
												<span> <xsl:text>: </xsl:text> </span> </xsl:for-each> <xsl:for-each select="swe:Quantity"> 
												<xsl:for-each select="swe:value"> <xsl:apply-templates/> <span> <xsl:text>&#160;</xsl:text> 
												</span> </xsl:for-each> <xsl:for-each select="swe:uom"> <xsl:for-each select="@code"> 
												<span> <xsl:value-of select="string(.)"/> </span> </xsl:for-each> <span> 
												<xsl:text>&#160;</xsl:text> </span> </xsl:for-each> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> </td> </tr> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> -->
										</xsl:for-each>
									</xsl:for-each>
								</xsl:when>
								<xsl:otherwise>
									<xsl:for-each select="sml:member">
										<xsl:for-each select="sml:System">
											<xsl:for-each select="sml:identification">
												<xsl:for-each select="sml:IdentifierList">
													<xsl:for-each select="sml:identifier">
														<xsl:if test="@name  = &quot;shortName&quot;">
															<tr class="sensorInfo">
																<span>
																	<td>
																		<b>Station: </b>
																	</td>
																</span>
																<td>
																	<xsl:for-each select="sml:Term">
																		<xsl:for-each select="sml:value">
																			<xsl:apply-templates />
																		</xsl:for-each>
																	</xsl:for-each>
																</td>
															</tr>
														</xsl:if>
														<xsl:if test="@name  = &quot;uniqueID&quot;">
															<tr class="sensorInfo">
																<span>
																	<td>
																		<b>ID: </b>
																	</td>
																</span>
																<td>
																	<xsl:for-each select="sml:Term">
																		<xsl:for-each select="sml:value">
																			<xsl:apply-templates />
																		</xsl:for-each>
																	</xsl:for-each>
																</td>
															</tr>
														</xsl:if>
														<xsl:if test="@name  = &quot;URN&quot;">
															<tr class="sensorInfo">
																<span>
																	<td>
																		<b>ID: </b>
																	</td>
																</span>
																<td>
																	<xsl:for-each select="sml:Term">
																		<xsl:for-each select="sml:value">
																			<xsl:apply-templates />
																		</xsl:for-each>
																	</xsl:for-each>
																</td>
															</tr>
														</xsl:if>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:for-each>
											<xsl:for-each select="sml:classification">
												<xsl:for-each select="sml:ClassifierList">
													<xsl:for-each select="sml:classifier">
														<xsl:if test="@name = &quot;sensorType&quot;">
															<tr class="sensorInfo">
																<span>
																	<td>
																		<b>Sensor: </b>
																	</td>
																</span>
																<td>
																	<xsl:for-each select="sml:Term">
																		<xsl:for-each select="sml:value">
																			<xsl:apply-templates />
																		</xsl:for-each>
																	</xsl:for-each>
																</td>
															</tr>
														</xsl:if>
														<xsl:if test="@name = &quot;phenomenon&quot;">
															<tr class="sensorInfo">
																<span>
																	<td>
																		<b>Parameter: </b>
																	</td>
																</span>
																<td>
																	<xsl:for-each select="sml:Term">
																		<xsl:for-each select="sml:value">
																			<xsl:apply-templates />
																		</xsl:for-each>
																	</xsl:for-each>
																</td>
															</tr>
														</xsl:if>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:for-each>
											<xsl:for-each select="sml:characteristics">
												<xsl:for-each select="swe:SimpleDataRecord">
													<xsl:for-each select="swe:field">
														<tr class="sensorInfo">
															<td>
																<b>
																	<xsl:value-of select="@name" />
																	:
																</b>
															</td>
															<td>
																<xsl:for-each select="swe:Text">
																	<xsl:for-each select="gml:description">
																		<b>
																			<xsl:apply-templates />
																			:
																		</b>
																	</xsl:for-each>
																	<xsl:for-each select="swe:value">
																		<xsl:apply-templates />
																	</xsl:for-each>
																</xsl:for-each>
															</td>
														</tr>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:for-each>
											<xsl:for-each select="sml:capabilities">
												<xsl:for-each select="swe:SimpleDataRecord">
													<xsl:for-each select="swe:field">
														<tr class="sensorInfo">
															<td>
																<b>
																	<xsl:value-of select="@name" />
																	:
																</b>
															</td>
															<td>
																<xsl:for-each select="swe:Text">
																	<xsl:for-each select="gml:description">
																		<b>
																			<xsl:apply-templates />
																			:
																		</b>
																	</xsl:for-each>
																	<xsl:for-each select="swe:value">
																		<xsl:apply-templates />
																	</xsl:for-each>
																</xsl:for-each>
															</td>
														</tr>
													</xsl:for-each>
												</xsl:for-each>
											</xsl:for-each>
											<!-- <xsl:for-each select="sml:position"> <xsl:for-each select="swe:Position"> 
												<tr class="sensorInfo"> <td> <span> <b>Position: </b> </span> </td> <td> 
												<span>(<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:crs:&quot;)"/> 
												<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:def:crs:&quot;)"/> 
												</span> <span> <xsl:text>) </xsl:text> </span> <xsl:for-each select="swe:location"> 
												<xsl:for-each select="swe:Vector"> <xsl:for-each select="swe:coordinate"> 
												<xsl:for-each select="@name"> <span> <xsl:value-of select="string(.)"/> </span> 
												<span> <xsl:text>: </xsl:text> </span> </xsl:for-each> <xsl:for-each select="swe:Quantity"> 
												<xsl:for-each select="swe:value"> <xsl:apply-templates/> <span> <xsl:text>&#160;</xsl:text> 
												</span> </xsl:for-each> <xsl:for-each select="swe:uom"> <xsl:for-each select="@code"> 
												<span> <xsl:value-of select="string(.)"/> </span> </xsl:for-each> <span> 
												<xsl:text>&#160;</xsl:text> </span> </xsl:for-each> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> </td> </tr> </xsl:for-each> </xsl:for-each> -->
											<!-- <xsl:for-each select="sml:positions"> <xsl:for-each select="sml:PositionList"> 
												<xsl:for-each select="sml:position"> <xsl:for-each select="swe:Position"> 
												<tr class="sensorInfo"> <td> <span> <b>Position: </b> </span> </td> <td> 
												<span>(<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:crs:&quot;)"/> 
												<xsl:value-of select="substring-after(@referenceFrame, &quot;urn:ogc:def:crs:&quot;)"/> 
												</span> <span> <xsl:text>) </xsl:text> </span> <xsl:for-each select="swe:location"> 
												<xsl:for-each select="swe:Vector"> <xsl:for-each select="swe:coordinate"> 
												<xsl:for-each select="@name"> <span> <xsl:value-of select="string(.)"/> </span> 
												<span> <xsl:text>: </xsl:text> </span> </xsl:for-each> <xsl:for-each select="swe:Quantity"> 
												<xsl:for-each select="swe:value"> <xsl:apply-templates/> <span> <xsl:text>&#160;</xsl:text> 
												</span> </xsl:for-each> <xsl:for-each select="swe:uom"> <xsl:for-each select="@code"> 
												<span> <xsl:value-of select="string(.)"/> </span> </xsl:for-each> <span> 
												<xsl:text>&#160;</xsl:text> </span> </xsl:for-each> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> </td> </tr> </xsl:for-each> </xsl:for-each> 
												</xsl:for-each> </xsl:for-each> -->
										</xsl:for-each>
									</xsl:for-each>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
