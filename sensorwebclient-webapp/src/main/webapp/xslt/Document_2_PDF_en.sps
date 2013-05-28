<?xml version="1.0" encoding="UTF-8"?>
<structure version="6" xsltversion="1" cssmode="strict" relativeto="*SPS" encodinghtml="UTF-8" encodingrtf="ISO-8859-1" encodingpdf="UTF-8">
	<parameters/>
	<schemasources>
		<namespaces>
			<nspair prefix="n52" uri="http://www.n52.org/oxf"/>
		</namespaces>
		<schemasources>
			<xsdschemasource name="$XML" main="1" schemafile="DocumentStructure.xsd" workingxmlfile="Document_example.xml">
				<xmltablesupport/>
				<textstateicons/>
			</xsdschemasource>
		</schemasources>
	</schemasources>
	<modules/>
	<flags>
		<scripts/>
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
							<template match="n52:DocumentStructure" matchtype="schemagraphitem">
								<editorproperties elementstodisplay="1" elementstofetch="all"/>
								<children>
									<newline/>
									<paragraph paragraphtag="h1">
										<children>
											<text fixtext="Diagram">
												<styles font-weight="bold"/>
											</text>
										</children>
									</paragraph>
									<image>
										<target>
											<xpath value="n52:DiagramURL"/>
										</target>
										<imagesource>
											<xpath value="n52:DiagramURL"/>
										</imagesource>
									</image>
									<newline/>
									<newline/>
									<paragraph paragraphtag="h1">
										<children>
											<text fixtext="Legend">
												<styles font-weight="bold"/>
											</text>
										</children>
									</paragraph>
									<image>
										<target>
											<xpath value="n52:LegendURL"/>
										</target>
										<imagesource>
											<xpath value="n52:LegendURL"/>
										</imagesource>
									</image>
									<newline/>
									<newline/>
									<template match="n52:TimeSeries" matchtype="schemagraphitem">
										<editorproperties elementstodisplay="1" elementstofetch="all"/>
										<children>
											<newline/>
											<paragraph paragraphtag="h1">
												<children>
													<text fixtext="Timeseries">
														<styles font-weight="bold"/>
													</text>
												</children>
											</paragraph>
											<table>
												<properties border="1" width="100%"/>
												<children>
													<tablebody>
														<children>
															<tablerow>
																<children>
																	<tablecell>
																		<properties width="344"/>
																		<children>
																			<text fixtext="Sensor Location"/>
																		</children>
																	</tablecell>
																	<tablecell>
																		<properties width="287"/>
																		<children>
																			<template match="@featureOfInterestID" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<content>
																						<format datatype="string"/>
																					</content>
																				</children>
																			</template>
																		</children>
																	</tablecell>
																</children>
															</tablerow>
															<tablerow>
																<children>
																	<tablecell>
																		<properties width="344"/>
																		<children>
																			<text fixtext="Sensor Phenomenon"/>
																		</children>
																	</tablecell>
																	<tablecell>
																		<properties width="287"/>
																		<children>
																			<template match="@phenomenID" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<content>
																						<format datatype="string"/>
																					</content>
																				</children>
																			</template>
																		</children>
																	</tablecell>
																</children>
															</tablerow>
															<tablerow>
																<children>
																	<tablecell>
																		<properties width="344"/>
																		<children>
																			<text fixtext="Sensor Type"/>
																		</children>
																	</tablecell>
																	<tablecell>
																		<properties width="287"/>
																		<children>
																			<template match="@procedureID" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="1" elementstofetch="all"/>
																				<children>
																					<content>
																						<format datatype="string"/>
																					</content>
																				</children>
																			</template>
																		</children>
																	</tablecell>
																</children>
															</tablerow>
														</children>
													</tablebody>
												</children>
											</table>
											<newline/>
											<text fixtext="Metadata of Timeseries:">
												<styles font-weight="bold"/>
											</text>
											<newline/>
											<table>
												<properties border="1"/>
												<children>
													<tableheader>
														<children>
															<tablerow>
																<children>
																	<tablecell headercell="1">
																		<children>
																			<text fixtext="Property"/>
																		</children>
																	</tablecell>
																	<tablecell headercell="1">
																		<children>
																			<text fixtext="Value"/>
																		</children>
																	</tablecell>
																</children>
															</tablerow>
														</children>
													</tableheader>
													<tablebody>
														<children>
															<template match="n52:Metadata" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="5"/>
																<children>
																	<template match="n52:genericMetadataPair" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="5"/>
																		<children>
																			<tablerow>
																				<children>
																					<tablecell>
																						<children>
																							<template match="@name" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="5"/>
																								<children>
																									<content>
																										<format datatype="string"/>
																									</content>
																								</children>
																							</template>
																						</children>
																					</tablecell>
																					<tablecell>
																						<children>
																							<template match="@value" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="5"/>
																								<children>
																									<content>
																										<format datatype="string"/>
																									</content>
																								</children>
																							</template>
																						</children>
																					</tablecell>
																				</children>
																			</tablerow>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</tablebody>
												</children>
											</table>
											<newline/>
											<text fixtext="Sensor Data:">
												<styles font-weight="bold"/>
											</text>
											<newline/>
											<table>
												<properties border="1"/>
												<children>
													<tableheader>
														<children>
															<tablerow>
																<children>
																	<tablecell headercell="1">
																		<properties width="200"/>
																		<children>
																			<template match="n52:Table" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="5"/>
																				<children>
																					<template match="n52:leftColHeader" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="5"/>
																						<children>
																							<content>
																								<format datatype="string"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</tablecell>
																	<tablecell headercell="1">
																		<children>
																			<template match="n52:Table" matchtype="schemagraphitem">
																				<editorproperties elementstodisplay="5"/>
																				<children>
																					<template match="n52:rightColHeader" matchtype="schemagraphitem">
																						<editorproperties elementstodisplay="5"/>
																						<children>
																							<content>
																								<format datatype="string"/>
																							</content>
																						</children>
																					</template>
																				</children>
																			</template>
																		</children>
																	</tablecell>
																</children>
															</tablerow>
														</children>
													</tableheader>
													<tablebody>
														<children>
															<template match="n52:Table" matchtype="schemagraphitem">
																<editorproperties elementstodisplay="1" elementstofetch="all"/>
																<children>
																	<template match="n52:entry" matchtype="schemagraphitem">
																		<editorproperties elementstodisplay="1" elementstofetch="all"/>
																		<children>
																			<tablerow>
																				<children>
																					<tablecell>
																						<properties width="200"/>
																						<children>
																							<template match="@time" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<content>
																										<format datatype="string"/>
																									</content>
																								</children>
																							</template>
																						</children>
																					</tablecell>
																					<tablecell>
																						<children>
																							<template match="@value" matchtype="schemagraphitem">
																								<editorproperties elementstodisplay="1" elementstofetch="all"/>
																								<children>
																									<content>
																										<format datatype="string"/>
																									</content>
																								</children>
																							</template>
																						</children>
																					</tablecell>
																				</children>
																			</tablerow>
																		</children>
																	</template>
																</children>
															</template>
														</children>
													</tablebody>
												</children>
											</table>
											<newline/>
										</children>
									</template>
									<newline/>
								</children>
							</template>
						</children>
					</template>
				</children>
			</globaltemplate>
		</children>
	</mainparts>
	<globalparts/>
	<pagelayout/>
	<designfragments/>
</structure>
