package testMyself;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import processing.core.PApplet;
import render.ZRender;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * description
 *
 * @author zbz_lennovo
 * @project shopping_mall
 * @date 2021/10/6
 * @time 17:03
 */
public class Test9ArcCircle extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    // create arc
    private ZPoint start = new ZPoint(500, 750);
    private ZPoint end = new ZPoint(500, 250);
    private ZPoint center = new ZPoint(500, 500);
    private ZPoint[] arcPts;

    // circle intersection
    private WB_Circle circle;
    private WB_Segment segment;
    private List<WB_Point> intersections1;
    private List<WB_Point> intersections2;
    private WB_Polygon polygon;

    // utils
    private WB_Render render;

    public void setup() {
        this.render = new WB_Render(this);

        // arc
        this.arcPts = ZFactory.createArc(center, start, end, 10, true);

        // circle intersection
        this.segment = new WB_Segment(
                new WB_Point(600, 200), new WB_Point(600, 800)
        );
        this.polygon = new WB_Polygon(
                new WB_Point[]{
                        new WB_Point(500, 500),
                        new WB_Point(750, 500),
                        new WB_Point(520, 720),
                        new WB_Point(500, 700),
                        new WB_Point(500, 500),
                }
        );
        this.circle = new WB_Circle(
                new WB_Point(500, 500),
                200
        );
        this.intersections1 = ZGeoMath.segmentCircleIntersection2D(segment, circle);
        this.intersections2 = ZGeoMath.polylineCircleIntersection(polygon, circle);
        System.out.println(intersections2.size());
        System.out.println(intersections2);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        // arc
        for (ZPoint p : arcPts) {
            ZRender.drawZPoint2D(this, p, 5);
        }
        for (int i = 0; i < arcPts.length - 1; i++) {
            line(arcPts[i].xf(), arcPts[i].yf(), arcPts[i + 1].xf(), arcPts[i + 1].yf());
        }

        // circle intersection
        render.drawCircle(circle);
        stroke(255, 0, 0);
        strokeWeight(2.5f);
        render.drawSegment2D(segment);
        for (WB_Point p : intersections1) {
            ellipse(p.xf(), p.yf(), 10, 10);
        }
        stroke(0, 255, 0);
        strokeWeight(2.5f);
        render.drawPolygonEdges2D(polygon);
        for (WB_Point p : intersections2) {
            ellipse(p.xf(), p.yf(), 10, 10);
        }
    }

}
