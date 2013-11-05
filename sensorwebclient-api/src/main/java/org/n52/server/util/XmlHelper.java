package org.n52.server.util;

import static org.n52.oxf.xmlbeans.tools.XmlUtil.selectPath;

import java.util.ArrayList;
import java.util.List;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.swe.x101.TextDocument.Text;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.shared.serializable.pojos.sos.Phenomenon;

public final class XmlHelper {

    private static final String SML_101_NAMESPACE = "http://www.opengis.net/sensorML/1.0.1";
    
    private static final String SWE_101_NAMESPACE = "http://www.opengis.net/swe/1.0.1";
    
    private static final String SAMS_20_NAMESPACE = "http://www.opengis.net/samplingSpatial/2.0";

    private static final String GML_321_NAMESPACE = "http://www.opengis.net/gml/3.2";
    
    public static String getShortName(IdentifierList identifiers) {
        String namepace = String.format("declare namespace sml='%s'", SML_101_NAMESPACE);
        String xpath = "$this//*//sml:identifier[@name='shortName']";
        String query = String.format("%s ; %s", namepace, xpath);
        Identifier[] results = (Identifier[]) selectPath(query, identifiers);
        return results.length > 0 ? results[0].getTerm().getValue() : null;
    }
    
    public static String getUniqueId(IdentifierList identifiers) {
        String namepace = String.format("declare namespace sml='%s'", SML_101_NAMESPACE);
        String xpath = "$this//sml:identifier//sml:Term[@definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']";
        String query = String.format("%s ; %s", namepace, xpath);
        Term[] results = (Term[]) selectPath(query, identifiers);
        return results.length > 0 ? results[0].getValue() : null;
    }
    
    public static String[] getRelatedFeatures(Capabilities identifiers) {
        String namepace = String.format("declare namespace swe='%s'", SWE_101_NAMESPACE);
        String xpath = "$this//*//swe:field/swe:Text[@definition='FeatureOfInterestID']";
        String query = String.format("%s ; %s", namepace, xpath);
        Text[] results = (Text[]) selectPath(query, identifiers);
        return results.length > 0 ? getTextValues(results) : null;
    }

    private static String[] getTextValues(Text[] results) {
        List<String> values = new ArrayList<String>();
        for (Text text : results) {
            values.add(text.getValue());
        }
        return values.toArray(new String[0]);
    }
    
    public static PointDocument getPoint(FeaturePropertyType member, CRSUtils referenceHelper) throws XmlException {
        
        String namepace = String.format("declare namespace sams='%s' ", SAMS_20_NAMESPACE);
        namepace += String.format("declare namespace gml='%s'", GML_321_NAMESPACE);
        String xpath = "$this//*//sams:shape";
        String query = String.format("%s ; %s", namepace, xpath);
        XmlObject[] points = (XmlObject[]) selectPath(query, member);
        if (points.length > 0) {
            return PointDocument.Factory.parse(points[0].xmlText());
        } else {
            return null;
        }
    }

    public static Phenomenon[] getRelatedPhenomena(Outputs outputs) {
        OutputList outputList = outputs.getOutputList();
        IoComponentPropertyType[] outputArray = outputList.getOutputArray();
        return null;
    }
    
}
