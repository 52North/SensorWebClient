
package org.n52.io.v1.data;

import static java.lang.Double.parseDouble;
import static java.lang.Math.toRadians;
<<<<<<< HEAD
import static org.n52.io.crs.CRSUtils.createEpsgForcedXYAxisOrder;
import static org.n52.io.crs.WGS84Util.EPSG_4326;
import static org.n52.io.crs.WGS84Util.getLatitudeDelta;
import static org.n52.io.crs.WGS84Util.getLongitudeDelta;
import static org.n52.io.crs.WGS84Util.normalizeLatitude;
import static org.n52.io.crs.WGS84Util.normalizeLongitude;
import static org.n52.io.geojson.GeojsonPoint.createWithCoordinates;

import org.n52.io.crs.BoundingBox;
import org.n52.io.crs.CRSUtils;
import org.n52.io.geojson.GeojsonPoint;
import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Represents the surrounding area based on a center and a radius. All coordinate calculations are based on a
 * EPSG:4326, lon-lat ordered reference frame.
 */
public class Vicinity {

    private static final int WGS84_EPSG_ID = 4326;

    private CRSUtils inputReference = createEpsgForcedXYAxisOrder();

    private GeometryFactory geometryFactory = inputReference.createGeometryFactory(WGS84_EPSG_ID);

    private Point center;

    private double radius;

    Vicinity() {
        // for serialization
    }

    /**
     * @param center
     *        the lon-lat ordered center.
     * @param radius
     *        the distance around the center
     */
    public Vicinity(Double[] center, String radius) {
        try {
            this.radius = parseDouble(radius);
            this.center = createCenter(createWithCoordinates(center));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse radius.");
        }
        catch (FactoryException e) {
            throw new IllegalArgumentException("Could not parse center.");
        }
    }

    /**
     * @return a bounding rectangle.
     */
    public BoundingBox calculateBounds() {
        double latInRad = toRadians(center.getY());
        double llEasting = normalizeLongitude(center.getX() - getLongitudeDelta(latInRad, radius));
        double llNorthing = normalizeLatitude(center.getY() - getLatitudeDelta(radius));
        double urEasting = normalizeLongitude(center.getX() + getLongitudeDelta(latInRad, radius));
        double urNorthing = normalizeLatitude(center.getY() + getLatitudeDelta(radius));
        GeojsonPoint ll = createWithCoordinates(new Double[]{llEasting, llNorthing});
        GeojsonPoint ur = createWithCoordinates(new Double[]{urEasting, urNorthing});
//        EastingNorthing ll = new EastingNorthing(llEasting, llNorthing, "4326");
//        EastingNorthing ur = new EastingNorthing(urEasting, urNorthing, "4326");
        return new BoundingBox(ll, ur);
    }

    /**
     * @param center
     *        the center point as GeoJSON point.
     * @return the lon-lat ordered WGS84 center point.
     * @throws FactoryException
     *         if creating coordinates fails.
     */
    private Point createCenter(GeojsonPoint center) throws FactoryException {
        Double easting = center.getCoordinates()[0];
        Double northing = center.getCoordinates()[1];
        Coordinate coordinate = inputReference.createCoordinate(EPSG_4326, easting, northing);
        return geometryFactory.createPoint(coordinate);
    }

    /**
     * @param coordinates
     * @throws FactoryException
     * @throws IllegalArgumentException
     *         if coordinates are <code>null</code> or do not contain a two dimensional point.
     */
    public void setCenter(Double[] coordinates) throws FactoryException {
        center = createCenter(createWithCoordinates(coordinates));
=======
import static org.n52.io.crs.AReferencingHelper.createEpsgForcedXYAxisOrder;
import static org.n52.io.crs.WGS84Util.EPSG_4326;
import static org.n52.io.crs.WGS84Util.getLatitudeDelta;
import static org.n52.io.crs.WGS84Util.getLongitudeDelta;
import static org.n52.io.crs.WGS84Util.normalizeLatitude;
import static org.n52.io.crs.WGS84Util.normalizeLongitude;

import org.n52.io.geojson.GeojsonPoint;
import org.n52.io.crs.AReferencingHelper;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Represents the surrounding area based on a center and a radius. All coordinate calculations are based on a
 * EPSG:4326, lon-lat ordered reference frame.
 */
public class Vicinity {

    private static final int WGS84_EPSG_ID = 4326;

    private AReferencingHelper inputReference = createEpsgForcedXYAxisOrder();

    private GeometryFactory geometryFactory = inputReference.createGeometryFactory(WGS84_EPSG_ID);

    private Point center;

    private double radius;

    Vicinity() {
        // for serialization
    }

    /**
     * @param center
     *        the lon-lat ordered center.
     * @param radius
     *        the distance around the center
     */
    public Vicinity(String[] center, String radius) {
        try {
            this.radius = parseDouble(radius);
            this.center = createCenter(GeojsonPoint.create(center));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse radius.");
        }
        catch (FactoryException e) {
            throw new IllegalArgumentException("Could not parse center.");
        }
    }

    /**
     * @return a bounding rectangle.
     */
    public BoundingBox calculateBounds() {
        double latInRad = toRadians(center.getY());
        double llEasting = normalizeLongitude(center.getX() - getLongitudeDelta(latInRad, radius));
        double llNorthing = normalizeLatitude(center.getY() - getLatitudeDelta(radius));
        double ulEasting = normalizeLongitude(center.getX() + getLongitudeDelta(latInRad, radius));
        double ulNorthing = normalizeLatitude(center.getY() + getLatitudeDelta(radius));
        EastingNorthing ll = new EastingNorthing(llEasting, llNorthing, "4326");
        EastingNorthing ur = new EastingNorthing(ulEasting, ulNorthing, "4326");
        return new BoundingBox(ll, ur);
    }

    /**
     * @param center
     *        the center point as GeoJSON point.
     * @return the lon-lat ordered WGS84 center point.
     * @throws FactoryException
     *         if creating coordinates fails.
     */
    private Point createCenter(GeojsonPoint center) throws FactoryException {
        Double easting = new Double(center.getCoordinates()[0]);
        Double northing = parseDouble(center.getCoordinates()[1]);
        Coordinate coordinate = inputReference.createCoordinate(EPSG_4326, easting, northing);
        return geometryFactory.createPoint(coordinate);
    }

    /**
     * @param coordinates
     * @throws FactoryException
     * @throws IllegalArgumentException
     *         if coordinates are <code>null</code> or do not contain a two dimensional point.
     */
    public void setCenter(String[] coordinates) throws FactoryException {
        center = createCenter(GeojsonPoint.create(coordinates));
>>>>>>> branch 'master' of ssh://git@github.com/ridoo/SensorWebClient.git
    }

    /**
     * @param radius
     *        the vicinity's radius.
     * @throws NumberFormatException
     *         if radius could not be parsed to a double value.
     */
    public void setRadius(String radius) {
        this.radius = parseDouble(radius);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [ ");
        sb.append("Center: ").append(center).append(", ");
        sb.append("Radius: ").append(radius).append(" km");
        return sb.append(" ]").toString();
    }

}
