<?xml version="1.0" encoding="UTF-8"?>
<structure version="6" xsltversion="1" cssmode="strict" relativeto="*SPS" encodinghtml="UTF-8" encodingrtf="ISO-8859-1" encodingpdf="UTF-8">
	<parameters/>
	<schemasources>
		<namespaces>
			<nspair prefix="gml" uri="http://www.opengis.net/gml"/>
			<nspair prefix="ism" uri="urn:us:gov:ic:ism:v2"/>
			<nspair prefix="sch" uri="http://www.ascc.net/xml/schematron"/>
			<nspair prefix="smil20" uri="http://www.w3.org/2001/SMIL20/"/>
			<nspair prefix="smil20lang" uri="http://www.w3.org/2001/SMIL20/Language"/>
			<nspair prefix="sml" uri="http://www.opengis.net/sensorML/1.0"/>
			<nspair prefix="swe" uri="http://www.opengis.net/swe/1.0"/>
			<nspair prefix="xlink" uri="http://www.w3.org/1999/xlink"/>
		</namespaces>
		<schemasources>
			<xsdschemasource name="$XML" main="1" schemafile="E:\temp\OGC_SVN\swe\trunk\sensorML\1.0.0\base\sensorML.xsd" workingxmlfile="SensorML_example.xml">
				<xmltablesupport/>
				<textstateicons/>
			</xsdschemasource>
		</schemasources>
	</schemasources>
	<modules/>
	<flags>
		<scripts>
			<script language="javascript" isactive="1"/>
		</scripts>
		<globalparts/>
		<designfragments/>
		<pagelayouts/>
	</flags>
	<scripts>
		<script language="javascript"/>
	</scripts>
	<globalstyles/>
	<mainparts>
		<children>
			<globaltemplate match="/" matchtype="named" parttype="main">
				<children>
					<template match="$XML" matchtype="schemasource">
						<editorproperties elementstodisplay="1" elementstofetch="all"/>
						<children>
							<template match="sml:SensorML" matchtype="schemagraphitem">
								<editorproperties elementstodisplay="1" elementstofetch="all"/>
								<children>
									<template match="sml:member" matchtype="schemagraphitem">
										<editorproperties elementstodisplay="1" elementstofetch="all"/>
										<children>
											<template match="sml:System" matchtype="schemagraphitem">
												<editorproperties elementstodisplay="1" elementstofetch="all"/>
												<children>
													<newline/>
													<template match="sml:identification" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="sml:IdentifierList" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="sml:identifier" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<condition>
																				<children>
																					<conditionbranch xpath="@name  = &quot;stationName&quot;">
																						<children>
																							<text fixtext="Station: "/>
																							<template match="sml:Term" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<template match="sml:value" matchtype="schemagraphitem">
																										<editorproperties elementstodisplay="1" elementstofetch="all"/>
																										<children>
																											<content>
																												<format datatype="token"/>
																											</content>
																										</children>
																									</template>
																								</children>
																							</template>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																			<newline/>
																			<condition>
																				<children>
																					<conditionbranch xpath="@name  = &quot;stationID&quot;">
																						<children>
																							<text fixtext="ID: "/>
																							<template match="sml:Term" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<template match="sml:value" matchtype="schemagraphitem">
																										<editorproperties elementstodisplay="1" elementstofetch="all"/>
																										<children>
																											<content>
																												<format datatype="token"/>
																											</content>
																										</children>
																									</template>
																								</children>
																							</template>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</template>
													<newline/>
													<template match="sml:classification" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="sml:ClassifierList" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="sml:classifier" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<condition>
																				<children>
																					<conditionbranch xpath="@name = &quot;sensorType&quot;">
																						<children>
																							<text fixtext="Sensor: "/>
																							<template match="sml:Term" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<template match="sml:value" matchtype="schemagraphitem">
																										<editorproperties elementstodisplay="1" elementstofetch="all"/>
																										<children>
																											<content>
																												<format datatype="token"/>
																											</content>
																										</children>
																									</template>
																								</children>
																							</template>
																						</children>
																					</conditionbranch>
																				</children>
																			</condition>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</template>
													<newline/>
													<newline/>
													<template match="sml:characteristics" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="swe:SimpleDataRecord" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="swe:field" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<newline/>
																			<template match="swe:Text" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<template match="gml:description" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<content/>
																							<text fixtext=": "/>
																						</children>
																					</template>
																					<template match="swe:value" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<content>
																								<format datatype="string"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</template>
													<template match="sml:capabilities" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="swe:SimpleDataRecord" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="swe:field" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<newline/>
																			<template match="swe:Text" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<template match="gml:description" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<content/>
																							<text fixtext=": "/>
																						</children>
																					</template>
																					<template match="swe:value" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<content>
																								<format datatype="string"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</template>
													<newline/>
													<newline/>
													<template match="sml:documentation" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="sml:Document" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="gml:description" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<content/>
																			<text fixtext=": "/>
																		</children>
																	</template>
																	<link>
																		<children>
																			<template match="sml:onlineResource" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<template match="@xlink:href" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<content>
																								<format datatype="anyURI"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																		<action>
																			<none/>
																		</action>
																		<bookmark/>
																		<hyperlink>
																			<xpath value="@xlink:href"/>
																		</hyperlink>
																	</link>
																</children>
															</template>
														</children>
													</template>
													<newline/>
													<newline/>
													<template match="sml:positions" matchtype="schemagraphitem">
														<editorproperties elementstodisplay="1" elementstofetch="all"/>
														<children>
															<template match="sml:PositionList" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="sml:position" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<template match="swe:Position" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<newline/>
																					<text fixtext="Position: ("/>
																					<autocalc xpath="substring-after( @referenceFrame, &quot;urn:ogc:crs:&quot;)"/>
																					<text fixtext=")"/>
																					<newline/>
																					<template match="swe:location" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="1" elementstofetch="all"/>
																						<children>
																							<template match="swe:Vector" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<template match="swe:coordinate" matchtype="schemagraphitem">
																										<editorproperties elementstodisplay="1" elementstofetch="all"/>
																										<children>
																											<newline/>
																											<template match="@name" matchtype="schemagraphitem">
																												<editorproperties elementstodisplay="1" elementstofetch="all"/>
																												<children>
																													<content>
																														<format datatype="token"/>
																													</content>
																													<text fixtext=": "/>
																												</children>
																											</template>
																											<template match="swe:Quantity" matchtype="schemagraphitem">
																												<editorproperties elementstodisplay="1" elementstofetch="all"/>
																												<children>
																													<template match="swe:value" matchtype="schemagraphitem">
																														<editorproperties elementstodisplay="1" elementstofetch="all"/>
																														<children>
																															<content>
																																<format datatype="double"/>
																															</content>
																														</children>
																													</template>
																													<template match="swe:uom" matchtype="schemagraphitem">
																														<editorproperties elementstodisplay="1" elementstofetch="all"/>
																														<children>
																															<template match="@code" matchtype="schemagraphitem">
																																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																																<children>
																																	<text fixtext=" "/>
																																	<content>
																																		<format datatype="string"/>
																																	</content>
																																</children>
																															</template>
																														</children>
																													</template>
																												</children>
																											</template>
																										</children>
																									</template>
																								</children>
																							</template>
																						</children>
																					</template>
																					<newline/>
																				</children>
																			</template>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</template>
													<newline/>
													<newline/>
												</children>
											</template>
										</children>
									</template>
								</children>
							</template>
							<newline/>
							<newline/>
						</children>
					</template>
					<newline/>
				</children>
			</globaltemplate>
		</children>
	</mainparts>
	<globalparts/>
	<pagelayout/>
	<designfragments/>
</structure>
