package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.triangulate.Segment;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的线数据类型，可代表直线、射线、线段，也可转化为 p+td 的形式
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 16:01
 */
public class ZLine {
    private ZPoint pt0;
    private ZPoint pt1;
    private ZPoint direction;

    private double k; // 斜率 y = kx + b
    private double b;

    private static final GeometryFactory gf = new GeometryFactory();

    /* ------------- constructor ------------- */

    public ZLine() {

    }

    public ZLine(ZPoint pt0, ZPoint pt1) {
        this.pt0 = pt0;
        this.pt1 = pt1;
        init();
    }

    public ZLine(WB_Point pt0, WB_Point pt1) {
        this.pt0 = new ZPoint(pt0);
        this.pt1 = new ZPoint(pt1);
        init();
    }

    public ZLine(WB_Segment segment) {
        this.pt0 = new ZPoint(segment.getOrigin());
        this.pt1 = new ZPoint(segment.getEndpoint());
        init();
    }

    public ZLine(double x0, double y0, double x1, double y1) {
        this.pt0 = new ZPoint(x0, y0);
        this.pt1 = new ZPoint(x1, y1);
        init();
    }

    /**
     * description
     *
     * @return void
     */
    private void init() {
        this.direction = pt1.sub(pt0);
        this.k = (pt1.yd() - pt0.yd()) / (pt1.xd() - pt0.xd());
        this.b = pt0.yd() - k * pt0.xd();
    }

    /* ------------- member function ------------- */

    /**
     * 两头略微出头
     *
     * @param dist extend distance
     * @return geometry.ZLine
     */
    public ZLine extendTwoSidesSlightly(double dist) {
        ZPoint newPt0 = pt0.add(getDirectionUnit().scaleTo(-dist));
        ZPoint newPt1 = pt1.add(getDirectionUnit().scaleTo(dist));
        return new ZLine(newPt0, newPt1);
    }

    /**
     * 尾部略微出头
     *
     * @param dist extend distance
     * @return geometry.ZLine
     */
    public ZLine extendSlightly(double dist) {
        ZPoint newPt1 = pt1.add(getDirectionUnit().scaleTo(dist));
        return new ZLine(pt0, newPt1);
    }

    /**
     * 以pt0为基准点缩放ZLine
     *
     * @param scale scale ratio
     * @return geometry.ZLine
     */
    public ZLine scaleTo(double scale) {
        ZPoint newDir = direction.scaleTo(scale);
        return new ZLine(pt0, pt0.add(newDir));
    }

    /**
     * 两头缩放ZLine，即以中点为基准
     *
     * @param scale scale ratio
     * @return geometry.ZLine
     */
    @Deprecated
    public ZLine scaleBothSides(double scale) {
        double scaleDist = getLength() * (scale - 1) * 0.5;
        pt1.add(direction.scaleTo(scaleDist));
        pt0.add(direction.scaleTo(scaleDist * -1));
        return new ZLine(
                pt0.add(direction.scaleTo(scaleDist * -1)),
                pt1.add(direction.scaleTo(scaleDist))
        );
    }

    /**
     * 使ZLine反向，即pt0和pt1互换
     *
     * @return geometry.ZLine
     */
    public ZLine reverse() {
        return new ZLine(this.pt1, this.pt0);
    }

    public List<ZPoint> splitByStep(final double step) {
        List<ZPoint> result = new ArrayList<>();

        ZPoint start = this.pt0;
        result.add(start);

        if (this.getLength() > step) {
            double currDist = this.getLength();

            while (currDist > step) {
                start = start.add(this.getDirectionUnit().scaleTo(step));
                result.add(start);
                currDist -= step;
            }
        }

        result.add(this.pt1);
        return result;
    }

    /* ------------- setter & getter ------------- */

    public void set(ZPoint pt0, ZPoint pt1) {
        this.pt0 = pt0;
        this.pt1 = pt1;
        init();
    }

    public ZPoint[] getPoints() {
        return new ZPoint[]{pt0, pt1};
    }

    public ZPoint getPt0() {
        return pt0;
    }

    public ZPoint getPt1() {
        return pt1;
    }

    public ZPoint getCenter() {
        return pt0.centerWith(pt1);
    }

    public ZPoint getDirection() {
        return direction;
    }

    public ZPoint getDirectionUnit() {
        return direction.unit();
    }

    public double getK() {
        return k;
    }

    public double getB() {
        return b;
    }

    public double getLength() {
        return pt0.distance(pt1);
    }

    public double minX() {
        return Math.min(pt0.xd(), pt1.xd());
    }

    public double maxX() {
        return Math.max(pt0.xd(), pt1.xd());
    }

    public double minY() {
        return Math.min(pt0.yd(), pt1.yd());
    }

    public double maxY() {
        return Math.max(pt0.yd(), pt1.yd());
    }

    /* ------------- transformation -------------*/

    @Override
    public String toString() {
        return "ZLine : [pt0:" + pt0.toString() + " pt1:" + pt1.toString() + "]";
    }

    /**
     * 转换为p+td形式
     *
     * @return geometry.ZPoint[]
     */
    public ZPoint[] toLinePD() {
        ZPoint[] line = new ZPoint[2];
        line[0] = pt0;
        line[1] = direction;
        return line;
    }

    /**
     * 转换为p+td形式（单位向量）
     *
     * @return geometry.ZPoint[]
     */
    public ZPoint[] toUnitLinePD() {
        ZPoint[] line = new ZPoint[2];
        line[0] = pt0;
        line[1] = direction.unit();
        return line;
    }

    public LineString toJtsLineString() {
        return gf.createLineString(new Coordinate[]{pt0.toJtsCoordinate(), pt1.toJtsCoordinate()});
    }

    public Segment toJtsSegment() {
        return new Segment(pt0.toJtsCoordinate(), pt1.toJtsCoordinate());
    }

    public WB_Segment toWB_Segment() {
        return new WB_Segment(new WB_Point(pt0.toWB_Point()), pt1.toWB_Point());
    }

    public WB_Ray toWB_Ray() {
        return new WB_Ray(pt0.toWB_Point(), direction.toWB_Point());
    }

    /* ------------- draw -------------*/

    public void display() {

    }

    public void display(PApplet app) {
        app.line(pt0.xf(), pt0.yf(), pt0.zf(), pt1.xf(), pt1.yf(), pt1.zf());
    }

    public void display(PApplet app, float strokeWeight) {
        app.strokeWeight(strokeWeight);
        display(app);
    }
}
