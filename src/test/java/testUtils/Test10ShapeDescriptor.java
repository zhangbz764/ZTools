package testUtils;

import advancedGeometry.MaximumInscribedCircle;
import advancedGeometry.ZShapeDescriptor;
import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import guo_cam.CameraController;
import igeo.ICurve;
import igeo.IG;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;
import render.ZRender;
import transform.ZJtsTransform;
import transform.ZTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/14
 * @time 13:13
 */
public class Test10ShapeDescriptor extends PApplet {

    public static void main(String[] args) {
        PApplet.main("testUtils.Test10ShapeDescriptor");
    }

    // test shape descriptors of one polygon
    private Polygon polygon;
    private List<ZPoint> randomPts;

    private Point centroid;
    private Geometry convexHull;
    private Geometry obb;

    private double r_equalLength;
    private double r_convex_equalLength;
    private double r_O;
    private Coordinate c_O;
    private double r_I;
    private Point c_I;

    private ZPoint vec1, vec2;

    private ZShapeDescriptor shapeDescriptor;

    // test axes of multiple polygons
    private Polygon[] polygons;
    private ZPoint[][] polyAxesVecs;

    // utils
    private JtsRender jtsRender;
    private CameraController gcam;
    private boolean draw = true;

    /* ------------- settings ------------- */

