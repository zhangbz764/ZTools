package demoTest;

import Guo_Cam.CameraController;
import geometry.ZLargestRectangleRatio;
import geometry.ZLine;
import geometry.ZPoint;
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
 * 1.测试jts的convexhull
 * 2.测试找凹点
 * 3.测试直线与多边形交点及交点排序
 * 4.测试LargestRectangle找给定长宽比的最大内接矩形
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/15
 * @time 20:48
 */
public class TestConvexHull extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public List<Geometry> polys;
    public List<Geometry> convexHull;
    public List<WB_Segment> maxIndices;
    public List<List<Integer>> concavePoints;
    public JtsRender jtsRender;
    public WB_Render render;

    public List<ZLargestRectangleRatio> largestRectangles;

    public CameraController gcam;

    public void setup() {
        gcam = new CameraController(this);
        this.jtsRender = new JtsRender(this);
        this.render = new WB_Render(this);

        // 载入几何模型，算凸包
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

        // 算凹点
        this.concavePoints = new ArrayList<>();
        for (Geometry g : polys) {
            concavePoints.add(ZGeoMath.getConcavePointIndices((Polygon) g));
        }

        //找最大矩形
        this.largestRectangles = new ArrayList<>();
        for (Geometry g : convexHull) {
            if (g instanceof Polygon) {
                ZLargestRectangleRatio rectangle = new ZLargestRectangleRatio(ZTransform.jtsPolygonToWB_Polygon((Polygon) g), 0.5);
                rectangle.init();
                largestRectangles.add(rectangle);
            }
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(1000);
        stroke(0);

        // 测试线与多段线交点
        WB_Polygon newPoly = ZTransform.jtsPolygonToWB_Polygon((Polygon) polys.get(0));
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
        // 画凹点，画多边形和凸包，打印比例
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

        // 画最大矩形
        for (ZLargestRectangleRatio largestRectangle : largestRectangles) {
            render.drawPolygonEdges2D(largestRectangle.getLargestRectangle());
        }
    }

}
