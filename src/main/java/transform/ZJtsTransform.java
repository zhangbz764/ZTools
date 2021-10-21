package transform;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.geom.*;
import wblut.math.WB_M33;

/**
 * 2D matrix transform for jts Geometry
 * using WB_M33
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/20
 * @time 20:53
 */
public class ZJtsTransform {
    private WB_M33 T;
    private WB_M33 invT;

    /* ------------- constructor ------------- */

    public ZJtsTransform() {
        this.T = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        this.invT = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    /* ------------- update transform matrix ------------- */

    /**
     * translate 2D
     *
     * @param v vector to translate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addTranslate2D(final ZPoint v) {
        this.T = (new WB_M33(1.0D, 0.0D, v.xd(), 0.0D, 1.0D, v.yd(), 0.0D, 0.0D, 1.0D)).mul(this.T);
        this.invT = this.invT.mul(new WB_M33(1.0D, 0.0D, -v.xd(), 0.0D, 1.0D, -v.yd(), 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * translate 2D with scale
     *
     * @param f ratio of translate vector
     * @param v vector to translate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addTranslate2D(final double f, final ZPoint v) {
        this.T = (new WB_M33(1.0D, 0.0D, f * v.xd(), 0.0D, 1.0D, f * v.yd(), 0.0D, 0.0D, 1.0D)).mul(this.T);
        this.invT = this.invT.mul(new WB_M33(1.0D, 0.0D, -f * v.xd(), 0.0D, 1.0D, -f * v.yd(), 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 2D by vector
     *
     * @param s scale vector
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale2D(final ZPoint s) {
        this.T = (new WB_M33(s.xd(), 0.0D, 0.0D, 0.0D, s.yd(), 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T);
        this.invT = this.invT.mul(new WB_M33(1.0D / s.xd(), 0.0D, 0.0D, 0.0D, 1.0D / s.yd(), 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 2D by x,y
     *
     * @param sx scale vector x
     * @param sy scale vector y
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale2D(final double sx, final double sy) {
        this.T = (new WB_M33(sx, 0.0D, 0.0D, 0.0D, sy, 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T);
        this.invT = this.invT.mul(new WB_M33(1.0D / sx, 0.0D, 0.0D, 0.0D, 1.0D / sy, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 2D
     *
     * @param s ratio
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale2D(final double s) {
        this.T = (new WB_M33(s, 0.0D, 0.0D, 0.0D, s, 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T);
        this.invT = this.invT.mul(new WB_M33(1.0D / s, 0.0D, 0.0D, 0.0D, 1.0D / s, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * rotate about origin
     *
     * @param angle angle to rotate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutOrigin(final double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M33 tmp = new WB_M33(c, -s, 0.0D, s, c, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T = tmp.mul(this.T);
        this.invT = this.invT.mul(tmp.getTranspose());
        return this;
    }

    /**
     * rotate about a given point
     *
     * @param angle angle to rotate
     * @param p     rotate base
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutPoint(final double angle, final ZPoint p) {
        this.addTranslate2D(-1.0D, p);
        this.addRotateAboutOrigin(angle);
        this.addTranslate2D(p);
        return this;
    }

    public ZJtsTransform reset() {
        this.T = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        this.invT = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        return this;
    }

    /* ------------- perform transform ------------- */

    /**
     * apply transform to coordinate
     *
     * @param c original coordinate
     * @return org.locationtech.jts.geom.Coordinate
     */
    private Coordinate applyToCoordinate2D(final Coordinate c) {
        double xp = this.T.m11 * c.getX() + this.T.m12 * c.getY() + this.T.m13;
        double yp = this.T.m21 * c.getX() + this.T.m22 * c.getY() + this.T.m23;
        double wp = this.T.m31 * c.getX() + this.T.m32 * c.getY() + this.T.m33;
        if (wp == 0) {
            return new Coordinate(xp, yp);
        } else {
            wp = 1.0D / wp;
            return new Coordinate(xp * wp, yp * wp);
        }
    }

    /**
     * apply transform to Geometry
     *
     * @param geo original Geometry
     * @return org.locationtech.jts.geom.Geometry
     */
    public Geometry applyToGeometry2D(final Geometry geo) {
        String type = geo.getGeometryType();
        Geometry result = null;
        switch (type) {
            case "Point":
                Point originalP = (Point) geo;
                Coordinate cP = applyToCoordinate2D(originalP.getCoordinate());
                result = ZFactory.jtsgf.createPoint(cP);
                break;
            case "LineString":
                LineString originalLS = (LineString) geo;
                Coordinate[] newCoordsLS = new Coordinate[originalLS.getNumPoints()];
                for (int i = 0; i < newCoordsLS.length; i++) {
                    newCoordsLS[i] = applyToCoordinate2D(originalLS.getCoordinateN(i));
                }
                result = ZFactory.jtsgf.createLineString(newCoordsLS);
                break;
            case "Polygon":
                Polygon originalPoly = (Polygon) geo;
                Coordinate[] newCoordsPoly = new Coordinate[originalPoly.getNumPoints()];
                for (int i = 0; i < newCoordsPoly.length; i++) {
                    newCoordsPoly[i] = applyToCoordinate2D(originalPoly.getCoordinates()[i]);
                }
                result = ZFactory.jtsgf.createPolygon(newCoordsPoly);
                break;
            case "MultiPoint":
                Point[] points = new Point[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    points[i] = (Point) applyToGeometry2D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiPoint(points);
                break;
            case "MultiLineString":
                LineString[] lineStrings = new LineString[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    lineStrings[i] = (LineString) applyToGeometry2D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiLineString(lineStrings);
                break;
            case "MultiPolygon":
                Polygon[] polygons = new Polygon[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    polygons[i] = (Polygon) applyToGeometry2D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiPolygon(polygons);
                break;
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    public WB_M33 getT() {
        return T;
    }

    public WB_M33 getInvT() {
        return invT;
    }
}
