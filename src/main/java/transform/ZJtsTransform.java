package transform;

import basicGeometry.ZFactory;
import basicGeometry.ZPoint;
import org.locationtech.jts.geom.*;
import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_M44;

/**
 * 2D & 3D matrix transform for jts Geometry
 * refer to WB_Transform
 * using WB_M33 and WB_M44
 *
 * @author ZHANG Baizhou zhangbz
 * @project city_site_matching
 * @date 2021/10/20
 * @time 20:53
 */
public class ZJtsTransform {
    private WB_M33 T3;
    private WB_M33 invT3;

    private WB_M44 T4;
    private WB_M44 invT4;

    /* ------------- constructor ------------- */

    public ZJtsTransform() {
        this.T3 = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        this.invT3 = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);

        this.T4 = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        this.invT4 = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    /* ------------- update transform matrix 2D ------------- */

    /**
     * translate 2D
     *
     * @param v vector to translate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addTranslate2D(final ZPoint v) {
        this.T3 = (new WB_M33(1.0D, 0.0D, v.xd(), 0.0D, 1.0D, v.yd(), 0.0D, 0.0D, 1.0D)).mul(this.T3);
        this.invT3 = this.invT3.mul(new WB_M33(1.0D, 0.0D, -v.xd(), 0.0D, 1.0D, -v.yd(), 0.0D, 0.0D, 1.0D));
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
        this.T3 = (new WB_M33(1.0D, 0.0D, f * v.xd(), 0.0D, 1.0D, f * v.yd(), 0.0D, 0.0D, 1.0D)).mul(this.T3);
        this.invT3 = this.invT3.mul(new WB_M33(1.0D, 0.0D, -f * v.xd(), 0.0D, 1.0D, -f * v.yd(), 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 2D by vector
     *
     * @param s scale vector
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale2D(final ZPoint s) {
        this.T3 = (new WB_M33(s.xd(), 0.0D, 0.0D, 0.0D, s.yd(), 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T3);
        this.invT3 = this.invT3.mul(new WB_M33(1.0D / s.xd(), 0.0D, 0.0D, 0.0D, 1.0D / s.yd(), 0.0D, 0.0D, 0.0D, 1.0D));
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
        this.T3 = (new WB_M33(sx, 0.0D, 0.0D, 0.0D, sy, 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T3);
        this.invT3 = this.invT3.mul(new WB_M33(1.0D / sx, 0.0D, 0.0D, 0.0D, 1.0D / sy, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 2D
     *
     * @param s ratio
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale2D(final double s) {
        this.T3 = (new WB_M33(s, 0.0D, 0.0D, 0.0D, s, 0.0D, 0.0D, 0.0D, 1.0D)).mul(this.T3);
        this.invT3 = this.invT3.mul(new WB_M33(1.0D / s, 0.0D, 0.0D, 0.0D, 1.0D / s, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * rotate 2D about origin
     *
     * @param angle angle to rotate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutOrigin2D(final double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M33 tmp = new WB_M33(c, -s, 0.0D, s, c, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T3 = tmp.mul(this.T3);
        this.invT3 = this.invT3.mul(tmp.getTranspose());
        return this;
    }

    /**
     * rotate 2D about a given point
     *
     * @param angle angle to rotate
     * @param p     rotate base
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutPoint2D(final double angle, final ZPoint p) {
        this.addTranslate2D(-1.0D, p);
        this.addRotateAboutOrigin2D(angle);
        this.addTranslate2D(p);
        return this;
    }

    /**
     * reflect 2D about X axis
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectX2D() {
        this.addScale2D(1.0D, -1.0D);
        return this;
    }

    /**
     * reflect 2D about Y axis
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectY2D() {
        this.addScale2D(-1.0D, 1.0D);
        return this;
    }

    /**
     * reflect 2D about a line parallel with X axis
     *
     * @param p point
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectXAboutPoint2D(ZPoint p) {
        this.addTranslate2D(-1.0D, p);
        this.addScale2D(1.0D, -1.0D);
        this.addTranslate2D(p);
        return this;
    }

    /**
     * reflect 2D about a line parallel with Y axis
     *
     * @param p point
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectYAboutPoint2D(ZPoint p) {
        this.addTranslate2D(-1.0D, p);
        this.addScale2D(-1.0D, 1.0D);
        this.addTranslate2D(p);
        return this;
    }

    /**
     * reflect 2D about a line by 2 points
     *
     * @param p1 first point on the line
     * @param p2 second point on the line
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflect2D(ZPoint p1, ZPoint p2) {
        ZPoint v = p2.sub(p1).normalize();
        ZPoint newP2 = p1.add(v);
        double a = p1.yd() - newP2.yd();
        double b = newP2.xd() - p1.xd();
        double c = p1.xd() * newP2.yd() - newP2.xd() * p1.yd();
        WB_M33 tmp = new WB_M33(
                b * b - a * a,
                -2 * a * b,
                -2 * a * c,
                -2 * a * b,
                a * a - b * b,
                -2 * b * c,
                0.0D, 0.0D, 1.0D
        );
        tmp.mul(1 / (a * a + b * b));
        this.T3 = tmp.mul(this.T3);
        this.invT3 = this.invT3.mul(tmp.getTranspose());
        return this;
    }

    /**
     * reset the 2D matrix
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform reset2D() {
        this.T3 = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        this.invT3 = new WB_M33(1, 0, 0, 0, 1, 0, 0, 0, 1);
        return this;
    }

    /* ------------- update transform matrix 3D ------------- */

