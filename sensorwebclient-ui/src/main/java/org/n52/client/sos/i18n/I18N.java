/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.client.sos.i18n;

import com.google.gwt.i18n.client.Constants;

public interface I18N extends Constants {

    String changeColor();

    String infos();

    String delete();

    String mail();

    String diagram();

    String table();

    String picker();

    String from();

    String to();

    String day();

    String week();

    String month();

    String year();
    
    String time();

    String back();

    String forth();

    String zoomIn();

    String zoomOut();

    String exportCSV();

    String exportXLS();

    String exportPDF();

    String refresh();
    
    String refreshExtended();

    String pickStation();

    String sosLabel();

    String offeringLabel();

    String foiLabel();

    String procedureLabel();

    String phenomenonLabel();
    
    String sensorLabel();

    String confirm();

    String cancel();

    String noData();

    String errorRequest();

    String errorCrossDates();

    String errorBeginDateBigger();

    String date();

    String errorResponse();

    String usLang();

    String deLang();

    String help();
    
    String admin();

    String map();

    String mapInfo();

    String errorInvalidDates();

    String detail();

    String logger();

    String errorMinimalInterval();

    String errorUrlParsing();

    String intro();

    String link();

    String changeColorExtended();

    String infoExtended();

    String deleteExtended();

    String mailExtended();

    String exportCSVExtended();

    String exportXLSExtended();

    String mapInfoExtended();

    String backExtended();

    String forthExtended();

    String linkExtended();

    String exportPDFExtended();

    String zoomOutExtended();

    String zoomInExtended();

    String usLangExtended();

    String deLangExtended();

    String pickerExtended();

    String loggerExtended();

    String helpExtended();

    String exportZipPDF();

    String exportZipPDFExtended();

    String exportZipCSVExtended();

    String exportZipCSV();

    String exportZipXLSExtended();

    String exportZipXLS();

    String addSOS();

    String sosURL();

    String changeStyle();

    String changeStyleExtended();

    String lineStyle();

    String dottedStyle();

    String areaStyle();

    String dashedStyle();

    String Opacity();

    String zeroScale();
    
    String scale();

    String color();

    String linestyle();

    String undo();
    
    String showGrid();
    
    String hideGrid();

    String undoMessage();

    String jumpToday();

    String bboxZoom();

    String bboxZoomExt();

    String diagMove();

    String diagMoveExt();

    String overviewInterval();

    String in();

    String intervalTypes();

    String firstValue();

    String lastValue();

    String errorChooseBiggerDate();

    String errorChooseSmallerDate();

    String errorOverviewInterval();

    String exportPDFallInOne();

    String exportPDFallInOneExtended();
    
    String resetScale();
    
    String autoScaleButtonExtra();

    String autoScaleButton();

    String up();

    String down();

    String upExt();

    String downExt();

    String undoExt();

    String jumpTodayExt();

    String autoScale();

    String getFeaturesForArea();

    String getFeaturesForAreaExt();

    String addSOSExt();

    String addNewTimeseries();

    String addNewTimeseriesExt();

    String addNewTimeseriesAndCloseExt();

    String addNewTimeseriesAndClose();

    String setAndRefresh();

    String setAndRefreshExt();

    String requestsLeft();

    String Impressum();

	String permalink();

    String jumpToFirst();

    String jumpToFirstExtended();

    String errorSOS();

    String jumpToLast();
    
    String jumpToLastExtended();

    String levelLine();

    String sumLine();

    String seriesType();

    String ttips();

    String ttipsExtended();

    String ddEmptyValue();

    String jumpTo();

    String jumpToTypes();

    String lineDotsStyle();

    String linkMessage();

    String SOSLoading();

    String invalidSOS();
    
    String sosError();

    String OK();

    String expertsMenu();

    String expertsMenuButton();

    String timeSeriesNotExists();

    String noDataAvailable();
    
    String locale();

    String imprintPath();
    
    String helpPath();

    String ddNoValues();
    
    String maxZoomInTime();

    String errorPhenomenonProperties();

    String today();

	String todayTooltip();
	
	String editProfile();
	
	String defaultJumpToType();
	
	String pdf();
	
	String csv();
	
	String floatingDiagramWindowTitle();

	String expandDiagramInteraction();

	String expandDiagramInteractionTooltip();

	String expandSelectionStationPicker();

	String expandSelectionStationTickerTooltip();

	String chooseDataSource();

	String showSettings();

	String hideSettings();

	String export();

	String toCSV();

	String toPDF();

	String lineWidth();

	String addToBookmarks();

	String deleteTimeSeriesActiv();

	String loggerWindowTitle();
	
	String dataprovider();
	
	String sesCommunicatorButton();
	
	String sesCommunicatorButtonExtend();

	String loggedInAs();

	String logout();

    String logoutSuccessful();
	
}