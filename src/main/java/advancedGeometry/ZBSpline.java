package advancedGeometry;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BSpline algorithms
 * inspired by
 * https://blog.csdn.net/qq_39300235/article/details/107204239
 * https://www.cnblogs.com/icmzn/p/5100789.html
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/8/20
 * @time 13:40
 */
public class ZBSpline {
    public static final int OPEN = 0;
    public static final int CLAMPED = 1;
    public static final int CLOSE = 2;

    // control points
    private final List<WB_Point> allCtrlPts;
    // knot vectors(uk)
    private List<Double> knots;

    // p: degree of the BSpline
    private final int p;
    // n+1: number of control points
    private int n;
    // m+1: number of knots
    private int m;

    // final curve points after subdivision
    private List<ZPoint> curvePts;
    private double length;

    /* ------------- constructor ------------- */

    public ZBSpline(List<WB_Point> ctrlPts, int degrees, int numU, int type) {
        this.p = degrees;
        this.allCtrlPts = new ArrayList<>();

        if (type == 0) {
            initOpen(ctrlPts, numU);
        } else if (type == 1) {
            initClamped(ctrlPts, numU);
        } else if (type == 2) {
            initClose(ctrlPts, numU);
        }
    }

    public ZBSpline(WB_Point[] ctrlPts, int degrees, int numU, int type) {
        this.p = degrees - 1;
        this.allCtrlPts = new ArrayList<>();

        List<WB_Point> ctrlPtsList = Arrays.asList(ctrlPts);
        if (type == 0) {
            initOpen(ctrlPtsList, numU);
        } else if (type == 1) {
            initClamped(ctrlPtsList, numU);
        } else if (type == 2) {
            initClose(ctrlPtsList, numU);
        }
    }

    /* ------------- member function ------------- */

    /**
     * initialize an open BSpline
     *
     * @param ctrlPts input control points
     * @param numU    number of subdivision
     * @return void
     */
    private void initOpen(List<WB_Point> ctrlPts, int numU) {
        // type: open
        allCtrlPts.addAll(ctrlPts);
        this.n = allCtrlPts.size() - 1;
        this.m = n + p + 1;

        // 构建节点向量
        this.knots = new ArrayList<>();
        double knotStep = 1d / m;
        for (int i = 0; i < m + 1; i++) {
            knots.add(i * knotStep);
        }

        // 根据细分数计算曲线点
        this.curvePts = new ArrayList<>();
        double uStep = (knots.get(m - p) - knots.get(p)) / (numU - 1);
        for (int i = 0; i < numU; i++) {
            double u = knots.get(p) + i * uStep;
            double[] pmiu = getPmiu(u, false);
            curvePts.add(new ZPoint(pmiu[0], pmiu[1]));
        }
    }

    /**
     * initialize a clamped BSpline
     *
     * @param ctrlPts input control points
     * @param numU    number of subdivision
     * @return void
     */
    private void initClamped(List<WB_Point> ctrlPts, int numU) {
        // type: clamped
        allCtrlPts.addAll(ctrlPts);
        this.n = allCtrlPts.size() - 1;
        this.m = n + p + 1;

        // 构建节点向量
        this.knots = new ArrayList<>();
        double knotStep = 1d / m;
        for (int i = 0; i < m + 1; i++) {
            knots.add(i * knotStep);
        }
        for (int i = 0; i < p + 1; i++) {
            knots.set(i, 0d);
            knots.set(m - p + i, 1d);
        }

        // 根据细分数计算曲线点
        this.curvePts = new ArrayList<>();
        double uStep = (knots.get(m - p) - knots.get(p)) / (numU - 1);
        for (int i = 0; i < numU; i++) {
            double u = knots.get(p) + i * uStep;
            double[] pmiu = getPmiu(u, true);
            curvePts.add(new ZPoint(pmiu[0], pmiu[1]));
        }
    }

