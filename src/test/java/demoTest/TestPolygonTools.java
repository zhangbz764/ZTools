package demoTest;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Arc;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.Arrays;

/**
 * description
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/8/6
 * @time 13:39
 */
public class TestPolygonTools extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private JtsRender jtsRender;

    // jts Polygon reverse & norm
    private Polygon poly1;

    // create arc
    private ZPoint start = new ZPoint(500, 750);
    private ZPoint end = new ZPoint(500, 250);
    private ZPoint center = new ZPoint(500, 500);
    private ZPoint[] arcPts;

    // polygon round
    private Polygon poly2;
    private Polygon roundPoly;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        // jts Polygon reverse
        createPolygon();
        System.out.println("original " + poly1);
        Geometry reverse = poly1.reverse();
        System.out.println("reversed " + reverse);

        // jts polygon normalize
        poly1.normalize();
        System.out.println("normalize " + poly1);
        reverse.normalize();
        System.out.println("normalize reversed " + reverse);
        Geometry norm = reverse.norm();
        System.out.println("norm " + norm);

        // arc
        this.arcPts = ZFactory.createArc(center, start, end, 10, true);

        // round polygon
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
        this.poly2 = (Polygon) ZFactory.jtsgf.createPolygon(shell,holes);
        this.roundPoly = ZGeoMath.roundPolygon(
                poly2,
                10,
                10
        );
    }

    private void createPolygon() {
        Coordinate[] polyEx = new Coordinate[6];
        polyEx[0] = new Coordinate(100, 100);
        polyEx[1] = new Coordinate(700, 100);
        polyEx[2] = new Coordinate(800, 400);
        polyEx[3] = new Coordinate(500, 800);
        polyEx[4] = new Coordinate(100, 600);
        polyEx[5] = polyEx[0];
        LinearRing exterior = ZFactory.jtsgf.createLinearRing(polyEx);

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
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        jtsRender.drawGeometry(poly1);
        for (ZPoint p : arcPts) {
            p.displayAsPoint(this, 5);
        }
        for (int i = 0; i < arcPts.length - 1; i++) {
            line(arcPts[i].xf(), arcPts[i].yf(), arcPts[i + 1].xf(), arcPts[i + 1].yf());
        }

        jtsRender.drawGeometry(roundPoly);
        for (Coordinate c : poly2.getCoordinates()) {
            ellipse((float) c.getX(), (float) c.getY(), 5, 5);
        }
    }

}
