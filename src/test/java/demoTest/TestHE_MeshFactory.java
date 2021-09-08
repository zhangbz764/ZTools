package demoTest;

import basicGeometry.ZFactory;
import guo_cam.CameraController;
import math.ZGeoMath;
import processing.core.PApplet;
import render.ZRender;
import wblut.geom.WB_Line;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * test function in WB_GeometryFactory
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/25
 * @time 16:20
 */
public class TestHE_MeshFactory extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    // subtract polygons
    private List<WB_Polygon> outer;
    private List<WB_Polygon> inner;
    private List<WB_Polygon> subtractResult;

    // constrain polygons
    private WB_Polygon container;
    private List<WB_Polygon> constrain;
    private List<WB_Polygon> constrainResult;

    // angle bisector
    private List<WB_Line> bisectors;

    // utils
    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);

        subtract();
        constrain();
    }

    public void subtract() {
        this.outer = new ArrayList<>();
        this.inner = new ArrayList<>();

        this.outer.add(
                new WB_Polygon(
                        new WB_Point(100, 100, 0),
                        new WB_Point(700, 100, 0),
                        new WB_Point(800, 400, 0),
                        new WB_Point(500, 800, 0),
                        new WB_Point(100, 600, 0),
                        new WB_Point(100, 100, 0)
                )
        );

        this.inner.add(
                new WB_Polygon(
                        new WB_Point(250, 200, 0),
                        new WB_Point(250, 400, 0),
                        new WB_Point(450, 400, 0),
                        new WB_Point(450, 200, 0),
                        new WB_Point(250, 200, 0)
                )
        );

        this.subtractResult = ZFactory.wbgf.subtractPolygons2D(outer, ZGeoMath.reversePolygon(inner.get(0)));
        System.out.println("subtract result: " + subtractResult.size());
        System.out.println("holes num: " + subtractResult.get(0).getNumberOfHoles());
    }

    public void constrain() {
        // = intersection
        this.constrain = new ArrayList<>();

        this.container = new WB_Polygon(
                new WB_Point(100, 100, 0),
                new WB_Point(700, 100, 0),
                new WB_Point(800, 400, 0),
                new WB_Point(500, 800, 0),
                new WB_Point(100, 600, 0),
                new WB_Point(100, 100, 0)
        );

        this.constrain.add(
                new WB_Polygon(
                        new WB_Point(300, 200, 0),
                        new WB_Point(300, 300, 0),
                        new WB_Point(500, 300, 0),
                        new WB_Point(500, 200, 0),
                        new WB_Point(300, 200, 0)
                )
        );
        this.constrain.add(
                new WB_Polygon(
                        new WB_Point(250, 200, 0),
                        new WB_Point(250, 400, 0),
                        new WB_Point(450, 400, 0),
                        new WB_Point(450, 200, 0),
                        new WB_Point(250, 200, 0)
                )
        );
        this.constrain.add(
                new WB_Polygon(
                        new WB_Point(0, 0, 0),
                        new WB_Point(200, 0, 0),
                        new WB_Point(200, 200, 0),
                        new WB_Point(0, 200, 0),
                        new WB_Point(0, 0, 0)
                )
        );

        this.constrainResult = ZFactory.wbgf.constrainPolygons2D(constrain, container);
        System.out.println("constrainResult: " + constrainResult.size());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(500);

        fill(255, 0, 0);
        stroke(0);
        strokeWeight(1);
        ZRender.drawWB_PolygonWithHoles(subtractResult.get(0), this);

        translate(1000, 0, 0);
        noFill();
        for (WB_Polygon p : constrain) {
            render.drawPolygonEdges2D(p);
        }
        render.drawPolygonEdges2D(container);
        stroke(255, 0, 0);
        strokeWeight(3);
        for (WB_Polygon p : constrainResult) {
            render.drawPolygonEdges2D(p);
        }
    }

}
