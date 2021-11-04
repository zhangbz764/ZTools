package advancedGeometry;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import basicGeometry.ZPoint;
import math.ZMath;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_Polygon;

import java.util.Arrays;

/**
 * several shape descriptors for a simple polygon
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/13
 * @time 15:54
 */
public class ZShapeDescriptor {
    private final double convexity;
    private final double solidity;
    private final double rectangularity;
    private final double elongation;
    private final double compactness;
    private final double circularity;
    private final double sphericity;
    private final double eccentricity;

    private ZPoint[] axes;

    private final Geometry convexHull;
    private final Geometry obb;

    /* ------------- constructor ------------- */

    public ZShapeDescriptor(final Polygon poly) {
        Polygon p = poly;
        this.convexHull = p.convexHull();
        this.obb = MinimumDiameter.getMinimumRectangle(p);

        this.convexity = convexity(p, convexHull);
        this.solidity = solidity(p, convexHull);
        this.rectangularity = rectangularity(p, obb);
        this.elongation = elongation(p, obb);
        this.compactness = compactness(p);
        this.circularity = circularity(p, convexHull);
        this.sphericity = sphericity(p);

        double[] eigen = covarianceMatrixEigen(p);
        this.eccentricity = eccentricity(eigen);
        this.axes = mainAxes(eigen);
    }

    public ZShapeDescriptor(final WB_Polygon poly) {
        this(ZTransform.WB_PolygonToPolygon(poly));
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
        return convexity(p, convexHull);
    }

    private static double convexity(final Polygon p, final Geometry convexHull) {
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
        return solidity(p, convexHull);
    }

    private static double solidity(final Polygon p, final Geometry convexHull) {
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
        return rectangularity(p, obb);
    }

    private static double rectangularity(final Polygon p, final Geometry obb) {
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
        return elongation(p, obb);
    }

    private static double elongation(final Polygon p, final Geometry obb) {
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
        return circularity(p, convexHull);
    }

    private static double circularity(final Polygon p, final Geometry convexHull) {
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
        double[] eigen = covarianceMatrixEigen(p);
        return eccentricity(eigen);
    }

    private static double eccentricity(double[] eigen) {
        return eigen[0] / eigen[1];
    }

    /**
     * the principle axes of the polygon (normalized)
     *
     * @param p input polygon
     * @return basicGeometry.ZPoint[]
     */
    public static ZPoint[] mainAxes(final Polygon p) {
        double[] eigen = covarianceMatrixEigen(p);
        return mainAxes(eigen);
    }

    private static ZPoint[] mainAxes(double[] eigen) {
        ZPoint axis1 = new ZPoint(eigen[2], eigen[3]);
        axis1.normalizeSelf();
        ZPoint axis2 = new ZPoint(eigen[4], eigen[5]);
        axis2.normalizeSelf();
        return new ZPoint[]{axis1, axis2};
    }

    /**
     * get the eigenvalues and vectors of the covariance matrix
     *
     * @param p input polygon
     * @return double[]
     */
    private static double[] covarianceMatrixEigen(final Polygon p) {
        double[][] sample = new double[p.getNumPoints() - 1][];
        for (int i = 0; i < p.getNumPoints() - 1; i++) {
            sample[i] = new double[]{
                    p.getCoordinates()[i].getX(), p.getCoordinates()[i].getY()
            };
        }
        double[][] matrix = ZMath.covarianceMatrix(sample);

        Matrix m = new Matrix(matrix);
        EigenvalueDecomposition e = m.eig();
        double[][] valueResult = e.getD().getArray();
        double[][] vecResult = e.getV().getArray();

        double lambda1 = valueResult[0][0];
        double lambda2 = valueResult[1][1];
        double vec1x = vecResult[0][0];
        double vec1y = vecResult[1][0];
        double vec2x = vecResult[0][1];
        double vec2y = vecResult[1][1];

//        double cxx = matrix[0][0];
//        double cyy = matrix[1][1];
//        double cxy = matrix[0][1];
//        double delta = Math.sqrt((cxx + cyy) * (cxx + cyy) - 4 * (cxx * cyy - cxy * cxy));
//        double lambda1 = 0.5 * (cxx + cyy + delta);
//        double lambda2 = 0.5 * (cxx + cyy - delta);
//        double vecK1 = -(cxy + cyy - lambda1) / (cxx + cxy - lambda1);
//        double vecK2 = -(cxy + cyy - lambda2) / (cxx + cxy - lambda2);

        return new double[]{lambda1, lambda2, vec1x, vec1y, vec2x, vec2y};
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

    public ZPoint[] getAxes() {
        return axes;
    }

    @Override
    public String toString() {
        return "ZShapeDescriptor{" +
                "convexity=" + convexity +
                ", solidity=" + solidity +
                ", rectangularity=" + rectangularity +
                ", elongation=" + elongation +
                ", compactness=" + compactness +
                ", circularity=" + circularity +
                ", sphericity=" + sphericity +
                ", eccentricity=" + eccentricity +
                ", axes=" + Arrays.toString(axes) +
                ", convexHull=" + convexHull +
                ", obb=" + obb +
                '}';
    }
}
