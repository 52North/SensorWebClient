
package org.n52.server.ses.feeder.connector;

import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_CAPABILITIES;
import static org.n52.server.da.oxf.DescribeSensorAccessor.getSensorDescriptionAsSensorML;
import static org.n52.server.util.TimeUtil.createIso8601Formatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.oxf.sos.capabilities.SOSContents;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.server.da.oxf.ObservationAccessor;
import org.n52.server.io.RequestConfig;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the communication to SOS.
 */
public class SOSConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SOSConnector.class);

    private String serviceUrl;

    private SOSAdapter sosAdapter;

    private ServiceDescriptor serviceDescriptor;

    private SOSMetadata serviceMetadata;

    /**
     * Instantiates a new SOSConnector.
     * 
     * @param sosURL
     *        The url to send request to the SOS
     */
    public SOSConnector(String sosURL) {
        try {
            serviceUrl = sosURL;
            serviceMetadata = ConfigurationContext.getSOSMetadata(serviceUrl);
            sosAdapter = SosAdapterFactory.createSosAdapter(serviceMetadata);
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available.", e);
        }
    }

    /**
     * Initialize the connection to the SOS.
     * 
     * @return true - if service is running
     */
    public boolean initSosConnection() {
        try {
            String serviceVersion = serviceMetadata.getVersion();
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER, serviceVersion);
            paramCon.addParameterShell(GET_CAPABILITIES_SERVICE_PARAMETER, "SOS");

            Operation operation = new Operation(GET_CAPABILITIES, serviceUrl + "?", serviceUrl);
            OperationResult opResult = sosAdapter.doOperation(operation, paramCon);
            serviceDescriptor = sosAdapter.initService(opResult);
        }
        catch (ExceptionReport e) {
            LOGGER.error("Error while init SOS service: " + e.getMessage());
            return false;
        }
        catch (OXFException e) {
            LOGGER.error("Error while init SOS serivce: " + e.getMessage());
            return false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Gets the offerings.
     * 
     * @return A List of observation offerings for this SOS
     */
    public List<ObservationOffering> getOfferings() {
        ArrayList<ObservationOffering> obsOffs = new ArrayList<ObservationOffering>();
        SOSContents sosCont = (SOSContents) this.serviceDescriptor.getContents();
        for (int i = 0; i < sosCont.getDataIdentificationCount(); i++) {
            obsOffs.add(sosCont.getDataIdentification(i));
        }
        return obsOffs;
    }

    /**
     * Requests sensor description as SensorML 1.0.1 for the set procedure within passed timeseries metadata.<br>
     * <br>
     * For the usage in SES context, all special characters (e.g. &auml;, &uuml;,...) have to be replaced
     * (e.g. by ae, ue, ...).
     * 
     * @param timeseriesMetadata
     *        The given timeseries metadata.
     * @return the sensor description response as SensorML 1.0.1
     */
    public SensorMLDocument getSensorML(TimeseriesMetadata timeseriesMetadata) {
        try {
            String serviceUrl = timeseriesMetadata.getServiceUrl();
            String procedure = timeseriesMetadata.getProcedure();
            SOSMetadata serviceMetadata = ConfigurationContext.getSOSMetadata(serviceUrl);
            XmlObject sml = getSensorDescriptionAsSensorML(procedure, serviceMetadata);
            return (SensorMLDocument) replaceSpecialCharacters(sml);
        }
        catch (Exception e) {
            LOGGER.error("Problems during parsing of describe sensor response: " + e.getMessage(), e);
        }
        return null;
    }

    public ObservationCollectionDocument performGetObservation(TimeseriesFeed timeseriesFeed) throws Exception {
        ObservationAccessor obsAccessor = new ObservationAccessor();
        List<String> fois = new ArrayList<String>();
        TimeseriesMetadata metadata = timeseriesFeed.getTimeseriesMetadata();
        fois.add(metadata.getFeatureOfInterest());
        List<String> phenoms = new ArrayList<String>();
        phenoms.add(metadata.getPhenomenon());
        List<String> procedures = new ArrayList<String>();
        procedures.add(metadata.getProcedure());

        SimpleDateFormat iso8601 = createIso8601Formatter();
        String begin = iso8601.format(timeseriesFeed.getLastFeeded().getTime());
        String end = iso8601.format(new Date());
        ITime time = TimeFactory.createTime(begin + "/" + end);
        RequestConfig request = new RequestConfig(metadata.getServiceUrl(),
                                                  metadata.getOffering(),
                                                  fois,
                                                  phenoms,
                                                  procedures,
                                                  time);
        OperationResult operationResult = obsAccessor.sendRequest(request);
        if (operationResult != null) {
            XmlObject response = XmlObject.Factory.parse(operationResult.getIncomingResultAsStream());
            LOGGER.trace("GetObservation Response: \n{} ", response);
            if (response instanceof ExceptionReportDocument) {
                ExceptionReportDocument exRepDoc = (ExceptionReportDocument) response;
                ExceptionType[] exceptionArray = exRepDoc.getExceptionReport().getExceptionArray();
                throw new Exception(exceptionArray[0].getExceptionTextArray(0));
            } 
            else if (response instanceof ObservationCollectionDocument) {
                return (ObservationCollectionDocument) response;
            }
            LOGGER.warn("Unexpected response: {}", response.schemaType());
        }
        ObservationCollectionDocument emptyCollection = ObservationCollectionDocument.Factory.newInstance();
        emptyCollection.addNewObservationCollection(); // adds an empty member array
        return emptyCollection;
    }
    
    /**
     * Replace special characters.
     * 
     * @param xmlObject
     *        the xml object
     * @return the xml object
     */
    private XmlObject replaceSpecialCharacters(XmlObject xmlObject) {
        String tempStr = xmlObject.toString();
        tempStr = tempStr.replace("Ä", "Ae");
        tempStr = tempStr.replace("Ö", "Oe");
        tempStr = tempStr.replace("Ü", "Ue");
        tempStr = tempStr.replace("ä", "ae");
        tempStr = tempStr.replace("ö", "oe");
        tempStr = tempStr.replace("ü", "ue");
        tempStr = tempStr.replace("ß", "ss");
        tempStr = tempStr.replace("´", "'");
        tempStr = tempStr.replace("`", "'");
        try {
            return XmlObject.Factory.parse(tempStr);
        }
        catch (XmlException e) {
            LOGGER.error("Error while replacing special characters: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets the desc.
     * 
     * @return the desc
     */
    public ServiceDescriptor getDesc() {
        return this.serviceDescriptor;
    }

}