    public void settings() {
        size(1900, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public void setup() {
        this.jtsRender = new JtsRender(this);
        this.gcam = new CameraController(this);
        gcam.top();
        textSize(12);

        shapeDescriptors();
        axes();

        ZPoint p0 = new ZPoint(100, 0);
        ZPoint p1 = new ZPoint(0, 100);
        ZPoint p2 = new ZPoint(-100, 100);
        ZPoint p3 = new ZPoint(-100, -100);
        ZPoint p4 = new ZPoint(0, -100);
        System.out.println(p0.angleWith(p1));
        System.out.println(p0.angleWith(p2));
        System.out.println(p0.angleWith(p3));
        System.out.println(p0.angleWith(p4));

        System.out.println("convexity = " + String.format("%.2f", shapeDescriptor.getConvexity()));
        System.out.println("solidity = " + String.format("%.2f", shapeDescriptor.getSolidity()));
        System.out.println("elongation = " + String.format("%.2f", shapeDescriptor.getElongation()));
        System.out.println("rectangularity = " + String.format("%.2f", shapeDescriptor.getRectangularity()));
        System.out.println("compactness = " + String.format("%.2f", shapeDescriptor.getCompactness()));
        System.out.println("circularity = " + String.format("%.2f", shapeDescriptor.getCircularity()));
        System.out.println("sphericity = " + String.format("%.2f", shapeDescriptor.getSphericity()));
        System.out.println("eccentricity = " + String.format("%.2f", shapeDescriptor.getEccentricity()));
    }

    private void shapeDescriptors() {
        // create polygon and shape descriptor
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(53, 102),
                new Coordinate(126, 105),
                new Coordinate(207, 76),
                new Coordinate(308, 89),
                new Coordinate(274, 162),
                new Coordinate(194, 154),
                new Coordinate(137, 182),
                new Coordinate(49, 167),
                new Coordinate(53, 102)
        };
//        Coordinate[] coords = new Coordinate[]{
//                new Coordinate(18, -155),
//                new Coordinate(22, -207),
//                new Coordinate(18, -255),
//                new Coordinate(30, -269),
//                new Coordinate(119, -271),
//                new Coordinate(117, -250),
//                new Coordinate(121, -215),
//                new Coordinate(79, -217),
//                new Coordinate(74, -191),
//                new Coordinate(78, -148),
//                new Coordinate(18, -155)
//        };
        this.polygon = ZFactory.jtsgf.createPolygon(coords);
        ZJtsTransform transform = new ZJtsTransform();
        transform.addScale2D(1.5);
        this.polygon = (Polygon) transform.applyToGeometry2D(polygon);
        this.shapeDescriptor = new ZShapeDescriptor(polygon);
        this.randomPts = new ArrayList<>();
        double[] aabb = ZFactory.createJtsAABB2D(polygon);
        for (int i = 0; i < 100; i++) {
            ZPoint p = new ZPoint(
                    random((float) aabb[0], (float) aabb[2]),
                    random((float) aabb[1], (float) aabb[3])
            );
            if (polygon.contains(p.toJtsPoint())) {
                randomPts.add(p);
            }
        }
        System.out.println(randomPts.size());
        this.centroid = polygon.getCentroid();
        this.convexHull = polygon.convexHull();
        this.obb = MinimumDiameter.getMinimumRectangle(polygon);
        this.r_equalLength = polygon.getLength() / (2 * Math.PI);
        this.r_convex_equalLength = convexHull.getLength() / (2 * Math.PI);
        MinimumBoundingCircle circle1 = new MinimumBoundingCircle(polygon);
        this.c_O = circle1.getCentre();
        this.r_O = circle1.getRadius();
        MaximumInscribedCircle circle2 = new MaximumInscribedCircle(polygon, 1);
        this.c_I = circle2.getCenter();
        this.r_I = c_I.distance(circle2.getRadiusPoint());

        this.vec1 = shapeDescriptor.getAxes()[0]; // sub
        this.vec2 = shapeDescriptor.getAxes()[1]; // main
    }

    private void axes() {
        String path = ".\\src\\test\\resources\\test_shape_descriptors.3dm";
        IG.init();
        IG.open(path);
        ICurve[] polyLines = IG.layer("blocks").curves();
        this.polygons = new Polygon[polyLines.length];
        for (int i = 0; i < polyLines.length; i++) {
            polygons[i] = (Polygon) ZTransform.ICurveToJts(polyLines[i]);
        }

        this.polyAxesVecs = new ZPoint[polygons.length][];
        for (int i = 0; i < polygons.length; i++) {
            ZPoint[] vecs = ZShapeDescriptor.mainAxes(polygons[i]);
            polyAxesVecs[i] = vecs;
        }
    }

    public void keyPressed() {
        if (key == '1') {
            draw = !draw;
        }
        if (key == 's') {
            String className = getClass().getSimpleName();
            save("./src/test/resources/exampleImgs/" + className + ".jpg");
        }
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
//        gcam.drawSystem(100);

        if (draw) {
            pushMatrix();
            drawDescriptors();
            popMatrix();
        } else {
            drawPolygonAxes();
        }

    }

    private void drawDescriptors() {
        // convexity
        pushStyle();
        noFill();
        strokeWeight(3);
        stroke(255, 0, 0);
        jtsRender.drawGeometry(convexHull);
//        stroke(0,146,69);
//        for (ZPoint p : randomPts) {
//            p.displayAsPoint(this, 1);
//        }
        strokeWeight(2);
        stroke(0);
        jtsRender.drawGeometry(polygon);
        fill(0);
//        text("convexity = " + String.format("%.2f", shapeDescriptor.getConvexity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // solidity
        translate(333, 0);
        pushStyle();
        noStroke();
        fill(200);
        jtsRender.drawGeometry(convexHull);
        fill(150);
        jtsRender.drawGeometry(polygon);
        fill(0);
//        text("solidity = " + String.format("%.2f", shapeDescriptor.getSolidity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // elongation
        translate(333, 0);
        pushStyle();
        stroke(0);
        noFill();
        strokeWeight(1);
        jtsRender.drawGeometry(obb);
        strokeWeight(2);
        jtsRender.drawGeometry(polygon);
        strokeWeight(3);
        stroke(255, 0, 0);
        line(
                0.5f * (float) (obb.getCoordinates()[0].getX() + obb.getCoordinates()[1].getX()),
                0.5f * (float) (obb.getCoordinates()[0].getY() + obb.getCoordinates()[1].getY()),
                0.5f * (float) (obb.getCoordinates()[2].getX() + obb.getCoordinates()[3].getX()),
                0.5f * (float) (obb.getCoordinates()[2].getY() + obb.getCoordinates()[3].getY())
        );
        stroke(0, 146, 69);
        line(
                0.5f * (float) (obb.getCoordinates()[1].getX() + obb.getCoordinates()[2].getX()),
                0.5f * (float) (obb.getCoordinates()[1].getY() + obb.getCoordinates()[2].getY()),
                0.5f * (float) (obb.getCoordinates()[3].getX() + obb.getCoordinates()[0].getX()),
                0.5f * (float) (obb.getCoordinates()[3].getY() + obb.getCoordinates()[0].getY())
        );
        fill(0);
//        text("elongation = " + String.format("%.2f", shapeDescriptor.getElongation()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // rectangularity
        translate(333, 0);
        pushStyle();
        noStroke();
        fill(200);
        jtsRender.drawGeometry(obb);
        fill(150);
        jtsRender.drawGeometry(polygon);
        fill(0);
//        text("rectangularity = " + String.format("%.2f", shapeDescriptor.getRectangularity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // compactness
        translate(-999, -333);
        pushStyle();
        noStroke();
        fill(200);
        ellipse((float) centroid.getX(), (float) centroid.getY(), (float) r_equalLength * 2, (float) r_equalLength * 2);
        fill(150);
        jtsRender.drawGeometry(polygon);
        fill(0);
//        text("compactness = " + String.format("%.2f", shapeDescriptor.getCompactness()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // circularity
        translate(333, 0);
        pushStyle();
        noStroke();
        fill(200);
        ellipse((float) centroid.getX(), (float) centroid.getY(), (float) r_convex_equalLength * 2, (float) r_convex_equalLength * 2);
        fill(150);
        jtsRender.drawGeometry(polygon);
        noFill();
        strokeWeight(2);
        stroke(0);
        jtsRender.drawGeometry(convexHull);
        popStyle();

        // sphericity
        translate(333, 0);
        pushStyle();
        stroke(0);
        strokeWeight(2);
        noFill();
        jtsRender.drawGeometry(polygon);
        strokeWeight(3);
        stroke(255, 0, 0);
        ellipse((float) c_O.getX(), (float) c_O.getY(), (float) r_O * 2, (float) r_O * 2);
        line((float) c_O.getX(), (float) c_O.getY(), (float) (c_O.getX() + r_O), (float) c_O.getY());
        stroke(0, 146, 69);
        ellipse((float) c_I.getX(), (float) c_I.getY(), (float) r_I * 2, (float) r_I * 2);
        line((float) c_I.getX(), (float) c_I.getY(), (float) (c_I.getX() + r_I), (float) c_I.getY());
        noStroke();
        fill(255, 0, 0);
        ellipse((float) c_O.getX(), (float) c_O.getY(), 5, 5);
        fill(0, 146, 69);
        ellipse((float) c_I.getX(), (float) c_I.getY(), 5, 5);
        fill(0);
//        text("sphericity = " + String.format("%.2f", shapeDescriptor.getSphericity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        // eccentricity
        translate(333, 0);
        pushStyle();
        noFill();
        strokeWeight(3);
        stroke(255, 0, 0);
        line(
                (float) centroid.getX() - vec1.xf() * 100,
                (float) centroid.getY() - vec1.yf() * 100,
                (float) centroid.getX() + vec1.xf() * 100,
                (float) centroid.getY() + vec1.yf() * 100
        );
        stroke(0, 146, 69);
        line(
                (float) (centroid.getX() - vec2.xf() * 100 * shapeDescriptor.getEccentricity()),
                (float) (centroid.getY() - vec2.yf() * 100 * shapeDescriptor.getEccentricity()),
                (float) (centroid.getX() + vec2.xf() * 100 * shapeDescriptor.getEccentricity()),
                (float) (centroid.getY() + vec2.yf() * 100 * shapeDescriptor.getEccentricity())
        );
        noStroke();
        fill(150, 100);
        jtsRender.drawGeometry(polygon);
        fill(0);
//        text("eccentricity = " + String.format("%.2f", shapeDescriptor.getEccentricity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();
    }

    private void drawPolygonAxes() {
        for (int i = 0; i < polygons.length; i++) {
            pushStyle();
            jtsRender.drawGeometry(polygons[i]);
            jtsRender.drawGeometry(MinimumDiameter.getMinimumRectangle(polygons[i]));
            stroke(255, 0, 0);
            ZRender.drawZPointAsVec2D(
                    this,
                    polyAxesVecs[i][0],
                    new ZPoint(polygons[i].getCentroid()),
                    50,
                    5
            );
//            polyAxesVecs[i][0].displayAsVector(
//                    this,
//                    new ZPoint(polygons[i].getCentroid()),
//                    -50,
//                    5
//            );

            stroke(0, 146, 69);
            ZRender.drawZPointAsVec2D(
                    this,
                    polyAxesVecs[i][1],
                    new ZPoint(polygons[i].getCentroid()),
                    50,
                    5
            );
//            polyAxesVecs[i][1].displayAsVector(
//                    this,
//                    new ZPoint(polygons[i].getCentroid()),
//                    -50,
//                    5
//            );

            popStyle();
        }
    }
}
