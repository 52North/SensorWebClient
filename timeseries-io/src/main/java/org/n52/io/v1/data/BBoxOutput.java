//
//package org.n52.io.v1.data;
//
//import org.n52.io.geojson.GeojsonPolygon;
//
//public class BBoxOutput {
//
//    private GeojsonPolygon geometry;
//
//    /**
//     * @param polygon
//     *        the bbox's geometry. If not a box itself, the bbox of the given polygon is being used.
//     */
//    private BBoxOutput(GeojsonPolygon polygon) {
//        this.geometry = polygon.getBounds();
//    }
//    
//    public GeojsonPolygon getGeometry() {
//        return geometry;
//    }
//
//    public void setGeometry(GeojsonPolygon geometry) {
//        this.geometry = geometry.getBounds();
//    }
//
//    public static BBoxOutput createZeroBBoxOutput() {
//        GeojsonPolygon zeroBBox = new GeojsonPolygon();
//        zeroBBox.addCoordinates(new Double[] {0d, 0d});
//        zeroBBox.addCoordinates(new Double[] {0d, 0d});
//        zeroBBox.addCoordinates(new Double[] {0d, 0d});
//        zeroBBox.addCoordinates(new Double[] {0d, 0d});
//        return new BBoxOutput(zeroBBox);
//    }
//
//    public static BBoxOutput createBBoxFor(GeojsonPolygon geometry) {
//        return new BBoxOutput(geometry);
//    }
//
//}
