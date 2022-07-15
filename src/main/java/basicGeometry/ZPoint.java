package basicGeometry;

import igeo.IPoint;
import igeo.IVec;
import igeo.IVecI;
import math.ZGeoMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

import java.util.List;

/**
 * custom point class
 * can represent a point or a vector
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
 */
public class ZPoint {
    private double x, y, z = 0;
    private final float r = 5;

    /* ------------- constructor ------------- */

    public ZPoint() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public ZPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ZPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ZPoint(Coordinate c) {
        this(c.getX(), c.getY());
        if (Double.isNaN(c.getZ())) {
            this.z = 0;
        } else {
            this.z = c.getZ();
        }
    }

    public ZPoint(Point c) {
        this(c.getCoordinate());
    }

    public ZPoint(WB_Coord c) {
        this(c.xd(), c.yd(), c.zd());
    }

    public ZPoint(IPoint p) {
        this(p.x(), p.y(), p.z());
    }

    public ZPoint(IVecI p) {
        this(p.x(), p.y(), p.z());
    }

    /* ------------- set & get ------------- */

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void set(WB_Coord c) {
        this.x = c.xd();
        this.y = c.yd();
        this.z = c.zd();
    }

    public void set(ZPoint point) {
        this.x = point.xd();
        this.y = point.yd();
        this.z = point.zd();
    }

    public double xd() {
        return this.x;
    }

    public double yd() {
        return this.y;
    }

    public double zd() {
        return this.z;
    }

    public float xf() {
        return (float) this.x;
    }

    public float yf() {
        return (float) this.y;
    }

    public float zf() {
        return (float) this.z;
    }

    public double rd() {
        return this.r;
    }

    public float rf() {
        return this.r;
    }

    /* ------------- transformation -------------*/

    public WB_Point toWB_Point() {
        return new WB_Point(x, y, z);
    }

    public WB_Vector toWB_Vector() {
        return new WB_Vector(x, y, z);
    }

    public Coordinate toJtsCoordinate() {
        return new Coordinate(x, y, z);
    }

    public Point toJtsPoint() {
        return ZFactory.jtsgf.createPoint(this.toJtsCoordinate());
    }

    public IPoint toIPoint() {
        return new IPoint(x, y, z);
    }

    public IVec toIVec() {
        return new IVec(x, y, z);
    }

    @Override
    public String toString() {
        return "ZPoint: [x=" + x + " y=" + y + " z=" + z + "]";
    }

    /*--------basic vector math--------*/

    /**
     * add
     *
     * @param v vector to add
     * @return geometry.ZPoint
     */
    public ZPoint add(ZPoint v) {
        return new ZPoint(x + v.xd(), y + v.yd(), z + v.zd());
    }

    /**
     * add 2D
     *
     * @param x x value to add
     * @param y y value to add
     * @return basicGeometry.ZPoint
     */
    public ZPoint add(double x, double y) {
        return new ZPoint(this.x + x, this.y + y);
    }

    /**
     * add 3D
     *
     * @param x x value to add
     * @param y y value to add
     * @param z z value to add
     * @return geometry.ZPoint
     */
    public ZPoint add(double x, double y, double z) {
        return new ZPoint(this.x + x, this.y + y, this.z + z);
    }

    /**
     * add self
     *
     * @param v vector to add
     */
    public void addSelf(ZPoint v) {
        this.set(x + v.xd(), y + v.yd(), z + v.zd());
    }

    /**
     * subtract
     *
     * @param v vector to subtract
     * @return geometry.ZPoint
     */
    public ZPoint sub(ZPoint v) {
        return new ZPoint(x - v.xd(), y - v.yd(), z - v.zd());
    }

    /**
     * subtract
     *
     * @param x x value to subtract
     * @param y y value to subtract
     * @param z z value to subtract
     * @return geometry.ZPoint
     */
    public ZPoint sub(double x, double y, double z) {
        return new ZPoint(this.x - x, this.y - y, this.z - z);
    }

    /**
     * length of a vector
     *
     * @return double
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * scale the vector self
     *
     * @param t ratio to scale
     */
    public void scaleSelf(double t) {
        this.set(x * t, y * t, z * t);
    }

    /**
     * scale the vector
     *
     * @param t ratio to scale
     * @return geometry.ZPoint
     */
    public ZPoint scaleTo(double t) {
        return new ZPoint(x * t, y * t, z * t);
    }

    /**
     * get the normalized vector
     *
     * @return geometry.ZPoint
     */
    public ZPoint normalize() {
        double length = this.getLength();
        return scaleTo(1.0 / length);
    }

