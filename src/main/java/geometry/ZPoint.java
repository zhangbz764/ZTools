package geometry;

import igeo.IPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
 * @description to describe a coordinate, point, vector
 */
public class ZPoint {
    private double x, y, z;
    private final float r = 15;
    private final float vecCap = 3;

    private static final GeometryFactory gf = new GeometryFactory();

    /* ------------- constructor ------------- */

    public ZPoint() {

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
        this.x = c.x;
        this.y = c.y;
        this.z = c.z;
    }

    public ZPoint(WB_Coord c) {
        this.x = c.xd();
        this.y = c.yd();
        this.z = c.zd();
    }

    public ZPoint(IPoint p) {
        this.x = p.x();
        this.y = p.y();
        this.z = p.z();
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

    public void set(WB_Coord c) {
        this.x = c.xd();
        this.y = c.yd();
        this.z = c.zd();
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public double r() {
        return this.r;
    }

    /* ------------- transformation -------------*/

    public WB_Point toWB_Point() {
        return new WB_Point(x, y, z);
    }

    public Coordinate toJtsCoordinate() {
        return new Coordinate(x, y, z);
    }

    public Point toJtsPoint() {
        return gf.createPoint(this.toJtsCoordinate());
    }

    public IPoint toIPoint() {
        return new IPoint(x, y, z);
    }

    public String toString() {
        return "ZPoint: [x=" + x + " y=" + y + " z=" + z + "]";
    }

    /*--------basic vector math--------*/

    /**
     * @return generalTools.ZVec
     * @description vector add
     */
    public ZPoint add(ZPoint v) {
        return new ZPoint(x + v.x(), y + v.y(), z + v.z());
    }

    /**
     * @return void
     * @description vector add (change self)
     */
    public void addSelf(ZPoint v) {
        this.set(x + v.x(), y + v.y(), z + v.z());
    }

    /**
     * @return generalTools.ZVec
     * @description vector subtract (self - input)
     */
    public ZPoint sub(ZPoint v) {
        return new ZPoint(x - v.x(), y - v.y(), z - v.z());
    }

    /**
     * @return double
     * @description get vector's length
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * @return void
     * @description scale vector by ratio (change self)
     */
    public void scaleSelf(double t) {
        this.set(x * t, y * t, z * t);
    }

    /**
     * @return generalTools.ZVec
     * @description get a new vector scaled by ratio
     */
    public ZPoint scaleTo(double t) {
        return new ZPoint(x * t, y * t, z * t);
    }

    /**
     * @return generalTools.ZVec
     * @description get normalized vector (length = 1)
     */
    public ZPoint unit() {
        double length = this.getLength();
        return scaleTo(1.0 / length);
    }

    /**
     * @return generalTools.ZPoint
     * @description rotate vector to an angle
     */
    public ZPoint rotate2D(double angle) {
        return new ZPoint(x * Math.cos(angle) - y * Math.sin(angle), x * Math.sin(angle) + y * Math.cos(angle));
    }

    /**
     * @return generalTools.ZPoint
     * @description get center point with the other ZPoint
     */
    public ZPoint centerWith(ZPoint other) {
        return new ZPoint((x + other.x()) / 2, (y + other.y()) / 2, (z + other.z()) / 2);
    }

    /**
     * @return double
     * @description vector dot
     */
    public double dot2D(ZPoint v) {
        return x * v.x() + y * v.y();
    }

    /**
     * @return double
     * @description vector cross (this X other)
     */
    public double cross2D(ZPoint v) {
        return x * v.y() - y * v.x();
    }

    /**
     * @return boolean
     * @description check if vector is united
     */
    public boolean isUnit() {
        return Math.abs(this.getLength() - 1) < 0.00000001;
    }

    /**
     * @return boolean
     * @description check if two vectors are on same line
     */
    public boolean isCollineation(ZPoint v) {
        return cross2D(v) == 0;
    }

    /* ------------- geometry method -------------*/

    /**
     * @return float
     * @description distance square to other node
     */
    public double distanceSq(ZPoint other) {
        return ((x - other.x()) * (x - other.x()) + (y - other.y()) * (y - other.y()) + (z - other.z()) * (z - other.z()));
    }

    /**
     * @return float
     * @description distance to other node
     */
    public double distance(ZPoint other) {
        return Math.sqrt(distanceSq(other));
    }

    /* ------------- draw -------------*/

    /**
     * @return void
     * @description draw ZPoint as a point
     */
    public void displayAsPoint(PApplet app) {
        app.ellipse((float) x, (float) y, r, r);
    }

    /**
     * @return void
     * @description draw ZPoint as a point
     */
    public void displayAsPoint(PApplet app, float r) {
        app.ellipse((float) x, (float) y, r, r);
    }

    /**
     * @return void
     * @description draw vector from base point
     */
    public void displayAsVector(PApplet app, ZPoint base) {
        ZPoint dest = base.add(this);
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.line((float) base.x(), (float) base.y(), (float) base.z(), (float) dest.x(), (float) dest.y(), (float) dest.z());
        app.ellipse((float) dest.x(), (float) dest.y(), vecCap, vecCap);
        app.popStyle();
    }

    /**
     * @return void
     * @description draw vector from base point by scale
     */
    public void displayAsVector(PApplet app, ZPoint base, double scale) {
        ZPoint dest = base.add(this.scaleTo(scale));
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.line((float) base.x(), (float) base.y(), (float) base.z(), (float) dest.x(), (float) dest.y(), (float) dest.z());
        app.ellipse((float) dest.x(), (float) dest.y(), vecCap, vecCap);
        app.popStyle();
    }
}