    /**
     * initialize a close BSpline
     *
     * @param ctrlPts input control points
     * @param numU    number of subdivision
     * @return void
     */
    private void initClose(List<WB_Point> ctrlPts, int numU) {
        // type: close
        if (ctrlPts.get(0).equals(ctrlPts.get(ctrlPts.size() - 1))) {
            // coincidence
            allCtrlPts.addAll(ctrlPts);
            for (int i = 1; i < p; i++) {
                allCtrlPts.add(ctrlPts.get(i));
            }
        } else {
            // non-coincidence
            allCtrlPts.addAll(ctrlPts);
            for (int i = 0; i < p; i++) {
                allCtrlPts.add(ctrlPts.get(i));
            }
        }
        this.n = allCtrlPts.size() - 1;
        this.m = n + p + 1;

        // 构建节点向量
        this.knots = new ArrayList<>();
        double knotStep = 1d / m;
        for (int i = 0; i < m + 1; i++) {
            knots.add(i * knotStep);
        }

        // 根据细分数计算曲线点
        this.curvePts = new ArrayList<>();
        double uStep = (knots.get(m - p) - knots.get(p)) / (numU - 1);
        for (int i = 0; i < numU; i++) {
            double u = knots.get(p) + i * uStep;
            double[] pmiu = getPmiu(u, false);
            curvePts.add(new ZPoint(pmiu[0], pmiu[1]));
        }

        curvePts.remove(curvePts.get(curvePts.size() - 1));
        curvePts.add(curvePts.get(0));
    }

    /**
     * description
     *
     * @param u       input u
     * @param clamped clamped or not
     * @return double[]
     */
    private double[] getPmiu(double u, boolean clamped) {
        double px = 0;
        double py = 0;
        for (int i = 0; i < n + 1; i++) {
            double cox = cox(u, i, p, clamped);
            px += allCtrlPts.get(i).xd() * cox;
            py += allCtrlPts.get(i).yd() * cox;
        }
        return new double[]{px, py};
    }

    /**
     * Cox-de Boor
     *
     * @param u
     * @param i
     * @param p
     * @param clamped clamped or not
     * @return double
     */
    private double cox(double u, int i, int p, boolean clamped) {
        if (clamped) {
            if (p == 0) {
                if (u >= knots.get(i) && u <= knots.get(i + 1)) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                double a, b;
                double dev = knots.get(i + p) - knots.get(i);
                a = dev == 0 ? 0 : (u - knots.get(i)) / dev;
                dev = knots.get(i + p + 1) - knots.get(i + 1);
                b = dev == 0 ? 0 : (knots.get(i + p + 1) - u) / dev;
                return a * cox(u, i, p - 1, true) + b * cox(u, i + 1, p - 1, true);
            }
        } else {
            if (p == 0) {
                if (u >= knots.get(i) && u < knots.get(i + 1)) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                double a, b;
                double dev = knots.get(i + p) - knots.get(i);
                a = dev == 0 ? 0 : (u - knots.get(i)) / dev;
                dev = knots.get(i + p + 1) - knots.get(i + 1);
                b = dev == 0 ? 0 : (knots.get(i + p + 1) - u) / dev;
                return a * cox(u, i, p - 1, false) + b * cox(u, i + 1, p - 1, false);
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public List<ZPoint> getCurvePts() {
        return curvePts;
    }

    public List<WB_Point> getAllCtrlPts() {
        return allCtrlPts;
    }

    public List<Double> getKnots() {
        return knots;
    }

    public double getLength() {
        for (int i = 0; i < curvePts.size() - 1; i++) {
            length += curvePts.get(i).distance(curvePts.get(i + 1));
        }
        return length;
    }

    public WB_PolyLine getAsWB_PolyLine() {
        WB_Point[] pts = new WB_Point[curvePts.size()];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = curvePts.get(i).toWB_Point();
        }
        return new WB_PolyLine(pts);
    }

    public WB_Polygon getAsWB_Polygon(){
        WB_Point[] pts = new WB_Point[curvePts.size()];
        for (int i = 0; i < pts.length; i++) {
            pts[i] = curvePts.get(i).toWB_Point();
        }
        return new WB_Polygon(pts);
    }

    public LineString getAsLineString(){
        Coordinate[] coordinates = new Coordinate[curvePts.size()];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = curvePts.get(i).toJtsCoordinate();
        }
        return ZFactory.jtsgf.createLineString(coordinates);
    }

    public Polygon getAsPolygon(){
        Coordinate[] coordinates = new Coordinate[curvePts.size()];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = curvePts.get(i).toJtsCoordinate();
        }
        return ZFactory.jtsgf.createPolygon(coordinates);
    }
}
