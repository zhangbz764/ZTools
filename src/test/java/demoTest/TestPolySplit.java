package demoTest;

import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render2D;

import java.util.List;

/**
 * test polygon split and offset methods
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/24
 * @time 17:14
 */
public class TestPolySplit extends PApplet {
    public void settings() {
        size(1000, 1000, P2D);
    }

    private WB_Render2D render;
    private WB_Polygon poly;
    private WB_PolyLine pl;

    // points along boundary
    private ZPoint test = new ZPoint(600, 100);
    private ZPoint[] besides = new ZPoint[2];

    // split points
    private List<ZPoint> split;

    // indices and offset
    private int count = 0;
    private int index;
    private ZLine offset;
    private double step;

    public void setup() {
        render = new WB_Render2D(this);

        // create
        WB_Point[] pts1 = new WB_Point[6]; // polygon
        WB_Point[] pts2 = new WB_Point[5]; // polyline
        pts2[0] = pts1[0] = new WB_Point(100, 100);
        pts2[1] = pts1[1] = new WB_Point(700, 100);
        pts2[2] = pts1[2] = new WB_Point(800, 400);
        pts2[3] = pts1[3] = new WB_Point(500, 800);
        pts2[4] = pts1[4] = new WB_Point(100, 600);
        pts1[5] = new WB_Point(100, 100);
        this.poly = new WB_Polygon(pts1);
        this.pl = new WB_PolyLine(pts2);

        // find points
        besides = ZGeoMath.pointsOnEdgeByDist(test, poly, 450);

        // split by threshold
        step = 50;
        split = ZGeoMath.splitPolyLineByThreshold(pl, 90, 84);
        println("split: " + split.size());

        // offset
        index = count % poly.getNumberSegments();
        println(index);
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);
    }

    public void draw() {
        background(255);
        stroke(0);
        noFill();
        strokeWeight(1);
//        render.drawPolygonEdges2D(poly);
        render.drawPolyLine2D(pl);
        pushStyle();
        fill(255, 0, 0);
        render.drawPoint2D(poly.getSegment(index).getOrigin(), 10);
        fill(0, 0, 255);
        render.drawPoint2D(poly.getSegment(index).getEndpoint(), 10);

        fill(0, 255, 0);
        test.displayAsPoint(this);
        for (ZPoint p : besides) {
            p.displayAsPoint(this);
        }
        popStyle();
        strokeWeight(4);
        render.drawSegment2D(poly.getSegment(index));
        offset.display(this);

        for (ZPoint p : split) {
            p.displayAsPoint(this);
        }

    }

    public void mouseClicked() {
        count++;
        index = count % poly.getNumberSegments();
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);
    }

    public void mouseDragged() {
        step = mouseX;
        split = ZGeoMath.splitPolyLineByStep(poly, step);
        println("split: " + split.size());
    }
}
