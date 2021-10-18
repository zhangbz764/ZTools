package advancedGeometry;

import math.ZMath;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * several shape descriptors for a simple polygon
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/13
 * @time 15:54
 */
public class ZShapeDescriptor {
    private double convexity;
    private double solidity;
    private double rectangularity;
    private double elongation;
    private double compactness;
    private double circularity;
    private double sphericity;
    private double eccentricity;

    private Geometry convexHull;
    private Geometry obb;

    /* ------------- constructor ------------- */

    public ZShapeDescriptor(final Polygon p) {
        this.convexHull = p.convexHull();
        this.obb = MinimumDiameter.getMinimumRectangle(p);

        this.convexity = _convexity(p, convexHull);
        this.solidity = _solidity(p, convexHull);
        this.rectangularity = _rectangularity(p, obb);
        this.elongation = _elongation(p, obb);
        this.compactness = compactness(p);
        this.circularity = _circularity(p, convexHull);
        this.sphericity = sphericity(p);
        this.eccentricity = eccentricity(p);
    }

    /* ------------- member function ------------- */

    /**
     * the ratio of a polygon perimeter to its convex hull perimeter (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double convexity(final Polygon p) {
        Geometry convexHull = p.convexHull();
        return _convexity(p, convexHull);
    }

    private static double _convexity(final Polygon p, final Geometry convexHull) {
        return Math.min(1, convexHull.getLength() / p.getLength());
    }

    /**
     * the ratio of a polygon area to its convex hull area (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double solidity(final Polygon p) {
        Geometry convexHull = p.convexHull();
        return _solidity(p, convexHull);
    }

    private static double _solidity(final Polygon p, final Geometry convexHull) {
        return Math.min(1, p.getArea() / convexHull.getArea());
    }

    /**
     * the ratio of a polygon area to its minimum bounding rectangle area (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double rectangularity(final Polygon p) {
        Geometry obb = MinimumDiameter.getMinimumRectangle(p);
        return _rectangularity(p, obb);
    }

    private static double _rectangularity(final Polygon p, final Geometry obb) {
        return Math.min(1, p.getArea() / obb.getArea());
    }

    /**
     * the ratio of the OBB length to the width (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double elongation(final Polygon p) {
        Geometry obb = MinimumDiameter.getMinimumRectangle(p);
        return _elongation(p, obb);
    }

    private static double _elongation(final Polygon p, final Geometry obb) {
        Polygon rect = (Polygon) obb;
        Coordinate c0 = rect.getCoordinates()[0];
        Coordinate c1 = rect.getCoordinates()[1];
        Coordinate c2 = rect.getCoordinates()[2];
        double l = c0.distance(c1);
        double w = c1.distance(c2);
        if (l >= w) {
            return w / l;
        } else {
            return l / w;
        }
    }

    /**
     * the ratio of a polygon area to a perimeter-equal circle area (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double compactness(final Polygon p) {
        double areaP = p.getArea();
        double length = p.getLength();
        return Math.min(1, (4 * Math.PI * areaP) / (length * length));
    }

    /**
     * the ratio of a polygon area to a circle area with the same convex perimeter (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double circularity(final Polygon p) {
        Geometry convexHull = p.convexHull();
        return _circularity(p, convexHull);
    }

    private static double _circularity(final Polygon p, final Geometry convexHull) {
        double areaP = p.getArea();
        double length = convexHull.getLength();
        return Math.min(1, (4 * Math.PI * areaP) / (length * length));
    }

    /**
     * the ratio of the maximum inscribe circle to the minimum bounding circle (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double sphericity(final Polygon p) {
        MinimumBoundingCircle circle1 = new MinimumBoundingCircle(p);
        double rO = circle1.getRadius();
        MaximumInscribedCircle circle2 = new MaximumInscribedCircle(p, 1);
        Point center = circle2.getCenter();
        Point radiusPoint = circle2.getRadiusPoint();
        double rI = center.distance(radiusPoint);
        return Math.min(1, rI / rO);
    }

    /**
     * the ratio of two principle axes of the polygon (0,1]
     *
     * @param p input polygon
     * @return double
     */
    public static double eccentricity(final Polygon p) {
        double[] axesLengths = covarianceMatrixEigenvalues(p);
        return axesLengths[1] / axesLengths[0];
    }

    /**
     * get the eigenvalues and vectors of the covariance matrix
     *
     * @param p input polygon
     * @return double[]
     */
    public static double[] covarianceMatrixEigenvalues(final Polygon p) {
        double[][] sample = new double[p.getNumPoints() - 1][];
        for (int i = 0; i < p.getNumPoints() - 1; i++) {
            sample[i] = new double[]{
                    p.getCoordinates()[i].getX(), p.getCoordinates()[i].getY()
            };
        }
        double[][] matrix = ZMath.covarianceMatrix(sample);

        double cxx = matrix[0][0];
        double cyy = matrix[1][1];
        double cxy = matrix[0][1];
        double delta = Math.sqrt((cxx + cyy) * (cxx + cyy) - 4 * (cxx * cyy - cxy * cxy));
        double lambda1 = 0.5 * (cxx + cyy + delta);
        double lambda2 = 0.5 * (cxx + cyy - delta);
        System.out.println("lambda1: " + lambda1);
        System.out.println("lambda2: " + lambda2);
        double vecK1 = -(cxy + cyy - lambda1) / (cxx + cxy - lambda1);
        double vecK2 = -(cxy + cyy - lambda2) / (cxx + cxy - lambda2);
        System.out.println("vecK1: " + vecK1);
        System.out.println("vecK2: " + vecK2);

        return new double[]{lambda1, lambda2, vecK2, vecK1};
    }

    /* ------------- setter & getter ------------- */

    public double getConvexity() {
        return convexity;
    }

    public double getSolidity() {
        return solidity;
    }

    public double getRectangularity() {
        return rectangularity;
    }

    public double getElongation() {
        return elongation;
    }

    public double getCompactness() {
        return compactness;
    }

    public double getCircularity() {
        return circularity;
    }

    public double getSphericity() {
        return sphericity;
    }

    public double getEccentricity() {
        return eccentricity;
    }
}