    /**
     * translate 3D
     *
     * @param v vector to translate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addTranslate3D(final ZPoint v) {
        this.T4 = (new WB_M44(1.0D, 0.0D, 0.0D, v.xd(), 0.0D, 1.0D, 0.0D, v.yd(), 0.0D, 0.0D, 1.0D, v.zd(), 0.0D, 0.0D, 0.0D, 1.0D)).mult(this.T4);
        this.invT4 = this.invT4.mult(new WB_M44(1.0D, 0.0D, 0.0D, -v.xd(), 0.0D, 1.0D, 0.0D, -v.yd(), 0.0D, 0.0D, 1.0D, -v.zd(), 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * translate 3D
     *
     * @param f ratio of translate vector
     * @param v vector to translate
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addTranslate3D(final double f, final ZPoint v) {
        this.T4 = (new WB_M44(1.0D, 0.0D, 0.0D, f * v.xd(), 0.0D, 1.0D, 0.0D, f * v.yd(), 0.0D, 0.0D, 1.0D, f * v.zd(), 0.0D, 0.0D, 0.0D, 1.0D)).mult(this.T4);
        this.invT4 = this.invT4.mult(new WB_M44(1.0D, 0.0D, 0.0D, -f * v.xd(), 0.0D, 1.0D, 0.0D, -f * v.yd(), 0.0D, 0.0D, 1.0D, -f * v.zd(), 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 3D by vector
     *
     * @param s scale vector
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale3D(final ZPoint s) {
        this.T4 = (new WB_M44(s.xd(), 0.0D, 0.0D, 0.0D, 0.0D, s.yd(), 0.0D, 0.0D, 0.0D, 0.0D, s.zd(), 0.0D, 0.0D, 0.0D, 0.0D, 1.0D)).mult(this.T4);
        this.invT4 = this.invT4.mult(new WB_M44(1.0D / s.xd(), 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / s.yd(), 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / s.zd(), 0.0D, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 3D by x,y,z
     *
     * @param sx scale vector x
     * @param sy scale vector y
     * @param sz scale vector z
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale3D(final double sx, final double sy, final double sz) {
        this.T4 = (new WB_M44(sx, 0.0D, 0.0D, 0.0D, 0.0D, sy, 0.0D, 0.0D, 0.0D, 0.0D, sz, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D)).mult(this.T4);
        this.invT4 = this.invT4.mult(new WB_M44(1.0D / sx, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / sy, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / sz, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * scale 3D
     *
     * @param s ratio
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addScale3D(final double s) {
        this.T4 = (new WB_M44(s, 0.0D, 0.0D, 0.0D, 0.0D, s, 0.0D, 0.0D, 0.0D, 0.0D, s, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D)).mult(this.T4);
        this.invT4 = this.invT4.mult(new WB_M44(1.0D / s, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / s, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D / s, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D));
        return this;
    }

    /**
     * rotate 3D about X axis
     *
     * @param angle rotate angle
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutX3D(final double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M44 tmp = new WB_M44(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, c, -s, 0.0D, 0.0D, s, c, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T4 = tmp.mult(this.T4);
        this.invT4 = this.invT4.mult(tmp.getTranspose());
        return this;
    }

    /**
     * rotate 3D about Y axis
     *
     * @param angle rotate angle
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutY3D(final double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M44 tmp = new WB_M44(c, 0.0D, s, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, -s, 0.0D, c, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T4 = tmp.mult(this.T4);
        this.invT4 = this.invT4.mult(tmp.getTranspose());
        return this;
    }

    /**
     * rotate 3D about Z axis
     *
     * @param angle rotate angle
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutZ3D(final double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M44 tmp = new WB_M44(c, -s, 0.0D, 0.0D, s, c, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T4 = tmp.mult(this.T4);
        this.invT4 = this.invT4.mult(tmp.getTranspose());
        return this;
    }

    /**
     * rotate 3D about the origin and a given axis direction
     *
     * @param angle   rotate angle
     * @param axisVec axis direction vector
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutOrigin3D(final double angle, final ZPoint axisVec) {
        ZPoint a = axisVec.normalize();
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        WB_M44 tmp = new WB_M44(a.xd() * a.xd() + (1.0D - a.xd() * a.xd()) * c, a.xd() * a.yd() * (1.0D - c) - a.zd() * s, a.xd() * a.zd() * (1.0D - c) + a.yd() * s, 0.0D, a.xd() * a.yd() * (1.0D - c) + a.zd() * s, a.yd() * a.yd() + (1.0D - a.yd() * a.yd()) * c, a.yd() * a.zd() * (1.0D - c) - a.xd() * s, 0.0D, a.xd() * a.zd() * (1.0D - c) - a.yd() * s, a.yd() * a.zd() * (1.0D - c) + a.xd() * s, a.zd() * a.zd() + (1.0D - a.zd() * a.zd()) * c, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D);
        this.T4 = tmp.mult(this.T4);
        this.invT4 = this.invT4.mult(tmp.getTranspose());
        return this;
    }

    /**
     * rotate 3D about a given axis
     *
     * @param angle   rotate angle
     * @param p       axis origin
     * @param axisVec axis direction vector
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutAxis3D(final double angle, final ZPoint p, final ZPoint axisVec) {
        this.addTranslate3D(-1.0D, p);
        this.addRotateAboutOrigin3D(angle, axisVec);
        this.addTranslate3D(p);
        return this;
    }

    /**
     * rotate 3D about a given axis represented by 2 points
     *
     * @param angle rotate angle
     * @param p0    first point
     * @param p1    second point
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addRotateAboutTwoPoints3D(final double angle, final ZPoint p0, final ZPoint p1) {
        this.addTranslate3D(-1.0D, p0);
        this.addRotateAboutOrigin3D(angle, p1.sub(p0));
        this.addTranslate3D(p0);
        return this;
    }

    /**
     * reflect 2D about XY plane
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectXY3D() {
        this.addScale3D(1.0D, 1.0D, -1.0D);
        return this;
    }

    /**
     * reflect 2D about YZ plane
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectYZ3D() {
        this.addScale3D(-1.0D, 1.0D, 1.0D);
        return this;
    }

    /**
     * reflect 2D about XZ plane
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform addReflectXZ3D() {
        this.addScale3D(1.0D, -1.0D, 1.0D);
        return this;
    }

    /**
     * duplicate 2D transform to 3D matrix for applying to 3D Geometry
     *
     * @return transform.ZJtsTransform
     */
    public ZJtsTransform duplicate2DTo3D() {
        this.T4.m11 = this.T3.m11;
        this.T4.m12 = this.T3.m12;
        this.T4.m14 = this.T3.m13;
        this.T4.m21 = this.T3.m21;
        this.T4.m22 = this.T3.m22;
        this.T4.m24 = this.T3.m23;
        this.T4.m31 = this.T3.m31;
        this.T4.m32 = this.T3.m32;
        this.T4.m34 = this.T3.m33;
        return this;
    }

