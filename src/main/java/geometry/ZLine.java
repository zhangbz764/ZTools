package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.triangulate.Segment;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/23
 * @time 16:01
 * @description
 */
public class ZLine {
    private ZPoint pt0;
    private ZPoint pt1;
    private ZPoint direction;

    private static final GeometryFactory gf = new GeometryFactory();

    /* ------------- constructor ------------- */

    public ZLine() {

    }

    public ZLine(ZPoint pt0, ZPoint pt1) {
        this.pt0 = pt0;
        this.pt1 = pt1;
        this.direction = pt1.sub(pt0);
    }

    public ZLine(WB_Point pt0, WB_Point pt1) {
        this.pt0 = new ZPoint(pt0);
        this.pt1 = new ZPoint(pt1);
        this.direction = this.pt1.sub(this.pt0);
    }

    public ZLine(WB_Segment segment) {
        this.pt0 = new ZPoint(segment.getOrigin());
        this.pt1 = new ZPoint(segment.getEndpoint());
        this.direction = pt1.sub(pt0);
    }

    /* ------------- geometry method ------------- */

    public ZLine scaleTo(double multiple) {
        ZPoint newDir = direction.scaleTo(multiple);
        return new ZLine(pt0, pt0.add(newDir));
    }

    public ZLine reverse() {
        return new ZLine(this.pt1, this.pt0);
    }

    /* ------------- set & get ------------- */

    public void set(ZPoint pt0, ZPoint pt1) {
        this.pt0 = pt0;
        this.pt1 = pt1;
        this.direction = pt1.sub(pt0);
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

    public ZPoint pt0() {
        return this.pt0;
    }

    public ZPoint pt1() {
        return this.pt1;
    }

    public ZPoint getDirection() {
        return direction;
    }

    public ZPoint getDirectionUnit() {
        return direction.unit();
    }

    /* ------------- transformation -------------*/

    public String toString() {
        return "ZLine : [pt0:" + pt0.toString() + " pt1:" + pt1.toString() + "]";
    }

    /**
     * @return generalTools.ZPoint[]
     * @description transform to a virtual line described by "p" & "d"
     */
    public ZPoint[] toLinePD() {
        ZPoint[] line = new ZPoint[2];
        line[0] = pt0;
        line[1] = direction;
        return line;
    }

    /**
     * @return generalTools.ZPoint[]
     * @description transform to a virtual line described by "p" & "d", "d" is united
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
        return new WB_Segment();
    }

    public WB_Ray toWB_Ray() {
        return new WB_Ray(pt0.toWB_Point(), direction.toWB_Point());
    }

    /* ------------- draw -------------*/

    public void display() {

    }

    /**
     * @return void
     * @description draw
     */
    public void display(PApplet app) {
        app.line((float) pt0.x(), (float) pt0.y(), (float) pt0.z(), (float) pt1.x(), (float) pt1.y(), (float) pt1.z());
    }

    /**
     * @return void
     * @description draw
     */
    public void display(PApplet app, float strokeWeight) {
        app.strokeWeight(strokeWeight);
        app.line((float) pt0.x(), (float) pt0.y(), (float) pt0.z(), (float) pt1.x(), (float) pt1.y(), (float) pt1.z());
    }
}
