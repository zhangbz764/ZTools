package geometry;

import igeo.IPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;

import java.util.List;

/**
 * 自定义的点数据类型，可代表点或向量
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/11
 * @time 17:55
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

    @Override
    public String toString() {
        return "ZPoint: [x=" + x + " y=" + y + " z=" + z + "]";
    }

    /*--------basic vector math--------*/

    /**
     * 向量相加
     *
     * @param v
     * @return geometry.ZPoint
     */
    public ZPoint add(ZPoint v) {
        return new ZPoint(x + v.x(), y + v.y(), z + v.z());
    }

    /**
     * 向量相加（改变自身）
     *
     * @param v
     * @return void
     */
    public void addSelf(ZPoint v) {
        this.set(x + v.x(), y + v.y(), z + v.z());
    }

    /**
     * 向量相减
     *
     * @param v
     * @return geometry.ZPoint
     */
    public ZPoint sub(ZPoint v) {
        return new ZPoint(x - v.x(), y - v.y(), z - v.z());
    }

    /**
     * 向量模长
     *
     * @param
     * @return double
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * 向量缩放（改变自身）
     *
     * @param t
     * @return void
     */
    public void scaleSelf(double t) {
        this.set(x * t, y * t, z * t);
    }

    /**
     * 向量缩放
     *
     * @param t
     * @return geometry.ZPoint
     */
    public ZPoint scaleTo(double t) {
        return new ZPoint(x * t, y * t, z * t);
    }

    /**
     * 得到单位向量
     *
     * @param
     * @return geometry.ZPoint
     */
    public ZPoint unit() {
        double length = this.getLength();
        return scaleTo(1.0 / length);
    }

    /**
     * 向量旋转（2D）
     *
     * @param angle
     * @return geometry.ZPoint
     */
    public ZPoint rotate2D(double angle) {
        return new ZPoint(x * Math.cos(angle) - y * Math.sin(angle), x * Math.sin(angle) + y * Math.cos(angle));
    }

    /**
     * 计算与另一点的中点
     *
     * @param other other point
     * @return geometry.ZPoint
     */
    public ZPoint centerWith(ZPoint other) {
        return new ZPoint((x + other.x()) / 2, (y + other.y()) / 2, (z + other.z()) / 2);
    }

    /**
     * 向量点积（2D）
     *
     * @param v
     * @return double
     */
    public double dot2D(ZPoint v) {
        return x * v.x() + y * v.y();
    }

    /**
     * 向量叉积（2D）
     *
     * @param v
     * @return double
     */
    public double cross2D(ZPoint v) {
        return x * v.y() - y * v.x();
    }

    /**
     * 判断向量是否是单位向量
     *
     * @param
     * @return boolean
     */
    public boolean isUnit() {
        return Math.abs(this.getLength() - 1) < 0.00000001;
    }

    /**
     * 判断向量是否共线
     *
     * @param v
     * @return boolean
     */
    public boolean isColinear(ZPoint v) {
        return cross2D(v) == 0;
    }

    /**
     * 判断是否与另一个点太近
     *
     * @param other
     * @param dist
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
     * 计算两点距离平方
     *
     * @param other
     * @return double
     */
    public double distanceSq(ZPoint other) {
        return ((x - other.x()) * (x - other.x()) + (y - other.y()) * (y - other.y()) + (z - other.z()) * (z - other.z()));
    }

    /**
     * 计算两点距离
     *
     * @param other
     * @return double
     */
    public double distance(ZPoint other) {
        return Math.sqrt(distanceSq(other));
    }

    /* ------------- draw -------------*/

    /**
     * 将点绘制为圆（默认半径）
     *
     * @param app
     * @return void
     */
    public void displayAsPoint(PApplet app) {
        app.ellipse((float) x, (float) y, r, r);
    }

    /**
     * 将点绘制为圆（输入半径）
     *
     * @param app
     * @param r
     * @return void
     */
    public void displayAsPoint(PApplet app, float r) {
        app.ellipse((float) x, (float) y, r, r);
    }

    /**
     * 绘制为向量（设置基点）
     *
     * @param app
     * @param base base point of a vector
     * @return void
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
     * 绘制为向量（设置基点和缩放比例）
     *
     * @param app
     * @param base  base point of a vector
     * @param scale scale
     * @return void
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
