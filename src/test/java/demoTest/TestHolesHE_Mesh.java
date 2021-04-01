package demoTest;

import geometry.ZFactory;
import math.ZGeoMath;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/25
 * @time 16:20
 */
public class TestHolesHE_Mesh extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    List<WB_Polygon> outer;
    List<WB_Polygon> inner;

    List<WB_Polygon> subtractResult;

    WB_Render render;

    public void setup() {
        render = new WB_Render(this);
        outer = new ArrayList<>();
        inner = new ArrayList<>();

        outer.add(new WB_Polygon(new WB_Point[]{
                new WB_Point(100, 100, 0),
                new WB_Point(700, 100, 0),
                new WB_Point(800, 400, 0),
                new WB_Point(500, 800, 0),
                new WB_Point(100, 600, 0),
                new WB_Point(100, 100, 0)
        }));

        inner.add(new WB_Polygon(new WB_Point[]{
                new WB_Point(250, 200, 0),
                new WB_Point(250, 400, 0),
                new WB_Point(450, 400, 0),
                new WB_Point(450, 200, 0),
                new WB_Point(250, 200, 0)
        }));

        subtractResult = ZFactory.wbgf.subtractPolygons2D(outer.get(0), ZGeoMath.reversePolygon(inner.get(0)));
        System.out.println("sub result: " + subtractResult.size());
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        fill(255, 0, 0);
        render.drawPolygon(subtractResult.get(0));
    }

}
