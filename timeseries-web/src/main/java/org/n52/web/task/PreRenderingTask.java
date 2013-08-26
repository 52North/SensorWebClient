package org.n52.web.task;

import static org.n52.io.img.RenderingContext.createContextForSingleTimeseries;
import static org.n52.io.v1.data.UndesignedParameterSet.createForSingleTimeseries;
import static org.n52.web.v1.ctrl.Stopwatch.startStopwatch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.io.IOFactory;
import org.n52.io.IOHandler;
import org.n52.io.TimeseriesIOException;
import org.n52.io.img.RenderingContext;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.ctrl.Stopwatch;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletConfigAware;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PreRenderingTask extends TimerTask implements ServletConfigAware {
	
	private String realPath;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PreRenderingTask.class);
	
	private String outputPath;
	
	// image dimensions with default values
	private int width = 800;
	private int height = 500;
	
	// default language
	private String language = "en";
	
	private boolean showGrid = true;
	
	private TimeseriesMetadataService timeseriesMetadataService;
	
	private TimeseriesDataService timeseriesDataService;
	
	@Override
	public void run() {
		LOGGER.info("Start prerendering task");
		createOutputDirectory();
		InputStream preRenderingConfigurations = getClass().getResourceAsStream("/preRenderingImages.json");
		InputStream phenomenonToStyleMapping = getClass().getResourceAsStream("/phenomenonToStyle.json");
		try {
			ObjectMapper om = new ObjectMapper();
			PreRenderingConfiguration[] configurations = om.readValue(preRenderingConfigurations, PreRenderingConfiguration[].class);
			Map<String, StyleProperties> phenStyleMap = om.readValue(phenomenonToStyleMapping, new TypeReference<Map<String, StyleProperties>>() {}); 
			for (PreRenderingConfiguration config : configurations) {
				String timeseriesId = config.getTimeseriesId();
				for (String interval : config.getInterval()) {
					
					// create timespan
			        String timespan = null;
			        DateTime now = new DateTime();
			        if (interval.equals("lastDay")) {
			        	timespan = new Interval(now.minusDays(1), now).toString();
			        } else if (interval.equals("lastWeek")) {
			        	timespan = new Interval(now.minusWeeks(1), now).toString();
			        } else if (interval.equals("lastMonth")) {
						timespan = new Interval(now.minusMonths(1), now).toString();
					} else {
						throw new ResourceNotFoundException("Unknown interval definition '" + interval + "' for timeseriesId " + timeseriesId);
					}
			        
			        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
			        String phenomenon = metadata.getParameters().getPhenomenon().getLabel();
			        StyleProperties style;
			        if (phenStyleMap.containsKey(phenomenon)) {
			        	style = phenStyleMap.get(phenomenon);
			        } else {
			        	style = config.getStyle();
			        }
			        if (style == null) {
						LOGGER.error("No style propertie defined for timeseries " + timeseriesId + " with phenomenon " + phenomenon); 
					}
			        
			        RenderingContext context = createContextForSingleTimeseries(metadata, style, timespan);
			        context.setDimensions(width, height);
			        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, timespan);
			        IOHandler renderer = IOFactory.create()
			                .inLanguage(language)
			                .showGrid(showGrid)
			                .createIOHandler(context);
			        try {
			        	File file = new File(createFileName(timeseriesId, interval));
			        	file.createNewFile();
						FileOutputStream fos = new FileOutputStream(file);
						renderer.generateOutput(getTimeseriesData(parameters));
						renderer.encodeAndWriteTo(fos);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						LOGGER.error("Error while creating image file", e);
					} catch (TimeseriesIOException e) {
						LOGGER.error("Image creation occures error", e);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error while reading prerendering configuration file", e);
		}
	}
	
	private void createOutputDirectory() {
		File dir = new File(realPath + File.separator + this.outputPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private String createFileName(String timeseriesId, String interval) {
		return realPath + File.separator + outputPath + File.separator + timeseriesId + "_" + interval + ".png";
	}
	
	private BufferedImage loadImage(String timeseriesId, String interval)
			throws IOException {
		File file = new File(createFileName(timeseriesId, interval));
		FileInputStream fis = new FileInputStream(file);
		return ImageIO.read(fis);
	}

	private TimeseriesDataCollection getTimeseriesData(UndesignedParameterSet parameters) {
        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());
        return timeseriesData;
    }
	
	public void closeGracefully() {
		LOGGER.info("Shut down prerendering task");
		this.cancel();
	}

	public boolean hasPrerenderedImage(String timeseriesId, String interval) {
		File name = new File(createFileName(timeseriesId, interval));
		return name.exists();
	}

	public void writeToOS(String timeseriesId, String interval,
			ServletOutputStream outputStream) {
		try {
			BufferedImage image = loadImage(timeseriesId, interval);
			ImageIO.write(image, "png", outputStream);
		} catch (IOException e) {
			LOGGER.error("Error while loading pre rendered image", e);
		}
	}

	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		realPath = servletConfig.getServletContext().getRealPath("/");
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public TimeseriesMetadataService getTimeseriesMetadataService() {
		return timeseriesMetadataService;
	}

	public void setTimeseriesMetadataService(
			TimeseriesMetadataService timeseriesMetadataService) {
		this.timeseriesMetadataService = timeseriesMetadataService;
	}

	public TimeseriesDataService getTimeseriesDataService() {
		return timeseriesDataService;
	}

	public void setTimeseriesDataService(TimeseriesDataService timeseriesDataService) {
		this.timeseriesDataService = timeseriesDataService;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

}
