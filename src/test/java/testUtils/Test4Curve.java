package testUtils;

import advancedGeometry.ZBSpline;
import advancedGeometry.ZCatmullRom;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Point;
import wblut.nurbs.WB_BSpline;
import wblut.processing.WB_Render;

/**
 * test ZCatmullRom, ZBSpline and WB_BSpline
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/5/14
 * @time 14:48
 */
public class Test4Curve extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Point[] controlPoints;
    private WB_Point[] controlPoints2;
    private WB_Point[] controlPoints3;

    // WB_BSpline
    private WB_BSpline spline;

    // catmull-rom curve
    private ZCatmullRom catmullRom;
    private LineString ls;
    private boolean closed = false;

    // BSpline
    private ZBSpline zbSpline;
    private LineString ls2;
    private int type = 2;

    // utils
    private WB_Render render;
    private JtsRender jtsRender;
    private CameraController gcam;

    public void setup() {
        gcam = new CameraController(this);
        gcam.top();
        this.render = new WB_Render(this);
        this.jtsRender = new JtsRender(this);

        setControlPoints();

        // catmull-rom
        this.catmullRom = new ZCatmullRom(controlPoints, 10, closed);
        this.ls = catmullRom.getAsLineString();

        // WB_BSpline
        this.spline = new WB_BSpline(controlPoints, 3);

        // ZBSpline
        this.zbSpline = new ZBSpline(controlPoints, 3, 100, type);
        System.out.println(zbSpline.getCurvePts().size());
        this.ls2 = zbSpline.getAsLineString();
    }

    private void setControlPoints() {
        this.controlPoints = new WB_Point[5];
        controlPoints[0] = new WB_Point(100, 100);
        controlPoints[1] = new WB_Point(100, 700);
        controlPoints[2] = new WB_Point(500, 800);
        controlPoints[3] = new WB_Point(750, 500);
        controlPoints[4] = new WB_Point(600, 200);

        this.controlPoints2 = new WB_Point[6];
        controlPoints2[0] = new WB_Point(100, 100);
        controlPoints2[1] = new WB_Point(100, 700);
        controlPoints2[2] = new WB_Point(500, 800);
        controlPoints2[3] = new WB_Point(750, 500);
        controlPoints2[4] = new WB_Point(600, 200);
        controlPoints2[5] = new WB_Point(100, 100);

        this.controlPoints3 = new WB_Point[]{
                new WB_Point(1000, 0),
                new WB_Point(950, 800),
                new WB_Point(1600, 950),
                new WB_Point(2100, 750),
                new WB_Point(2150, 0),
                new WB_Point(1800, 0),
                new WB_Point(1600, 600),
                new WB_Point(1200, 550),
                new WB_Point(1150, 200),
                new WB_Point(1550, 0)
        };
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(100);
        for (WB_Point p : controlPoints) {
            render.drawPoint2D(p, 10);
        }
//        for (WB_Point p : controlPoints2) {
//            render.drawPoint2D(p, 10);
//        }
//        for (WB_Point p : controlPoints3) {
//            render.drawPoint2D(p, 10);
//        }
        stroke(255, 0, 0);
        render.drawCurve(spline, 64);

        stroke(0, 255, 0);
        jtsRender.drawGeometry(ls);

        stroke(0, 0, 255);

        // bspline
        for (ZPoint zPoint : zbSpline.getCurvePts()) {
            ellipse(zPoint.xf(), zPoint.yf(), 5, 5);
        }
        jtsRender.drawGeometry(ls2);
    }

    public void keyPressed() {
        if (key == 'q') {
            this.closed = !closed;

            // update catmullRom
            this.catmullRom = new ZCatmullRom(controlPoints, 10, closed);
            this.ls = catmullRom.getAsLineString();
        }
        if (key == 'w') {
            this.type = (type + 1) % 3;

            // update bspline
            this.zbSpline = new ZBSpline(controlPoints, 3, 100, type);
            this.ls2 = zbSpline.getAsLineString();
        }
    }

}
