package demoTest;

import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * description
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/9/8
 * @time 15:39
 */
public class TestIntersection extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<Geometry> polys;
    private WB_Polygon intersectionPoly;

    // utils
    private JtsRender jtsRender;
    private WB_Render render;
    private ZLine ray;

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        loadPolys();
        this.intersectionPoly = ZTransform.PolygonToWB_Polygon((Polygon) polys.get(0));
        this.ray = new ZLine(new ZPoint(650, 250), new ZPoint(mouseX, mouseY));
    }

    private void loadPolys() {
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_convex_hull.3dm")
        ).getPath();

        IG.init();
        IG.open(path);
        this.polys = new ArrayList<>();

        ICurve[] polyLines = IG.layer("test").curves();
        for (ICurve polyLine : polyLines) {
            polys.add(ZTransform.ICurveToJts(polyLine));
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        // intersection
        ray = new ZLine(new ZPoint(650, 250), new ZPoint(mouseX, mouseY));
        ray.display(this);
        List<ZPoint> inters = ZGeoMath.rayPolygonIntersect2D(ray.toLinePD(), intersectionPoly);
        for (ZPoint p : inters) {
            p.displayAsPoint(this, 15);
        }
        List<Integer> interOrder = ZGeoMath.rayPolygonIntersectIndices2D(ray.toLinePD(), intersectionPoly);
        fill(0);
        if (interOrder.size() != 0) {
            for (int i = 0; i < interOrder.size(); i++) {
                text(i + 1, intersectionPoly.getSegment(interOrder.get(i)).getCenter().xf(), intersectionPoly.getSegment(interOrder.get(i)).getCenter().yf());
            }
        }
    }

}
