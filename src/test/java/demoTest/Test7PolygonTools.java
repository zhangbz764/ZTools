package demoTest;

import basicGeometry.ZFactory;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.processing.WB_Render;

/**
 * test polygon tools in ZGeoMath
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/8/6
 * @time 13:39
 */
public class Test7PolygonTools extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    // utils
    private WB_Render render;
    private JtsRender jtsRender;

    // getPointOnPolyEdge()
    private WB_PolyLine pl;
    private WB_Point onLine;

    // jts Polygon reverse & norm
    private Polygon poly1;

    // polygon round
    private Polygon poly2;
    private Polygon roundPoly;

    // polygon smooth
    private Polygon smoothPoly;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        createGeometries();

        // getPointOnPolyEdge()
        System.out.println("poly length: " + ZGeoMath.getPolyLength(pl));
        this.onLine = ZGeoMath.getPointOnPolyEdge(pl, 1500);
        System.out.println("point on line: " + onLine);

        // jts Polygon reverse
        System.out.println("original " + poly1);
        Geometry reverse = poly1.reverse();
        System.out.println("reversed " + reverse);

        // jts polygon normalize
        this.poly1.normalize();
        System.out.println("normalize " + poly1);
        reverse.normalize();
        System.out.println("normalize reversed " + reverse);
        Geometry norm = reverse.norm();
        System.out.println("norm " + norm);

        // round polygon
        this.roundPoly = ZGeoMath.roundPolygon(
                poly2,
                10,
                10
        );

        // smooth poly
        this.smoothPoly = ZGeoMath.smoothPolygon(poly1, 3, 3);
    }

    private void createGeometries() {
        // point on poly
        WB_Point[] plPts = new WB_Point[5];
        plPts[0] = new WB_Point(100, 100);
        plPts[1] = new WB_Point(700, 100);
        plPts[2] = new WB_Point(800, 400);
        plPts[3] = new WB_Point(500, 800);
        plPts[4] = new WB_Point(100, 600);
        this.pl = new WB_PolyLine(plPts);

        // jts reverse & smooth poly
        Coordinate[] polyJtsReverseEx = new Coordinate[6];
        polyJtsReverseEx[0] = new Coordinate(100, 100);
        polyJtsReverseEx[1] = new Coordinate(700, 100);
        polyJtsReverseEx[2] = new Coordinate(800, 400);
        polyJtsReverseEx[3] = new Coordinate(500, 800);
        polyJtsReverseEx[4] = new Coordinate(100, 600);
        polyJtsReverseEx[5] = polyJtsReverseEx[0];
        LinearRing exterior = ZFactory.jtsgf.createLinearRing(polyJtsReverseEx);
        LinearRing[] interior = new LinearRing[2];
        Coordinate[] polyIn_1 = new Coordinate[4];
        polyIn_1[0] = new Coordinate(200, 200);
        polyIn_1[1] = new Coordinate(250, 400);
        polyIn_1[2] = new Coordinate(400, 300);
        polyIn_1[3] = polyIn_1[0];
        interior[0] = ZFactory.jtsgf.createLinearRing(polyIn_1);
        Coordinate[] polyIn_2 = new Coordinate[4];
        polyIn_2[0] = new Coordinate(500, 500);
        polyIn_2[1] = new Coordinate(500, 600);
        polyIn_2[2] = new Coordinate(550, 550);
        polyIn_2[3] = polyIn_2[0];
        interior[1] = ZFactory.jtsgf.createLinearRing(polyIn_2);
        this.poly1 = ZFactory.jtsgf.createPolygon(exterior, interior);

        // round poly
        Coordinate[] polyEx = new Coordinate[7];
        polyEx[0] = new Coordinate(750, 750);
        polyEx[1] = new Coordinate(950, 800);
        polyEx[2] = new Coordinate(850, 850);
        polyEx[3] = new Coordinate(950, 950);
        polyEx[4] = new Coordinate(850, 950);
        polyEx[5] = new Coordinate(750, 950);
        polyEx[6] = polyEx[0];
        LinearRing shell = ZFactory.jtsgf.createLinearRing(polyEx);
        Coordinate[] polyIn1 = new Coordinate[4];
        polyIn1[0] = new Coordinate(800, 800);
        polyIn1[1] = new Coordinate(800, 850);
        polyIn1[2] = new Coordinate(840, 850);
        polyIn1[3] = polyIn1[0];
        Coordinate[] polyIn2 = new Coordinate[5];
        polyIn2[0] = new Coordinate(800, 870);
        polyIn2[1] = new Coordinate(800, 930);
        polyIn2[2] = new Coordinate(850, 930);
        polyIn2[3] = new Coordinate(850, 870);
        polyIn2[4] = polyIn2[0];
        LinearRing[] holes = new LinearRing[]{ZFactory.jtsgf.createLinearRing(polyIn1), ZFactory.jtsgf.createLinearRing(polyIn2)};
        this.poly2 = (Polygon) ZFactory.jtsgf.createPolygon(shell, holes);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);

        jtsRender.drawGeometry(poly1);

        // round poly
        stroke(0, 0, 255);
        jtsRender.drawGeometry(roundPoly);
        for (Coordinate c : poly2.getCoordinates()) {
            ellipse((float) c.getX(), (float) c.getY(), 5, 5);
        }

        // smooth poly
        stroke(0, 255, 0);
        strokeWeight(2);
        jtsRender.drawGeometry(smoothPoly);

        // point on line
        strokeWeight(3);
        stroke(255, 0, 0);
        render.drawPoint2D(onLine, 10);
    }

}
