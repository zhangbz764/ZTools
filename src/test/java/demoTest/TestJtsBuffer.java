package demoTest;

import basicGeometry.ZFactory;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test jts buffer, intersection and geometry centroid
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/3/11
 * @time 13:41
 */
public class TestJtsBuffer extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<Geometry> geometries;
    private List<Point> geoCenters;
    private Geometry buffer;
    private Polygon boundary;
    private Geometry inter;

    private JtsRender jtsRender;

    public void setup() {
        jtsRender = new JtsRender(this);
        Coordinate[] boundaryPts = new Coordinate[5];
        boundaryPts[0] = new Coordinate(100, 100);
        boundaryPts[1] = new Coordinate(900, 100);
        boundaryPts[2] = new Coordinate(900, 900);
        boundaryPts[3] = new Coordinate(100, 900);
        boundaryPts[4] = new Coordinate(100, 100);
        this.boundary = ZFactory.jtsgf.createPolygon(boundaryPts);

        System.out.println(this.getClass().getClassLoader().getResource("").getPath());
        // load
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_jts_buffer.3dm")
        ).getPath();

        IG.init();
        IG.open(path);

        this.geometries = new ArrayList<>();
        this.geoCenters = new ArrayList<>();

        ICurve[] polyLines = IG.layer("geometries").curves();
        for (ICurve polyLine : polyLines) {
            Geometry geo = ZTransform.ICurveToJts(polyLine);
            geometries.add(geo);
            geoCenters.add(geo.getCentroid());
        }

        // buffer
        GeometryFactory gf = new GeometryFactory();

        Geometry[] geos = geometries.toArray(new Geometry[0]);
        GeometryCollection collection = gf.createGeometryCollection(geos);

        BufferOp bop = new BufferOp(collection);
        bop.setEndCapStyle(BufferParameters.CAP_SQUARE);
        buffer = bop.getResultGeometry(20);

        LineString bufferLS = ZTransform.PolygonToLineString((Polygon) buffer).get(0);

        inter = bufferLS.intersection(boundary);
        System.out.println(inter.getGeometryType());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        noFill();
        stroke(0);
        strokeWeight(1);
        for (Geometry g : geometries) {
            jtsRender.drawGeometry(g);
        }
        for (Point c : geoCenters) {
            jtsRender.drawGeometry(c);
        }
        stroke(255, 0, 0);
        strokeWeight(3);
        jtsRender.drawGeometry(inter);
//        jtsRender.drawGeometry(buffer);
    }

}