    /* ------------- perform transform ------------- */

    /**
     * apply 2D transform to coordinate
     *
     * @param c original coordinate
     * @return org.locationtech.jts.geom.Coordinate
     */
    public Coordinate applyToCoordinate2D(final Coordinate c) {
        double xp = this.T3.m11 * c.getX() + this.T3.m12 * c.getY() + this.T3.m13;
        double yp = this.T3.m21 * c.getX() + this.T3.m22 * c.getY() + this.T3.m23;
        double wp = this.T3.m31 * c.getX() + this.T3.m32 * c.getY() + this.T3.m33;
        if (wp == 0) {
            return new Coordinate(xp, yp);
        } else {
            wp = 1.0D / wp;
            return new Coordinate(xp * wp, yp * wp);
        }
    }

    /**
     * apply 2D transform to Geometry
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

    /**
     * apply 3D transform to coordinate
     *
     * @param c original coordinate
     * @return org.locationtech.jts.geom.Coordinate
     */
    private Coordinate applyToCoordinate3D(final Coordinate c) {
        double xp = this.T4.m11 * c.getX() + this.T4.m12 * c.getY() + this.T4.m13 * c.getZ() + this.T4.m14;
        double yp = this.T4.m21 * c.getX() + this.T4.m22 * c.getY() + this.T4.m23 * c.getZ() + this.T4.m24;
        double zp = this.T4.m31 * c.getX() + this.T4.m32 * c.getY() + this.T4.m33 * c.getZ() + this.T4.m34;
        double wp = this.T4.m41 * c.getX() + this.T4.m42 * c.getY() + this.T4.m43 * c.getZ() + this.T4.m44;
        if (WB_Epsilon.isZero(wp)) {
            return new Coordinate(xp, yp, zp);
        } else {
            wp = 1.0D / wp;
            return new Coordinate(xp * wp, yp * wp, zp * wp);
        }
    }

