package demoTest;

import advancedGeometry.ZCatmullRom;
import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import render.JtsRender;
import wblut.geom.WB_Point;
import wblut.nurbs.WB_BSpline;
import wblut.nurbs.WB_NurbsKnot;
import wblut.nurbs.WB_RBSpline;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * test Catmull-Rom curve and WB_BSpline
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
    private WB_BSpline spline;
    private WB_RBSpline rbSpline;

    private List<ZPoint> cps;
    private ZCatmullRom catmullRom;
    private boolean closed = false;

    private WB_Render render;
    private JtsRender jtsRender;
    private LineString ls;

    public void setup() {
        this.render = new WB_Render(this);
        this.jtsRender = new JtsRender(this);

        this.controlPoints = new WB_Point[5];
        controlPoints[0] = new WB_Point(100, 100);
        controlPoints[1] = new WB_Point(100, 300);
        controlPoints[2] = new WB_Point(500, 500);
        controlPoints[3] = new WB_Point(700, 700);
        controlPoints[4] = new WB_Point(800, 700);

        WB_NurbsKnot knot = new WB_NurbsKnot(5, 2);
        this.spline = new WB_BSpline(controlPoints, knot);
        this.rbSpline = new WB_RBSpline(controlPoints, knot);

        this.cps = new ArrayList<>();
        for (WB_Point p : controlPoints) {
            cps.add(new ZPoint(p));
        }
        this.catmullRom = new ZCatmullRom(cps, 10, closed);
        List<ZPoint> splinePoints = catmullRom.getCurveDividePoints();

        Coordinate[] coordinates = new Coordinate[splinePoints.size()];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = splinePoints.get(i).toJtsCoordinate();
        }
        ls = ZFactory.jtsgf.createLineString(coordinates);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        for (ZPoint p : cps) {
            p.displayAsPoint(this, 10);
        }
//        render.drawCurve(rbSpline, 32);

        jtsRender.drawGeometry(ls);
    }

    public void mouseDragged() {
        for (ZPoint pt : cps) {
            if (new ZPoint(mouseX, mouseY).distance(pt) < 10) {
                pt.set(mouseX, mouseY);
                this.catmullRom = new ZCatmullRom(cps, 10, closed);
                List<ZPoint> splinePoints = catmullRom.getCurveDividePoints();

                Coordinate[] coordinates = new Coordinate[splinePoints.size()];
                for (int i = 0; i < coordinates.length; i++) {
                    coordinates[i] = splinePoints.get(i).toJtsCoordinate();
                }
                ls = ZFactory.jtsgf.createLineString(coordinates);
                break;
            }
        }
    }

    public void keyPressed() {
        if (key == '1') {
            this.cps = new ArrayList<>();
            for (WB_Point p : controlPoints) {
                cps.add(new ZPoint(p));
            }
            this.catmullRom = new ZCatmullRom(cps, 10, closed);
            List<ZPoint> splinePoints = catmullRom.getCurveDividePoints();

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
