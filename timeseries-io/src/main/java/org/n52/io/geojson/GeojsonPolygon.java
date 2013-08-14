//
//package org.n52.io.geojson;
//
//import org.geotools.geojson.GeoJSON;
//import org.geotools.geojson.geom.GeometryJSON;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//
//
//public class GeojsonPolygon extends GeojsonGeometry {
//
//    private static final long serialVersionUID = 1238376453113150366L;
//    
//    private static final String GEOJSON_TYPE_POLYGON = "Polygon";
//    
//    public static GeojsonPolygon create(Double[][] polygonCoordinates) {
//        GeojsonPolygon sfGeometry = new GeojsonPolygon();
//        sfGeometry.setCoordinates(coordinatesArray);
//        return sfGeometry;
//    }
//
//    public String getType() {
//        return GEOJSON_TYPE_POLYGON;
//    }
//
//    public Double[][][] getCoordinates() {
//        return coordinates.toArray(new Double[0][][]);
//    }
//    
//    public void addCoordinates(Double[] coordinates) {
//        this.coordinates.add(checkCoordinates(coordinates));
//    }
//    
//    @JsonIgnore
//    public GeojsonPolygon getBounds() {
//        double minx = 0d;
//        double maxx = 0d;
//        double miny = 0d;
//        double maxy = 0d;
//        for (Double[] coordinates : this.coordinates) {
//            minx = Math.min(coordinates[0].doubleValue(), minx);
//            maxx = Math.max(coordinates[0].doubleValue(), maxx);
//            miny = Math.min(coordinates[1].doubleValue(), miny);
//            maxy = Math.max(coordinates[1].doubleValue(), maxy);
//        }
//        Double[] ll = new Double[] {new Double(minx), new Double(miny)};
//        Double[] ur = new Double[] {new Double(maxx), new Double(maxy)};
//        return GeojsonPolygon.create(new Double[][]{ll,ur});
//    }
//
//}
