package demoTest;

import advancedGeometry.MaximumInscribedCircle;
import advancedGeometry.ZShapeDescriptor;
import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import processing.core.PApplet;
import render.JtsRender;

/**
 * description
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/14
 * @time 13:13
 */
public class Test10ShapeDescriptor extends PApplet {

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private Polygon polygon;

    private Point centroid;
    private Geometry convexHull;
    private Geometry obb;

    private double r_equalLength;
    private double r_O;
    private Coordinate c_O;
    private double r_I;
    private Point c_I;

    private double[] axes;
    private ZPoint vec1, vec2;
    private float ea, eb;
    private float angle;

    private ZShapeDescriptor shapeDescriptor;

    private JtsRender jtsRender;

    public void setup() {
        this.jtsRender = new JtsRender(this);

//        Coordinate[] coords = new Coordinate[]{
//                new Coordinate(53, 102),
//                new Coordinate(126, 105),
//                new Coordinate(207, 76),
//                new Coordinate(308, 89),
//                new Coordinate(274, 162),
//                new Coordinate(194, 154),
//                new Coordinate(137, 182),
//                new Coordinate(49, 167),
//                new Coordinate(53, 102)
//        };
        Coordinate[] coords = new Coordinate[]{
                new Coordinate(120, 100),
                new Coordinate(150, 150),
                new Coordinate(180, 100),
                new Coordinate(150, 50),
                new Coordinate(120, 100)
        };

        this.polygon = ZFactory.jtsgf.createPolygon(coords);

        this.centroid = polygon.getCentroid();
        this.convexHull = polygon.convexHull();
        this.obb = MinimumDiameter.getMinimumRectangle(polygon);
        this.r_equalLength = polygon.getLength() / (2 * Math.PI);
        MinimumBoundingCircle circle1 = new MinimumBoundingCircle(polygon);
        this.c_O = circle1.getCentre();
        this.r_O = circle1.getRadius();
        MaximumInscribedCircle circle2 = new MaximumInscribedCircle(polygon, 1);
        this.c_I = circle2.getCenter();
        this.r_I = c_I.distance(circle2.getRadiusPoint());

        this.axes = ZShapeDescriptor.covarianceMatrixEigenvalues(polygon);
        this.vec1 = new ZPoint(1, axes[2]).normalize();
        this.vec2 = new ZPoint(1, axes[3]).normalize();
        this.angle = PI * (float) vec1.angleWith(new ZPoint(1, 0)) / 180;


        double areaP = polygon.getArea();
        double areaE = Math.PI * axes[0] * axes[1];
        System.out.println("areaP " + areaP);
        this.ea = (float) (Math.sqrt(areaP / areaE) * axes[0]);
        this.eb = (float) (Math.sqrt(areaP / areaE) * axes[1]);
        System.out.println("ea eb  " + ea + "  " + eb);
        System.out.println("areaE " + Math.PI * ea * eb);
        this.shapeDescriptor = new ZShapeDescriptor(polygon);

        textSize(12);
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);

