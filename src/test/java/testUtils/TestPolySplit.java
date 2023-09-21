package testUtils;

import basicGeometry.ZFactory;
import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;
import transform.ZTransform;
import wblut.geom.*;
import wblut.processing.WB_Render2D;

import java.util.List;

/**
 * test polygon divide and offset methods
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/24
 * @time 17:14
 */
public class TestPolySplit extends PApplet {
    public static void main(String[] args) {
        PApplet.main("testUtils.TestPolySplit");
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    private JtsRender jtsRender;
    private WB_Render2D render;
    private WB_Polygon poly;
    private WB_PolyLine pl;

    // points along boundary
    private WB_Point start = new WB_Point(600, 100);
    private WB_Point[] besides = new WB_Point[2];
    private Coordinate start2 = new Coordinate(925, 200);
    private Coordinate[] besides2 = new Coordinate[2];
    private Coordinate beside3;
    private LineString cut;

    // divide points
    private List<WB_Point> divide;

    // indices and offset
    private int count = 0;
    private int index;
    private WB_Segment offset;
    private double step;

    // most curvature
    private LineString ls;
    private Coordinate curvature;

    public void setup() {
        jtsRender = new JtsRender(this);
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

        // divide by threshold
        step = 50;
        divide = ZGeoMath.dividePolyLineByThreshold(pl, 90, 84);
        println("divide: " + divide.size());

        // offset
        index = count % poly.getNumberSegments();
        println(index);
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);

        // most curvature
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(900, 100),
                new Coordinate(950, 300),
                new Coordinate(960, 650),
                new Coordinate(970, 700),
                new Coordinate(900, 900)
        };
        this.ls = ZFactory.jtsgf.createLineString(coordinates);
        curvature = ZGeoMath.maxCurvaturePt(ls, 100);
        System.out.println(curvature);

        // find points
        besides = ZGeoMath.pointOnEdgeByDist(start, poly, 450);
        besides2 = ZGeoMath.pointOnEdgeByDist(start2, ls, 200);
        beside3 = ZGeoMath.pointOnEdgeByDist(ls, 400, 1, true);
        Coordinate cut1 = new Coordinate(925, 200);
        Coordinate cut2 = new Coordinate(965, 675);
        this.cut = ZFactory.cutLineString2Points(ls, cut1, cut2);
        List<Coordinate> divideTest = ZGeoMath.dividePolyLineByStep(cut, 1000);
        System.out.println(divideTest);
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
        render.drawPoint2D(start, 10);
        jtsRender.drawCoordinate2D(start2, 10);
        for (WB_Point p : besides) {
            render.drawPoint2D(p, 15);
        }
//        for (ZPoint p : besides2) {
//            p.displayAsPoint(this, 15);
//        }
        jtsRender.drawCoordinate2D(beside3, 15);
        popStyle();
        strokeWeight(4);
        render.drawSegment2D(poly.getSegment(index));
        render.drawSegment2D(offset);

        for (WB_Point p : divide) {
            render.drawPoint2D(p, 5);
        }

        jtsRender.drawGeometry(ls);
        jtsRender.drawCoordinate2D(curvature, 10);
        strokeWeight(8);
        jtsRender.drawGeometry(cut);
    }

    public void mouseClicked() {
        count++;
        index = count % poly.getNumberSegments();
        offset = ZGeoMath.offsetWB_PolygonSegment(poly, index, 30);

        WB_Point clo = WB_GeometryOp2D.getClosestPoint2D(new WB_Point(mouseX, mouseY), ZTransform.LineStringToWB_PolyLine(ls));
        start2 = new Coordinate(clo.xd(), clo.yd());
        besides2 = ZGeoMath.pointOnEdgeByDist(start2, ls, 200);
    }

    public void mouseDragged() {
        step = mouseX;
        divide = ZGeoMath.dividePolyLineByStep(poly, step);
        println("divide: " + divide.size());
    }
}
