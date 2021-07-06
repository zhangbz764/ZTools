package demoTest;

import advancedGeometry.largestRectangle.ZLargestRectangle;
import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import math.ZGeoMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import transform.ZTransform;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test jts convexhull
 * concave points
 * line polygon intersection
 * largest rectangle
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/15
 * @time 20:48
 */
public class TestConvexHull extends PApplet {
    public static void main(String[] args) {
        PApplet.main("demoTest.TestConvexHull");
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private List<Geometry> polys;
    private List<Geometry> convexHull;
    private List<WB_Segment> maxIndices;
    private List<List<Integer>> concavePoints;
    private JtsRender jtsRender;
    private WB_Render render;

    private List<WB_Polygon> largestRectangles;

    private CameraController gcam;

    public void setup() {
        gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

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

        // largest rectangle
        this.largestRectangles = new ArrayList<>();
        for (Geometry g : convexHull) {
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
        gcam.drawSystem(1000);
        stroke(0);

        // intersection
        WB_Polygon newPoly = ZTransform.PolygonToWB_Polygon((Polygon) polys.get(0));
        ZLine ray = new ZLine(new ZPoint(650, 250), new ZPoint(mouseX, mouseY));
        ray.display(this);
        List<ZPoint> inters = ZGeoMath.rayPolygonIntersect2D(ray.toLinePD(), newPoly);
        for (ZPoint p : inters) {
            p.displayAsPoint(this, 15);
        }
        List<Integer> interOrder = ZGeoMath.rayPolygonIntersectIndices2D(ray.toLinePD(), newPoly);
        fill(0);
        if (interOrder.size() != 0) {
            for (int i = 0; i < interOrder.size(); i++) {
                text(i + 1, newPoly.getSegment(interOrder.get(i)).getCenter().xf(), newPoly.getSegment(interOrder.get(i)).getCenter().yf());
            }
        }

        noFill();
        // concave
        for (int i = 0; i < polys.size(); i++) {
            for (Integer index : concavePoints.get(i)) {
                ellipse((float) polys.get(i).getCoordinates()[index].x, (float) polys.get(i).getCoordinates()[index].y, 10, 10);
            }
        }
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

        // rectangle
        for (WB_Polygon rect : largestRectangles) {
            render.drawPolygonEdges2D(rect);
        }
    }

}
