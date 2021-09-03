package demoTest;

import advancedGeometry.ZCatmullRom;
import advancedGeometry.ZBSpline;
import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Point;
import wblut.nurbs.WB_BSpline;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * test several curves
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/5/14
 * @time 14:48
 */
public class TestCurve extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Point[] controlPoints;
    private WB_Point[] controlPoints3;
    private WB_Point[] controlPoints2;

    // WB_BSpline
    private WB_BSpline spline;

    // catmull-rom curve
    private List<ZPoint> cps;
    private ZCatmullRom catmullRom;
    private boolean closed = false;
    private LineString ls;

    // BSpline
    private ZBSpline zbSpline;
    private List<ZPoint> spPts2;

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
        this.cps = new ArrayList<>();
        for (WB_Point p : controlPoints) {
            cps.add(new ZPoint(p));
        }
        this.catmullRom = new ZCatmullRom(cps, 10, closed);
        this.ls = catmullRom.getAsLineString();

        // WB_BSpline
        this.spline = new WB_BSpline(controlPoints, 3);

        // ZBSpline
        this.zbSpline = new ZBSpline(Arrays.asList(controlPoints), 3, 100, 2);
        spPts2 = zbSpline.getCurvePts();
        System.out.println("spPts2.size(): " + spPts2.size() + " " + spPts2);

        ZPoint v1 = new ZPoint(10, 0);
        ZPoint v2 = new ZPoint(-1, -1);
        System.out.println(v1.angleWith(v2));
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
        for (ZPoint p : cps) {
            p.displayAsPoint(this, 10);
        }
        for (WB_Point p : controlPoints2) {
            render.drawPoint2D(p, 10);
        }
        for (WB_Point p : controlPoints3) {
            render.drawPoint2D(p, 10);
        }
        stroke(255, 0, 0);
        render.drawCurve(spline, 64);

        stroke(0, 255, 0);
        jtsRender.drawGeometry(ls);

        stroke(0, 0, 255);

        // bspline
        for (ZPoint zPoint : spPts2) {
            ellipse(zPoint.xf(), zPoint.yf(), 5, 5);
        }
        for (int i = 0; i < spPts2.size() - 1; i++) {
            line(spPts2.get(i).xf(), spPts2.get(i).yf(), spPts2.get(i + 1).xf(), spPts2.get(i + 1).yf());
        }
    }

    public void mouseDragged() {
//        if (mouseButton == LEFT) {
//            double[] pointer = gcam.getCoordinateFromScreenDouble(mouseX, mouseY, 0);
//            double[] mouse = new double[]{pointer[0] + width * 0.5, pointer[1] + height * 0.5};
//            for (int i = 0; i < controlPoints2.length; i++) {
//                if (new WB_Point(mouse[0], mouse[1]).getDistance2D(controlPoints2[i]) < 10) {
//                    controlPoints2[i].set(mouse[0], mouse[1]);
//                    if (i == 0) {
//                        controlPoints2[controlPoints2.length - 1].set(mouse[0], mouse[1]);
//                    }
//                    this.zbSpline2 = new ZBSpline2(3, Arrays.asList(controlPoints_WB), true, 100);
//                    spPts2 = zbSpline.getCurvePts();
//                    break;
//                }
//            }
//        }
    }

    public void keyPressed() {
        if (key == '1') {

        }
        if (key == 'w') {
            this.cps = new ArrayList<>();
            for (WB_Point p : controlPoints) {
                cps.add(new ZPoint(p));
            }
            this.catmullRom = new ZCatmullRom(cps, 10, closed);
            List<ZPoint> splinePoints = catmullRom.getCurveDividePts();

            Coordinate[] coordinates = new Coordinate[splinePoints.size()];
            for (int i = 0; i < coordinates.length; i++) {
                coordinates[i] = splinePoints.get(i).toJtsCoordinate();
            }
            ls = ZFactory.jtsgf.createLineString(coordinates);
        }
        if (key == 'q') {
            this.closed = !closed;
        }
    }

}
