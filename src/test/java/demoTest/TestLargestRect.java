package demoTest;

import advancedGeometry.largestRectangle.ZLargestRectangle;
import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project Ztools
 * @date 2023/2/16
 * @time 16:59
 */
public class TestLargestRect extends PApplet {

    public static void main(String[] args) {
        PApplet.main("demoTest.TestLargestRect");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController cam;

    private List<WB_Polygon> polys;
    private List<WB_Polygon> rects;

    public void setup() {
        this.cam = new CameraController(this);
        this.render = new WB_Render(this);

        // add polygons
        this.polys = new ArrayList<>();
        polys.add(new WB_Polygon(new WB_Point[]{
                new WB_Point(100+0, 0, 0),
                new WB_Point(100+50, -12, 0),
                new WB_Point(100+100, 25, 0),
                new WB_Point(100+80, 50, 0),
                new WB_Point(100+100, 100, 0),
                new WB_Point(100-20, 80, 0),
                new WB_Point(100+0, 0, 0)
        }
        ));

        // get rectangles
        this.rects = new ArrayList<>();
        for (WB_Polygon g : polys) {
            ZLargestRectangle rectangle = new ZLargestRectangle(g);
            rectangle.init();
            rects.add(rectangle.getRectangleResult());
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        for (WB_Polygon g : polys) {
            render.drawPolygonEdges2D(g);
        }

        // rectangle
        strokeWeight(3);
        stroke(255, 0, 0);
        for (WB_Polygon rect : rects) {
            render.drawPolygonEdges2D(rect);
        }
    }

}
