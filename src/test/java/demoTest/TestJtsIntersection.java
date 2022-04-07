package demoTest;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.linemerge.LineMerger;
import processing.core.PApplet;
import render.JtsRender;

import java.util.Collection;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project shopping_mall
 * @date 2022/3/31
 * @time 16:29
 */
public class TestJtsIntersection extends PApplet {
    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private Polygon polygon1, polygon2;
    private LineString lineString1, lineString2;
    private Geometry intersect;

    private JtsRender jtsRender;
    private CameraController gcam;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);
        gcam.top();
        createPolygon();
    }

    private void createPolygon() {
        Coordinate[] coordinates1 = new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(100, 0),
                new Coordinate(50, 100),
                new Coordinate(50, 150),
                new Coordinate(100, 200),
                new Coordinate(0, 200),
                new Coordinate(0, 0)
        };
//        Coordinate[] coordinates2 = new Coordinate[]{
//                new Coordinate(75, 50),
//                new Coordinate(300, 50),
//                new Coordinate(300, 175),
//                new Coordinate(75, 175),
//                new Coordinate(50, 150),
//                new Coordinate(50, 100),
//                new Coordinate(75, 50),
//        };
        Coordinate[] coordinates2 = new Coordinate[]{
                new Coordinate(100, 0),
                new Coordinate(300, 50),
                new Coordinate(300, 175),
                new Coordinate(100, 200),
                new Coordinate(50, 150),
                new Coordinate(50, 100),
                new Coordinate(100, 0),
        };

        this.polygon1 = ZFactory.jtsgf.createPolygon(coordinates1);
        this.polygon2 = ZFactory.jtsgf.createPolygon(coordinates2);

        this.lineString1 = ZFactory.jtsgf.createLineString(coordinates1);
        this.lineString2 = ZFactory.jtsgf.createLineString(coordinates2);

//        this.intersect = polygon1.intersection(polygon2);
        this.intersect = lineString1.intersection(lineString2);
        System.out.println(intersect);

        LineMerger lineMerger = new LineMerger();
        lineMerger.add(intersect);
        Collection merge = lineMerger.getMergedLineStrings();
        System.out.println(merge);
        // result: polygon & polygon intersect = multilinestring
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        strokeWeight(1);
        stroke(0);
        jtsRender.drawGeometry(polygon1);
        jtsRender.drawGeometry(polygon2);

        strokeWeight(3);
        stroke(255,0,0);
        jtsRender.drawGeometry(intersect);
    }

}
