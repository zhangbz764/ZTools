package basicGeometry;

import igeo.ICurve;
import igeo.IVec;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.triangulate.Segment;
import processing.core.PApplet;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * custom line class
 * can represent a line, a ray, or a segment
 * also can be converted to the form of 'p+td'
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
    private double length;

    private double k; // y = kx + b
    private double b;

    /* ------------- constructor ------------- */

    public ZLine() {

    }

    public ZLine(ZPoint pt0, ZPoint pt1) {
        this.pt0 = pt0;
        this.pt1 = pt1;
        init();
    }

    public ZLine(double x0, double y0, double x1, double y1) {
        this(new ZPoint(x0, y0), new ZPoint(x1, y1));
    }

    public ZLine(WB_Coord pt0, WB_Coord pt1) {
        this(new ZPoint(pt0), new ZPoint(pt1));
    }

    public ZLine(WB_Segment segment) {
        this(new ZPoint(segment.getOrigin()), new ZPoint(segment.getEndpoint()));
    }

    public ZLine(Coordinate c0, Coordinate c1) {
        this(new ZPoint(c0), new ZPoint(c1));
    }

    public ZLine(Point pt0, Point pt1) {
        this(new ZPoint(pt0), new ZPoint(pt1));
    }

    public ZLine(Segment segment) {
        this(new ZPoint(segment.getStart()), new ZPoint(segment.getEnd()));
    }

    /**
     * initialize properties
     */
    private void init() {
        this.direction = pt1.sub(pt0);
        this.k = (pt1.yd() - pt0.yd()) / (pt1.xd() - pt0.xd());
        this.b = pt0.yd() - k * pt0.xd();
        this.length = pt0.distance(pt1);
    }

    /* ------------- member function ------------- */

    /**
     * extend ZLine slightly both sides
     *
     * @param dist extend distance
     * @return geometry.ZLine
     */
    public ZLine extendBothSides(double dist) {
        ZPoint newPt0 = pt0.add(getDirectionNor().scaleTo(-dist));
        ZPoint newPt1 = pt1.add(getDirectionNor().scaleTo(dist));
        return new ZLine(newPt0, newPt1);
    }

    /**
     * extend the end of a ZLine
     *
     * @param dist extend distance
     * @return geometry.ZLine
     */
    public ZLine extendSlightly(double dist) {
        ZPoint newPt1 = pt1.add(getDirectionNor().scaleTo(dist));
        return new ZLine(pt0, newPt1);
    }

    /**
     * absolute cosine value of this line and the other line
     *
     * @param other the other ZLine
     * @return double
     */
    public double angleCosWith(ZLine other) {
        double dot = this.direction.dot2D(other.direction);
        return Math.abs(dot / (this.length * other.length));
    }

    /**
     * scale the length of ZLine based on pt0
     *
     * @param scale scale ratio
     * @return geometry.ZLine
     */
    public ZLine scaleTo(double scale) {
        ZPoint newDir = direction.scaleTo(scale);
        return new ZLine(pt0, pt0.add(newDir));
    }

    /**
     * reverse the direction of ZLine
     *
     * @return geometry.ZLine
     */
    public ZLine reverse() {
        return new ZLine(this.pt1, this.pt0);
    }

    /**
     * translate the ZLine by given vector
     *
     * @param vec vector to translate
     * @return basicGeometry.ZLine
     */
    public ZLine translate2D(ZPoint vec) {
        return new ZLine(pt0.add(vec), pt1.add(vec));
    }

    /**
     * get two offset lines
     *
     * @param dist offset distance
     * @return basicGeometry.ZLine[]
     */
    public ZLine[] offset2D(double dist) {
        ZPoint perpendicular = this.direction.normalize().perpVec();
        return new ZLine[]{
                new ZLine(
                        pt0.add(perpendicular.scaleTo(dist)),
                        pt1.add(perpendicular.scaleTo(dist))
                ),
                new ZLine(
                        pt0.add(perpendicular.scaleTo(-1 * dist)),
                        pt1.add(perpendicular.scaleTo(-1 * dist))
                )
        };
    }

    /**
     * divide ZLine by giving step length
     *
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public List<ZPoint> divideByStep(final double step) {
        List<ZPoint> result = new ArrayList<>();

        ZPoint start = this.pt0;
        result.add(start);

        if (this.getLength() > step) {
            double currDist = length;

            while (currDist > step) {
                start = start.add(this.getDirectionNor().scaleTo(step));
                result.add(start);
                currDist -= step;
            }
        }

        result.add(this.pt1);
        return result;
    }

    /**
     * equally divide ZLine by giving the number of segments
     *
     * @param num number to division
     * @return java.util.List<geometry.ZPoint>
     */
    public List<ZPoint> divide(final int num) {
        List<ZPoint> result = new ArrayList<>();

        ZPoint start = this.pt0;
        result.add(start);

        double step = length / num;

        for (int i = 0; i < num; i++) {
            start = start.add(this.getDirectionNor().scaleTo(step));
            result.add(start);
        }

        return result;
    }

    /**
     * divide ZLine by giving the max and min step
     *
     * @param min min step
     * @param max max step
     * @return java.util.List<geometry.ZPoint>
     */
    public List<ZPoint> divideByThreshold(double min, double max) {
        int num = 1;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double currStep = length / i;
            if (currStep >= min && currStep <= max) {
                num = i;
                break;
            } else if (currStep < min) {
                break;
            }
        }

        return divide(num);
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

    public ZPoint getDirectionNor() {
        return direction.normalize();
    }

    public double getK() {
        return k;
    }

    public double getB() {
        return b;
    }

    public double getLength() {
        return length;
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
     * convert to p+td
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
     * convert to p+td (normalized)
     *
     * @return geometry.ZPoint[]
     */
    public ZPoint[] toLinePDNor() {
        ZPoint[] line = new ZPoint[2];
        line[0] = pt0;
        line[1] = direction.normalize();
        return line;
    }

    public LineString toJtsLineString() {
        return ZFactory.jtsgf.createLineString(new Coordinate[]{pt0.toJtsCoordinate(), pt1.toJtsCoordinate()});
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

    public WB_Line toWB_Line() {
        return new WB_Line(pt0.toWB_Point(), direction.toWB_Point());
    }

    public ICurve createICurve() {
        IVec[] vecs = new IVec[]{
                pt0.toIVec(),
                pt1.toIVec()
        };
        return new ICurve(vecs);
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
