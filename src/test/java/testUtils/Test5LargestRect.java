package testUtils;

import advancedGeometry.largestQuad.ZLargestQuad;
import advancedGeometry.largestRectangle.ZLargestRectangle;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render;

/**
 * test ZLargestRectangle
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/9/7
 * @time 17:48
 */
public class Test5LargestRect extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testUtils.Test5LargestRect");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Polygon boundary;
    private WB_Polygon largestQuad;
    private WB_Polygon largestRect;

    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        cal();
    }

    private void cal() {
        // random boundary polygon
        WB_Point[] pts = new WB_Point[8];
        double angle = Math.PI / 4;
        for (int i = 0; i < 8; i++) {
            WB_Vector v = new WB_Vector(Math.cos(angle * i), Math.sin(angle * i));
            pts[i] = new WB_Point(0, 0).add(v.scale(Math.random() * 50 + 50));
        }
        this.boundary = new WB_Polygon(pts);

        // largest rectangle
        long t1 = System.currentTimeMillis();
        ZLargestRectangle rectangle = new ZLargestRectangle(boundary);
        rectangle.init();
        this.largestRect = rectangle.getRectResult_WB();
        // largest quad
        long t2 = System.currentTimeMillis();
        ZLargestQuad quad = new ZLargestQuad(boundary);
        quad.init();
        this.largestQuad = quad.getQuadResult_WB();
        long t3 = System.currentTimeMillis();

        System.out.println("largest rectangle: " + (t2 - t1) + "ms");
        System.out.println("largest quad: " + (t3 - t2) + "ms");
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        render.drawPolygonEdges(boundary);

        // rectangle
        strokeWeight(3);
        stroke(255, 0, 0);
        render.drawPolygonEdges2D(largestRect);


        translate(200, 0);
        strokeWeight(1);
        stroke(0);
        render.drawPolygonEdges(boundary);
        // quad
        strokeWeight(3);
        stroke(0, 0, 255);
        render.drawPolygonEdges2D(largestQuad);
    }

    public void keyPressed() {
        if (key == 's') {
            cal();
        }
    }
}
