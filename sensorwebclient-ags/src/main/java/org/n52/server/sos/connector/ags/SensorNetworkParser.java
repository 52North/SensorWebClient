package org.n52.server.sos.connector.ags;

import static org.n52.oxf.xmlbeans.tools.XmlUtil.getXmlAnyNodeFrom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.n52.server.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.sensorML.x101.ComponentDocument;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.SensorDescriptionType;
import net.opengis.swes.x20.SensorDescriptionType.Data;

public final class SensorNetworkParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorNetworkParser.class);

    public SensorNetworkParser() {
    }
    
    public Map<String, ComponentType> parseSensorDescriptions(InputStream stream) {
        Map<String, ComponentType> sensorDescriptions = new HashMap<String, ComponentType>();
        ComponentDocument[] components = parseNetworkComponentsFromDescribeSensorResponse(stream);
        for (ComponentDocument componentDocument : components) {
            ComponentType networkComponent = componentDocument.getComponent();
            if (networkComponent.getIdentificationArray().length > 0) {
                Identification identification = networkComponent.getIdentificationArray(0);
                String id = XmlHelper.getUniqueId(identification.getIdentifierList());
                if (id != null) {
                    sensorDescriptions.put(id, networkComponent);
                }
            }
        }
        return sensorDescriptions;
    }

    private ComponentDocument[] parseNetworkComponentsFromDescribeSensorResponse(InputStream stream) {
        List<ComponentDocument> componentDocs = new ArrayList<ComponentDocument>();
        try {
            DescribeSensorResponseDocument responseDoc = DescribeSensorResponseDocument.Factory.parse(stream);
            DescribeSensorResponseType response = responseDoc.getDescribeSensorResponse();
            for (DescribeSensorResponseType.Description description : response.getDescriptionArray()) {
                SensorDescriptionType sensorDescription = description.getSensorDescription();
                Data descriptionContent = sensorDescription.getData();
                SystemDocument system = (SystemDocument) getXmlAnyNodeFrom(descriptionContent, "System");
                ComponentList components = system.getSystem().getComponents().getComponentList();
                for (Component component : components.getComponentArray()) {
                    componentDocs.add(ComponentDocument.Factory.parse(component.getProcess().getDomNode()));
                }
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse DescribeSensorResponse for procedure.", e);
        }
        catch (IOException e) {
            LOGGER.error("Could not read DescribeSensorResponse for procedure.", e);
        }
        return componentDocs.toArray(new ComponentDocument[0]);
    }
}
