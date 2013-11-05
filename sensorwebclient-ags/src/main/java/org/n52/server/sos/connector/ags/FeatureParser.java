package org.n52.server.sos.connector.ags;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.gml.x32.PointType;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlException;
import org.n52.io.crs.CRSUtils;
import org.n52.server.util.XmlHelper;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public final class FeatureParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureParser.class);
    
    private CRSUtils crsUtil;

    public FeatureParser(CRSUtils crsHelper) {
        this.crsUtil = crsHelper;
    }

    public Map<String, Point> parseFeatures(InputStream stream) {
        Map<String, Point> featureLocations = new HashMap<String, Point>();
        try {
            GetFeatureOfInterestResponseDocument responseDoc = GetFeatureOfInterestResponseDocument.Factory.parse(stream);
            GetFeatureOfInterestResponseType response = responseDoc.getGetFeatureOfInterestResponse();
            for (FeaturePropertyType member : response.getFeatureMemberArray()) {
                PointDocument pointDoc = XmlHelper.getPoint(member, crsUtil);
                Point location = getCrs84Location(pointDoc);
                featureLocations.put(pointDoc.getPoint().getId(), location);
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse GetFeatureOfInterestResponse.", e);
        }
        catch (IOException e) {
            LOGGER.error("Could not read GetFeatureOfInterestResponse.", e);
        }
        return featureLocations;
    }

    private Point getCrs84Location(PointDocument pointDoc) {
        try {
            PointType point = pointDoc.getPoint();
            DirectPositionType position = point.getPos();
            String[] lonLat = position.getStringValue().split(" ");
            Double x = Double.parseDouble(lonLat[0]);
            Double y = Double.parseDouble(lonLat[1]);
            String srsName = crsUtil.extractSRSCode(point.getSrsName());
            Point outerRefPoint = crsUtil.createPoint(x, y, srsName);
            return crsUtil.transformOuterToInner(outerRefPoint, srsName);
        }
        catch (FactoryException e) {
            LOGGER.error("Could not create reference helper to parse shape from feature.", e);
            return null;
        }
        catch (TransformException e) {
            LOGGER.error("Could not transform to CRS:84.", e);
            return null;
        }
    }
}