        pushStyle();
        noFill();
        strokeWeight(3);
        stroke(255, 0, 0);
        jtsRender.drawGeometry(convexHull);
        strokeWeight(1);
        stroke(0);
        jtsRender.drawGeometry(polygon);
        fill(0);
        text("convexity = " + String.format("%.2f", shapeDescriptor.getConvexity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(333, 0);
        pushStyle();
        noStroke();
        fill(200);
        jtsRender.drawGeometry(convexHull);
        fill(150);
        jtsRender.drawGeometry(polygon);
        fill(0);
        text("solidity = " + String.format("%.2f", shapeDescriptor.getSolidity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(333, 0);
        pushStyle();
        noStroke();
        fill(200);
        ellipse((float) centroid.getX(), (float) centroid.getY(), (float) r_equalLength * 2, (float) r_equalLength * 2);
        fill(150, 100);
        jtsRender.drawGeometry(polygon);
        fill(0);
        text("compactness = " + String.format("%.2f", shapeDescriptor.getCompactness()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(-666, 333);
        pushStyle();
        noStroke();
        fill(200);
        jtsRender.drawGeometry(obb);
        fill(150);
        jtsRender.drawGeometry(polygon);
        fill(0);
        text("rectangularity = " + String.format("%.2f", shapeDescriptor.getRectangularity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(333, 0);
        pushStyle();
        stroke(0);
        noFill();
        jtsRender.drawGeometry(obb);
        jtsRender.drawGeometry(polygon);
        strokeWeight(3);
        stroke(255, 0, 0);
        line(
                0.5f * (float) (obb.getCoordinates()[0].getX() + obb.getCoordinates()[1].getX()),
                0.5f * (float) (obb.getCoordinates()[0].getY() + obb.getCoordinates()[1].getY()),
                0.5f * (float) (obb.getCoordinates()[2].getX() + obb.getCoordinates()[3].getX()),
                0.5f * (float) (obb.getCoordinates()[2].getY() + obb.getCoordinates()[3].getY())
        );
        stroke(0, 0, 255);
        line(
                0.5f * (float) (obb.getCoordinates()[1].getX() + obb.getCoordinates()[2].getX()),
                0.5f * (float) (obb.getCoordinates()[1].getY() + obb.getCoordinates()[2].getY()),
                0.5f * (float) (obb.getCoordinates()[3].getX() + obb.getCoordinates()[0].getX()),
                0.5f * (float) (obb.getCoordinates()[3].getY() + obb.getCoordinates()[0].getY())
        );
        fill(0);
        text("elongation = " + String.format("%.2f", shapeDescriptor.getElongation()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(333, 0);
        pushStyle();
        stroke(0);
        strokeWeight(1);
        noFill();
        jtsRender.drawGeometry(polygon);
        strokeWeight(3);
        stroke(255, 0, 0);
        ellipse((float) c_O.getX(), (float) c_O.getY(), (float) r_O * 2, (float) r_O * 2);
        line((float) c_O.getX(), (float) c_O.getY(), (float) (c_O.getX() + r_O), (float) c_O.getY());
        stroke(0, 0, 255);
        ellipse((float) c_I.getX(), (float) c_I.getY(), (float) r_I * 2, (float) r_I * 2);
        line((float) c_I.getX(), (float) c_I.getY(), (float) (c_I.getX() + r_I), (float) c_I.getY());
        noStroke();
        fill(255, 0, 0);
        ellipse((float) c_O.getX(), (float) c_O.getY(), 5, 5);
        fill(0, 0, 255);
        ellipse((float) c_I.getX(), (float) c_I.getY(), 5, 5);
        fill(0);
        text("sphericity = " + String.format("%.2f", shapeDescriptor.getSphericity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();

        translate(-666, 333);
        pushStyle();
        noFill();
        strokeWeight(3);
        stroke(255, 0, 0);
        line(
                (float) centroid.getX() - vec1.xf() * ea,
                (float) centroid.getY() - vec1.yf() * ea,
                (float) centroid.getX() + vec1.xf() * ea,
                (float) centroid.getY() + vec1.yf() * ea
        );
        stroke(0, 0, 255);
        line(
                (float) centroid.getX() - vec2.xf() * eb,
                (float) centroid.getY() - vec2.yf() * eb,
                (float) centroid.getX() + vec2.xf() * eb,
                (float) centroid.getY() + vec2.yf() * eb
        );
        pushMatrix();
        strokeWeight(1);
        stroke(0);
        translate((float) centroid.getX(), (float) centroid.getY());
        rotate(-angle);
        ellipse(0, 0, ea * 2, eb * 2);
        popMatrix();
        noStroke();
        fill(150, 100);
        jtsRender.drawGeometry(polygon);
        fill(0);
        text("eccentricity = " + String.format("%.2f", shapeDescriptor.getEccentricity()), (float) centroid.getX(), (float) centroid.getY() + 120);
        popStyle();
    }

}