    /**
     * normalize vector itself
     */
    public void normalizeSelf() {
        double length = this.getLength();
        this.scaleSelf(1.0 / length);
    }

    /**
     * angle with the other vector (from this to other, return degrees, -180 ~ 180)
     *
     * @param other the other vector
     * @return double
     */
    public double angleWith(ZPoint other) {
        double l1 = this.getLength();
        double l2 = other.getLength();
        double nor = l1 * l2;
        double cross = this.cross2D(other);
        double dot = this.dot2D(other);
        double rho = Math.asin(cross / nor);
        double theta = Math.acos(dot / nor);

        if (Double.isNaN(theta)) {
            // floating point error
            return 0;
        } else {
            if (rho < 0) {
                return -360 * (theta / (2 * Math.PI));
            } else {
                return 360 * (theta / (2 * Math.PI));
            }
        }
    }

    /**
     * rotate the vector (2D)
     *
     * @param angle angle to rotate (radian system)
     * @return geometry.ZPoint
     */
    public ZPoint rotate2D(double angle) {
        return new ZPoint(x * Math.cos(angle) - y * Math.sin(angle), x * Math.sin(angle) + y * Math.cos(angle));
    }

    /**
     * get the perpendicular vector (not specified)
     *
     * @return basicGeometry.ZPoint
     */
    public ZPoint perpVec() {
        return new ZPoint(-y, x);
    }

    /**
     * calculate the center point with the other ZPoint
     *
     * @param other other point
     * @return geometry.ZPoint
     */
    public ZPoint centerWith(ZPoint other) {
        return new ZPoint((x + other.xd()) * 0.5, (y + other.yd()) * 0.5, (z + other.zd()) * 0.5);
    }

    /**
     * get the mirror point with a center point
     *
     * @param center center point
     * @return basicGeometry.ZPoint
     */
    public ZPoint mirrorPoint(ZPoint center) {
        return new ZPoint(center.xd() * 2 - x, center.yd() * 2 - y);
    }

    /**
     * get the mirror point with a line
     *
     * @param l ZLine
     * @return basicGeometry.ZPoint
     */
    public ZPoint mirrorPoint(ZLine l) {
        double x1 = l.getPt0().xd();
        double x2 = l.getPt1().xd();
        double y1 = l.getPt0().yd();
        double y2 = l.getPt1().yd();
        if (x1 - x2 == 0) {
            return this.mirrorPoint(new ZPoint(x1, y));
        } else if (y1 - y2 == 0) {
            return this.mirrorPoint(new ZPoint(x, y1));
        } else {
            double p = (x1 - x2) / (y1 - y2);
            double m = (p * p * x + x2 - p * y2) / (p * p + 1);
            double n = (m - x2) / p + y2;
            return this.mirrorPoint(new ZPoint(m, n));
        }
    }

    /**
     * dot product (2D)
     *
     * @param v vector to dot product
     * @return double
     */
    public double dot2D(ZPoint v) {
        return x * v.xd() + y * v.yd();
    }

    /**
     * cross product (2D)
     *
     * @param v vector to cross product
     * @return double
     */
    public double cross2D(ZPoint v) {
        return x * v.yd() - y * v.xd();
    }

    /**
     * judge if the vector is normalized
     *
     * @return boolean
     */
    public boolean isNormalized() {
        return Math.abs(this.getLength() - 1) < 0.00000001;
    }

    /**
     * judge if the vector is collinear with another
     *
     * @param v vector to judge
     * @return boolean
     */
    public boolean isCollinear(ZPoint v) {
        return cross2D(v) == 0;
    }

    /**
     * judge if the point is too close to another
     *
     * @param other the other ZPoint
     * @param dist  distance to check
     * @return boolean
     */
    @Deprecated
    public boolean isTooClose(List<ZPoint> other, double dist) {
        for (ZPoint p : other) {
            if (this.distance(p) < dist) {
                return true;
            }
        }
        return false;
    }

    /* ------------- geometry method -------------*/

    /**
     * get the square of distance with the other point
     *
     * @param other the other point
     * @return double
     */
    public double distanceSq(ZPoint other) {
        return ((x - other.xd()) * (x - other.xd()) + (y - other.yd()) * (y - other.yd()) + (z - other.zd()) * (z - other.zd()));
    }

    /**
     * get the distance with the other point
     *
     * @param other the other point
     * @return double
     */
    public double distance(ZPoint other) {
        return Math.sqrt(distanceSq(other));
    }

    /* ------------- draw -------------*/
}
