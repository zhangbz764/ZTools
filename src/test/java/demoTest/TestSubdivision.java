package demoTest;

import Guo_Cam.CameraController;
import org.locationtech.jts.geom.GeometryFactory;
import processing.core.PApplet;
import subdivision.*;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * 测试几种剖分方法
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/6
 * @time 14:47
 */
public class TestSubdivision extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    WB_Polygon inputPolygon;
    ZSubdivision subdivision;
    boolean updateShape;

    GeometryFactory gf = new GeometryFactory();
    WB_GeometryFactory wbgf = new WB_GeometryFactory();
    WB_Render render;
    CameraController gcam;

    public void setup() {
        render = new WB_Render(this);
        gcam = new CameraController(this);

        WB_Point[] outer = new WB_Point[9]; // counter clockwise
        outer[0] = new WB_Point(100, 500, 0);
        outer[1] = new WB_Point(500, 400, 0);
        outer[2] = new WB_Point(800, 500, 0);
        outer[3] = new WB_Point(850, 650, 0);
        outer[4] = new WB_Point(700, 650, 0);
        outer[5] = new WB_Point(400, 600, 0);
        outer[6] = new WB_Point(200, 650, 0);
        outer[7] = new WB_Point(100, 600, 0);
        outer[8] = new WB_Point(100, 500, 0);

        WB_Point[] inner0 = new WB_Point[4]; // clockwise
        inner0[0] = new WB_Point(250, 200, 0);
        inner0[1] = new WB_Point(250, 400, 0);
        inner0[2] = new WB_Point(450, 400, 0);
        inner0[3] = new WB_Point(450, 200, 0);
//        inner0[4] = new WB_Point(250, 200, 0);

        WB_Point[] inner1 = new WB_Point[4]; // clockwise
        inner1[0] = new WB_Point(500, 500, 0);
        inner1[1] = new WB_Point(400, 600, 0);
        inner1[2] = new WB_Point(500, 700, 0);
        inner1[3] = new WB_Point(600, 500, 0);
//        inner1[4] = new WB_Point(500, 500, 0);

        WB_Point[][] inner = new WB_Point[][]{inner0, inner1};

//        inputPolygon = wbgf.createPolygonWithHoles(outer, inner);
        inputPolygon = wbgf.createSimplePolygon(outer);

        subdivision = new ZSD_SideStrip(inputPolygon);
        subdivision.performDivide();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        subdivision.display(this, render);
        if (updateShape) {
            subdivision.updateSiteShape();
        }

        translate(0, -300);
        subdivision.displayMesh(render);

        fill(0);
        textSize(15);
        for (int i = 0; i < inputPolygon.getNumberSegments(); i++) {
            text(i, inputPolygon.getPoint(i).xf(), inputPolygon.getPoint(i).yf(), inputPolygon.getPoint(i).zf());
        }
    }

    public void keyPressed() {
        if (key == '1') {
            subdivision = new ZSD_SkeVorStrip(inputPolygon);
            subdivision.setCellConstraint(40);
            subdivision.performDivide();
        } else if (key == '2') {
            subdivision = new ZSD_OBB(inputPolygon);
            subdivision.setCellConstraint(3);
            subdivision.performDivide();
        } else if (key == '3') {
            subdivision = new ZSD_DoubleStrip(inputPolygon);
            subdivision.performDivide();
        } else if (key == '4') {
            subdivision = new ZSD_SideStrip(inputPolygon);
            subdivision.performDivide();
        } else if (key == '5') {
            subdivision = new ZSD_Voronoi(inputPolygon);
            subdivision.setCellConstraint(80);
            subdivision.performDivide();
            subdivision.initializeShapeVector();
        }
        if (key == 'u') {
            updateShape = !updateShape;
        }
    }

}
