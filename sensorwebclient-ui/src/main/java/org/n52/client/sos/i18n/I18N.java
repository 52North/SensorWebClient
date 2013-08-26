/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
	
	String toZIP();

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
    
    String diagramAxisLabelTime();
	
}