    /**
     * apply 3D transform to Geometry
     *
     * @param geo original Geometry
     * @return org.locationtech.jts.geom.Geometry
     */
    public Geometry applyToGeometry3D(final Geometry geo) {
        String type = geo.getGeometryType();
        Geometry result = null;
        switch (type) {
            case "Point":
                Point originalP = (Point) geo;
                Coordinate cP = applyToCoordinate3D(originalP.getCoordinate());
                result = ZFactory.jtsgf.createPoint(cP);
                break;
            case "LineString":
                LineString originalLS = (LineString) geo;
                Coordinate[] newCoordsLS = new Coordinate[originalLS.getNumPoints()];
                for (int i = 0; i < newCoordsLS.length; i++) {
                    newCoordsLS[i] = applyToCoordinate3D(originalLS.getCoordinateN(i));
                }
                result = ZFactory.jtsgf.createLineString(newCoordsLS);
                break;
            case "Polygon":
                Polygon originalPoly = (Polygon) geo;
                Coordinate[] newCoordsPoly = new Coordinate[originalPoly.getNumPoints()];
                for (int i = 0; i < newCoordsPoly.length; i++) {
                    newCoordsPoly[i] = applyToCoordinate3D(originalPoly.getCoordinates()[i]);
                }
                result = ZFactory.jtsgf.createPolygon(newCoordsPoly);
                break;
            case "MultiPoint":
                Point[] points = new Point[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    points[i] = (Point) applyToGeometry3D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiPoint(points);
                break;
            case "MultiLineString":
                LineString[] lineStrings = new LineString[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    lineStrings[i] = (LineString) applyToGeometry3D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiLineString(lineStrings);
                break;
            case "MultiPolygon":
                Polygon[] polygons = new Polygon[geo.getNumGeometries()];
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    polygons[i] = (Polygon) applyToGeometry3D(geo.getGeometryN(i));
                }
                result = ZFactory.jtsgf.createMultiPolygon(polygons);
                break;
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    public WB_M33 getT3() {
        return T3;
    }

    public WB_M33 getInvT3() {
        return invT3;
    }

    public WB_M44 getT4() {
        return T4;
    }

    public WB_M44 getInvT4() {
        return invT4;
    }
}
