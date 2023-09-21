package testUtils;

import advancedGeometry.largestRectangle.ZLargestRectangle;
import basicGeometry.ZFactory;
import guo_cam.CameraController;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

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

    private List<Geometry> polys;
    private List<WB_Polygon> largestRectangles;

    private JtsRender jtsRender;
    private WB_Render render;
    private CameraController gcam;

    public void setup() {
        this.gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        this.polys = new ArrayList<>();
        polys.add(ZFactory.jtsgf.createPolygon(
                new Coordinate[]{
                        new Coordinate(0,0,0),
                        new Coordinate(50,-12,0),
                        new Coordinate(100,25,0),
                        new Coordinate(80,50,0),
                        new Coordinate(100,100,0),
                        new Coordinate(-20,80,0),
                        new Coordinate(0,0,0)
                }
        ));


        // largest rectangle
        this.largestRectangles = new ArrayList<>();
        for (Geometry g : polys) {
            if (g instanceof Polygon) {
                ZLargestRectangle rectangle = new ZLargestRectangle(ZTransform.PolygonToWB_Polygon((Polygon) g));
                rectangle.init();
                largestRectangles.add(rectangle.getRectangleResult());
            }
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        strokeWeight(1);
        stroke(0);
        for (Geometry g : polys) {
            jtsRender.drawGeometry(g);
        }

        // rectangle
        strokeWeight(3);
        stroke(255, 0, 0);
        for (WB_Polygon rect : largestRectangles) {
            render.drawPolygonEdges2D(rect);
        }
    }
    public void keyPressed() {
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }
}
