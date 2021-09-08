package demoTest;

import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test jts convexhull and ZGeoMath concave points
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/15
 * @time 20:48
 */
public class Test1ConvexConcave extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<Geometry> polys;
    private List<Geometry> convexHull;
    private List<WB_Segment> maxIndices;
    private List<List<Integer>> concavePoints;

    // utils
    private JtsRender jtsRender;

    public void setup() {
        this.jtsRender = new JtsRender(this);

        // convexHull
        String path = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("./test_convex_hull.3dm")
        ).getPath();

        IG.init();
        IG.open(path);
        this.polys = new ArrayList<>();
        this.convexHull = new ArrayList<>();

        ICurve[] polyLines = IG.layer("test").curves();
        for (ICurve polyLine : polyLines) {
            polys.add(ZTransform.ICurveToJts(polyLine));
        }
        for (Geometry g : polys) {
            convexHull.add(g.convexHull());
        }

        // concave points
        this.concavePoints = new ArrayList<>();
        for (Geometry g : polys) {
            concavePoints.add(ZGeoMath.getConcavePointIndices((Polygon) g));
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        stroke(0);

        noFill();
        // concave
        for (int i = 0; i < polys.size(); i++) {
            for (Integer index : concavePoints.get(i)) {
                ellipse((float) polys.get(i).getCoordinates()[index].x, (float) polys.get(i).getCoordinates()[index].y, 10, 10);
            }
        }

        // convex hull
        for (int i = 0; i < polys.size(); i++) {
            noFill();
            jtsRender.drawGeometry(polys.get(i));
            float ratio = (float) (polys.get(i).getArea() / convexHull.get(i).getArea());
            fill(0);
            textSize(15);
            text(ratio * 100 + "%", (float) convexHull.get(i).getCentroid().getX(), (float) convexHull.get(i).getCentroid().getY());
        }
        stroke(255, 0, 0);
        noFill();
        for (Geometry ch : convexHull) {
            jtsRender.drawGeometry(ch);
        }
    }

}
