package math;

import basicGeometry.*;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import transform.ZTransform;
import wblut.geom.*;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;
import wblut.math.WB_Epsilon;

import java.util.*;

/**
 * geometry math tools
 * vector, intersection, distance, geometry relation, boundary methods, polygon tool, and others
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/9/29
 * @time 15:38
 * <p>
 */
public final class ZGeoMath {
    public static final double epsilon = 0.00000001;
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    private static final GeometryFactory jtsgf = new GeometryFactory();

    /*-------- vector angle --------*/

    /**
     * 2D cross product for jts Vector2D
     *
     * @param v1 v1
     * @param v2 v2
     * @return double
     */
    public static double cross2D(Vector2D v1, Vector2D v2) {
        return v1.getX() * v2.getY() - v1.getY() * v2.getX();
    }

    /**
     * return a new normalized WB_Vector
     *
     * @param v original WB_Vector
     * @return wblut.geom.WB_Vector
     */
    public static WB_Vector normalizeWB(WB_Vector v) {
        double d = v.getLength();
        if (WB_Epsilon.isZero(d)) {
            return new WB_Vector(0.0, 0.0, 0.0);
        } else {
            return new WB_Vector(v.xd() / d, v.yd() / d, v.zd() / d);
        }
    }

    /**
     * get angle bisector by the order of v0 -> v1 (counter-clockwise, reflex angle includes)
     *
     * @param v0 first vector
     * @param v1 second vector
     */
    public static WB_Vector getAngleBisectorOrdered(final WB_Vector v0, final WB_Vector v1) {
        if (WB_CoordOp2D.cross2D(v0, v1) > 0) {
            WB_Vector bi = normalizeWB(v0).add(normalizeWB(v1));
            bi.normalizeSelf();
            return bi;
        } else if (WB_CoordOp2D.cross2D(v0, v1) < 0) {
            WB_Vector bi = normalizeWB(v0).add(normalizeWB(v1));
            bi.scale(-1);
            bi.normalizeSelf();
            return bi;
        } else {
            if (v0.dot2D(v1) > 0) {
                return normalizeWB(v0);
            } else {
                WB_Vector ortho = new WB_Vector(v0.yd(), -v0.xd());
                if (!(WB_CoordOp2D.cross2D(v0, ortho) > 0)) {
                    ortho.scaleSelf(-1);
                }
                ortho.normalizeSelf();
                return ortho;
            }
        }
    }

    /**
     * get angle bisector by the order of v0 -> v1 (counter-clockwise, reflex angle includes)
     *
     * @param v0 first vector
     * @param v1 second vector
     */
    public static Vector2D getAngleBisectorOrdered(final Vector2D v0, final Vector2D v1) {
        if (cross2D(v0, v1) > 0) {
            return v0.normalize().add(v1.normalize()).normalize();
        } else if (cross2D(v0, v1) < 0) {
            return v0.normalize().add(v1.normalize()).normalize().multiply(-1);
        } else {
            if (v0.dot(v1) > 0) {
                return v0.normalize();
            } else {
                Vector2D ortho = new Vector2D(v0.getY(), -v0.getX()).normalize();
                if (!(cross2D(v0, ortho) > 0)) {
                    ortho = ortho.multiply(-1);
                }
                return ortho;
            }
        }
    }

    /**
     * find all concave points in a polygon (WB_Polygon)
     *
     * @param polygon input WB_Polygon
     */
    public static List<WB_Point> getConcavePoints(final WB_Polygon polygon) {
        List<WB_Point> concavePoints = new ArrayList<>();
        WB_Polygon faceUp = polygonFaceUp(polygon); // 保证正向
        for (int i = 1; i < faceUp.getNumberOfPoints(); i++) {
            WB_Point prev = faceUp.getPoint(i - 1).sub(faceUp.getPoint(i));
            WB_Point next = faceUp.getPoint((i + 1) % (faceUp.getNumberOfPoints() - 1)).sub(faceUp.getPoint(i));
            double crossValue = WB_CoordOp2D.cross2D(next, prev);
            if (crossValue < 0) {
                concavePoints.add(faceUp.getPoint(i));
            }
        }
        return concavePoints;
    }

    /**
     * find all concave points in a polygon (jts Polygon)
     *
     * @param polygon input jts Polygon
     */
    public static List<Coordinate> getConcavePoints(final Polygon polygon) {
        List<Coordinate> concavePoints = new ArrayList<>();
        WB_Polygon wbPolygon = ZTransform.PolygonToWB_Polygon(polygon);
        List<WB_Point> concave = getConcavePoints(wbPolygon);
        concave.forEach(wbp -> concavePoints.add(ZTransform.WB_CoordToCoordinate(wbp)));
        return concavePoints;
    }

    /**
     * find all concave points indices in a polygon (WB)
     *
     * @param polygon input WB_Polygon
     * @return java.util.List<java.lang.Integer> - indices of input polygon
     */
    public static List<Integer> getConcavePointIndices(final WB_Polygon polygon) {
        List<Integer> concavePoints = new ArrayList<>();
        if (polygon.getNormal().zd() > 0) {
            for (int i = 1; i < polygon.getNumberOfPoints(); i++) {
                ZPoint prev = new ZPoint(polygon.getPoint(i - 1).sub(polygon.getPoint(i)));
                ZPoint next = new ZPoint(polygon.getPoint((i + 1) % (polygon.getNumberOfPoints() - 1)).sub(polygon.getPoint(i)));
                double crossValue = next.cross2D(prev);
                if (crossValue < 0) {
                    if (i == polygon.getNumberOfPoints() - 1) {
                        concavePoints.add(0);
                    } else {
                        concavePoints.add(i);
                    }
                }
            }
        } else {
            for (int i = 1; i < polygon.getNumberOfPoints(); i++) {
                ZPoint prev = new ZPoint(polygon.getPoint(i - 1).sub(polygon.getPoint(i)));
                ZPoint next = new ZPoint(polygon.getPoint((i + 1) % (polygon.getNumberOfPoints() - 1)).sub(polygon.getPoint(i)));
                double crossValue = prev.cross2D(next);
                if (crossValue < 0) {
                    if (i == polygon.getNumberOfPoints() - 1) {
                        concavePoints.add(0);
                    } else {
                        concavePoints.add(i);
                    }
                }
            }
        }
        return concavePoints;
    }

    /**
     * find all concave points indices in a polygon(jts)
     *
     * @param polygon input jts Polygon
     * @return java.util.List<java.lang.Integer> - indices of input polygon
     */
    public static List<Integer> getConcavePointIndices(final Polygon polygon) {
        WB_Polygon wbPolygon = ZTransform.PolygonToWB_Polygon(polygon);
        return getConcavePointIndices(wbPolygon);
    }

    /**
     * sort a list of vectors by polar coordinates, return indices
     *
     * @param vectors vector list to be sorted
     * @return int[] - indices of input list
     */
    public static int[] sortPolarAngleIndices(final List<? extends WB_Vector> vectors) {
        assert !vectors.isEmpty() : "input list must at least include 1 vector";
        double[] atanValue = new double[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            double curr_value = Math.atan2(vectors.get(i).yd(), vectors.get(i).xd());
            atanValue[i] = curr_value;
        }
        return ZMath.getArraySortedIndices(atanValue);
    }

    /**
     * sort a list of vectors by polar coordinates, return new list of vectors
     *
     * @param vectors vector list to be sorted
     */
    public static WB_Vector[] sortPolarAngle(final List<? extends WB_Vector> vectors) {
        assert !vectors.isEmpty() : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        WB_Vector[] sorted = new WB_Vector[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = vectors.get(newOrder[i]);
        }
        return sorted;
    }

    /**
     * sort a list of vectors by polar coordinates, return new list of normalized vectors
     *
     * @param vectors vector list to be sorted
     */
    public static WB_Vector[] sortPolarAngleNor(final List<? extends WB_Vector> vectors) {
        assert !vectors.isEmpty() : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        WB_Vector[] sorted = new WB_Vector[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = normalizeWB(vectors.get(newOrder[i]));
        }
        return sorted;
    }

    /**
     * find the nearest vector in a list of vector
     *
     * @param target target vector
     * @param other  vector list
     */
    public static WB_Vector getClosestVec(final WB_Vector target, final List<? extends WB_Vector> other) {
        assert other != null && !other.isEmpty() : "invalid input vectors";
        double[] dotValue = new double[other.size()];
        for (int i = 0; i < other.size(); i++) {
            dotValue[i] = normalizeWB(target).dot2D(normalizeWB(other.get(i)));
        }
        int maxIndex = ZMath.getMaxIndex(dotValue);
        return other.get(maxIndex);
    }

    /*-------- intersection 2D (jts) --------*/

    /**
     * get intersection points of two segments (given 4 coords)
     *
     * @param seg0c0
     * @param seg0c1
     * @param seg1c0
     * @param seg1c1
     * @return
     */
    public static Coordinate lineIntersection2D(final Coordinate seg0c0, final Coordinate seg0c1, final Coordinate seg1c0, final Coordinate seg1c1) {
        Vector2D[] seg0 = new Vector2D[]{new Vector2D(seg0c0), new Vector2D(seg0c0, seg0c1).normalize()};
        Vector2D[] seg1 = new Vector2D[]{new Vector2D(seg1c0), new Vector2D(seg1c0, seg1c1).normalize()};

        Vector2D delta = seg1[0].subtract(seg0[0]);
        double crossBase = cross2D(seg0[1], seg1[1]);
        double crossDelta0 = cross2D(delta, seg0[1]);
        double crossDelta1 = cross2D(delta, seg1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray0
            double t = crossDelta0 / crossBase; // ray1
            return seg1[0].add(seg1[1].multiply(t)).toCoordinate();
        }
    }

    /**
     * get intersection points of two rays
     *
     * @param ray0 first ray {point P, direction d}
     * @param ray1 second ray {point P, direction d}
     * @return org.locationtech.jts.geom.Coordinate
     */
    public static Coordinate rayIntersection2D(final Vector2D[] ray0, final Vector2D[] ray1) {
        Vector2D delta = ray1[0].subtract(ray0[0]);
        double crossBase = cross2D(ray0[1], ray1[1]);
        double crossDelta0 = cross2D(delta, ray0[1]);
        double crossDelta1 = cross2D(delta, ray1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray0
            double t = crossDelta0 / crossBase; // ray1
            if (s >= 0 && t >= 0) {
                return ray1[0].add(ray1[1].multiply(t)).toCoordinate();
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }


    /**
     * get intersection points of a ray and a polygon ([--) for each edge)
     *
     * @param ray  ray {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<org.locationtech.jts.geom.Coordinate>
     */
    public static List<Coordinate> rayPolygonIntersection2D(final Vector2D[] ray, final Polygon poly) {
        List<Coordinate> result = new ArrayList<>();

        for (int i = 0; i < poly.getCoordinates().length - 1; i++) {
            Coordinate c0 = poly.getCoordinates()[i];
            Coordinate c1 = poly.getCoordinates()[i + 1];
            Vector2D[] polySeg = new Vector2D[]{
                    Vector2D.create(c0),
                    new Vector2D(c0, c1)
            };

            Coordinate intersect = null;
            Vector2D delta = polySeg[0].subtract(ray[0]);
            double crossBase = cross2D(ray[1], polySeg[1]);
            double crossDelta0 = cross2D(delta, ray[1]);
            double crossDelta1 = cross2D(delta, polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // ray
                double t = crossDelta0 / crossBase; // seg
                if (s >= 0 && t >= 0 && t < 1) {
                    intersect = polySeg[0].add(polySeg[1].multiply(t)).toCoordinate();
                }
            }

            if (intersect != null) {
                result.add(intersect);
            }
        }
        return result;
    }

    /*-------- intersection 2D --------*/

    // TODO: 2021/1/4 intersection check

    /**
     * check a ray and a segment are intersecting
     *
     * @param ray ray {point P, direction d}
     * @param seg segment {point P, direction d}
     * @return boolean
     */
    public static boolean checkRaySegmentIntersect(final ZPoint[] ray, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(ray[0]);
        double crossBase = ray[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(ray[1]);
        double crossDelta1 = delta.cross2D(seg[1]);

        if (Math.abs(crossBase) < epsilon) {
            return false;
        } else {
            double s = crossDelta1 / crossBase; // ray
            double t = crossDelta0 / crossBase; // seg
            return s >= 0 && t >= 0 && t <= 1;
        }
    }

    /**
     * check a line and a segment are intersecting
     *
     * @param line line {point P, direction d}
     * @param seg  segment {point P, direction d}
     * @return boolean
     */
    public static boolean checkLineSegmentIntersect(final ZPoint[] line, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(line[0]);
        double crossBase = line[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(line[1]);

        if (Math.abs(crossBase) < epsilon) {
            return false;
        } else {
            double t = crossDelta0 / crossBase; // seg
            return t >= 0 && t <= 1;
        }
    }

    /**
     * check 2 lines are intersecting
     *
     * @param line1 line1 {point P, direction d}
     * @param line2 line2 {point P, direction d}
     * @return boolean
     */
    public static boolean checkLineIntersect(final ZPoint[] line1, final ZPoint[] line2) {
        double crossBase = line1[1].cross2D(line2[1]);

        return !(Math.abs(crossBase) < epsilon);
    }

    /**
     * check a ray and a polyline are intersecting
     *
     * @param ray ray {point P, direction d}
     * @param pl  polyline
     * @return boolean
     */
    public static boolean checkRayPolyLineIntersect(final ZPoint[] ray, final WB_PolyLine pl) {
        for (int i = 0; i < pl.getNumberSegments(); i++) {
            if (checkRaySegmentIntersect(ray, new ZLine(pl.getSegment(i)).toLinePD())) {
                return true;
            }
        }
        return false;
    }

    /**
     * check a ray and a polyline are intersecting
     *
     * @param ray ray {point P, direction d}
     * @param ls  polyline
     * @return boolean
     */
    public static boolean checkRayPolyLineIntersect(final ZPoint[] ray, final LineString ls) {
        for (int i = 0; i < ls.getCoordinates().length - 1; i++) {
            if (checkRaySegmentIntersect(ray, new ZLine(ls.getCoordinateN(i), ls.getCoordinateN(i + 1)).toLinePD())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get intersection points: line, ray or segment
     *
     * @param l0    first line
     * @param type0 first type "line" "ray" "segment"
     * @param l1    second line
     * @param type1 second type "line" "ray" "segment"
     * @return geometry.ZPoint
     */
    public static ZPoint simpleLineElementsIntersection2D(final ZLine l0, final String type0, final ZLine l1, final String type1) {
        return simpleLineElementsIntersection2D(l0.toLinePD(), type0, l1.toLinePD(), type1);
    }

    /**
     * get intersection points: line, ray or segment
     *
     * @param l0    first line
     * @param type0 first type "line" "ray" "segment"
     * @param l1    second line
     * @param type1 second type "line" "ray" "segment"
     * @return geometry.ZPoint
     */
    public static ZPoint simpleLineElementsIntersection2D(final ZPoint[] l0, final String type0, final ZPoint[] l1, final String type1) {
        if (type0.equals("line") && type1.equals("line")) {
            return lineIntersection2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("segment")) {
            return segmentIntersection2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("ray")) {
            return rayIntersection2D(l0, l1);
        } else if (type0.equals("line") && type1.equals("ray")) {
            return lineRayIntersection2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("line")) {
            return lineRayIntersection2D(l1, l0);
        } else if (type0.equals("line") && type1.equals("segment")) {
            return lineSegmentIntersection2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("line")) {
            return lineSegmentIntersection2D(l1, l0);
        } else if (type0.equals("ray") && type1.equals("segment")) {
            return raySegmentIntersection2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("ray")) {
            return lineRayIntersection2D(l1, l0);
        } else {
            throw new IllegalArgumentException("input type must be line, ray or segment");
        }
    }

    /**
     * get intersection points of two lines
     *
     * @param line0 first line {point P, direction d}
     * @param line1 second line {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineIntersection2D(final ZPoint[] line0, final ZPoint[] line1) {
        ZPoint delta = line1[0].sub(line0[0]);
        double crossDelta = delta.cross2D(line0[1]);
        double crossBase = line0[1].cross2D(line1[1]);
        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double t = crossDelta / crossBase;
            return line1[0].add(line1[1].scaleTo(t));
        }
    }

    /**
     * get intersection points of two rays
     *
     * @param ray0 first ray {point P, direction d}
     * @param ray1 second ray {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint rayIntersection2D(final ZPoint[] ray0, final ZPoint[] ray1) {
        ZPoint delta = ray1[0].sub(ray0[0]);
        double crossBase = ray0[1].cross2D(ray1[1]);
        double crossDelta0 = delta.cross2D(ray0[1]);
        double crossDelta1 = delta.cross2D(ray1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray0
            double t = crossDelta0 / crossBase; // ray1
            if (s >= 0 && t >= 0) {
                return ray1[0].add(ray1[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * get intersection points of two segments
     *
     * @param seg0 first segment {point P, direction d}
     * @param seg1 first segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint segmentIntersection2D(final ZPoint[] seg0, final ZPoint[] seg1) {
        ZPoint delta = seg1[0].sub(seg0[0]);
        double crossBase = seg0[1].cross2D(seg1[1]);
        double crossDelta0 = delta.cross2D(seg0[1]);
        double crossDelta1 = delta.cross2D(seg1[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // seg0
            double t = crossDelta0 / crossBase; // seg1
            if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
                return seg1[0].add(seg1[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * get intersection points of a line and a ray
     *
     * @param line line {point P, direction d}
     * @param ray  ray {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineRayIntersection2D(final ZPoint[] line, final ZPoint[] ray) {
        ZPoint delta = ray[0].sub(line[0]);
        double crossBase = line[1].cross2D(ray[1]);
        double crossDelta0 = delta.cross2D(line[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double t = crossDelta0 / crossBase; // ray
            if (t >= 0) {
                return ray[0].add(ray[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * get intersection points of a line and a segment
     *
     * @param line line {point P, direction d}
     * @param seg  segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint lineSegmentIntersection2D(final ZPoint[] line, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(line[0]);
        double crossBase = line[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(line[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double t = crossDelta0 / crossBase; // seg
            if (t >= 0 && t <= 1) {
                return seg[0].add(seg[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * get intersection points of a ray and a segment
     *
     * @param ray ray {point P, direction d}
     * @param seg segment {point P, direction d}
     * @return geometry.ZPoint
     */
    public static ZPoint raySegmentIntersection2D(final ZPoint[] ray, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(ray[0]);
        double crossBase = ray[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(ray[1]);
        double crossDelta1 = delta.cross2D(seg[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // ray
            double t = crossDelta0 / crossBase; // seg
            if (s >= 0 && t >= 0 && t <= 1) {
                return seg[0].add(seg[1].scaleTo(t));
            } else {
//                System.out.println("intersection is not on one of these line elements");
                return null;
            }
        }
    }

    /**
     * get intersection points of a segment and a polyline ([--) for each edge, [--] for the last one)
     *
     * @param seg seg {point P, direction d}
     * @param pl  input polyline
     * @return java.util.List<basicGeometry.ZPoint>
     */
    public static List<ZPoint> segmentPolyLineIntersection2D(final ZPoint[] seg, final WB_PolyLine pl) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < pl.getNumberSegments() - 1; i++) {
            ZPoint[] polySeg = new ZLine(pl.getSegment(i)).toLinePD();
            ZPoint intersect = null;

            ZPoint delta = polySeg[0].sub(seg[0]);
            double crossBase = seg[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(seg[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // seg
                double t = crossDelta0 / crossBase; // polySeg
                if (s >= 0 && s <= 1 && t >= 0 && t < 1) {
                    intersect = polySeg[0].add(polySeg[1].scaleTo(t));
                }
            }
            if (intersect != null) {
                result.add(intersect);
            }
        }

        // final polyline segment: [--]
        ZPoint[] polySeg = new ZLine(pl.getSegment(pl.getNumberSegments() - 1)).toLinePD();
        ZPoint intersect = null;

        ZPoint delta = polySeg[0].sub(seg[0]);
        double crossBase = seg[1].cross2D(polySeg[1]);
        double crossDelta0 = delta.cross2D(seg[1]);
        double crossDelta1 = delta.cross2D(polySeg[1]);

        if (Math.abs(crossBase) >= epsilon) {
            double s = crossDelta1 / crossBase; // seg
            double t = crossDelta0 / crossBase; // polySeg
            if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
                intersect = polySeg[0].add(polySeg[1].scaleTo(t));
            }
        }
        if (intersect != null) {
            result.add(intersect);
        }

        return result;
    }

    /**
     * get intersection points of a ray and a polygon ([--) for each edge)
     *
     * @param ray  ray {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> rayPolygonIntersection2D(final ZPoint[] ray, final WB_Polygon poly) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint intersect = null;

            ZPoint delta = polySeg[0].sub(ray[0]);
            double crossBase = ray[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(ray[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // ray
                double t = crossDelta0 / crossBase; // seg
                if (s >= 0 && t >= 0 && t < 1) {
                    intersect = polySeg[0].add(polySeg[1].scaleTo(t));
                }
            }

            if (intersect != null) {
                result.add(intersect);
            }
        }
        return result;
    }

    /**
     * get intersection points of a line and a polygon ([--) for each edge)
     *
     * @param line line {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> linePolygonIntersection2D(final ZPoint[] line, final WB_Polygon poly) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint intersect = null;

            ZPoint delta = polySeg[0].sub(line[0]);
            double crossBase = line[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(line[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double t = crossDelta0 / crossBase; // seg
                if (t >= 0 && t < 1) {
                    intersect = polySeg[0].add(polySeg[1].scaleTo(t));
                }
            }
            if (intersect != null) {
                result.add(intersect);
            }
        }
        return result;
    }

    /**
     * get intersection points of a ray and a polygon (return indices by distance order)
     *
     * @param ray  ray {point P, direction d}
     * @param poly input polygon
     * @return java.util.List<java.lang.Integer> - sorted indices of input polygon
     */
    public static List<Integer> rayPolygonIntersectionIndices2D(final ZPoint[] ray, final WB_Polygon poly) {
        List<Integer> indicesResult = new ArrayList<>();
        List<Double> resultDist = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint delta = polySeg[0].sub(ray[0]);
            double crossBase = ray[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(ray[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // ray
                double t = crossDelta0 / crossBase; // seg
                if (s >= 0 && t > 0 && t <= 1) {
                    indicesResult.add(i);
                    resultDist.add(polySeg[0].add(polySeg[1].scaleTo(t)).distanceSq(ray[0]));
                }
            }
        }

        if (resultDist.size() > 1) {
            double[] distArray = new double[resultDist.size()];
            for (int i = 0; i < resultDist.size(); i++) {
                distArray[i] = resultDist.get(i);
            }
            int[] ascending = ZMath.getArraySortedIndices(distArray);

            List<Integer> newOrder = new ArrayList<>();
            for (int j = 0; j < indicesResult.size(); j++) {
                newOrder.add(indicesResult.get(ascending[j]));
            }
            return newOrder;
        } else {
            return indicesResult;
        }
    }

    /**
     * get intersection points of a circle and a segment
     *
     * @param S input segment
     * @param C input circle
     * @return java.util.List<wblut.geom.WB_Point>
     */
    public static List<WB_Point> segmentCircleIntersection2D(final WB_Segment S, final WB_Circle C) {
        List<WB_Point> result = new ArrayList<>();
        double a = WB_CoordOp2D.getSqLength2D(
                S.getEndpoint().xd() - S.getOrigin().xd(),
                S.getEndpoint().yd() - S.getOrigin().yd()
        );
        double b = 2.0D * (
                (S.getEndpoint().xd() - S.getOrigin().xd()) * (S.getOrigin().xd() - C.getCenter().xd())
                        + (S.getEndpoint().yd() - S.getOrigin().yd()) * (S.getOrigin().yd() - C.getCenter().yd())
        );
        double c = C.getCenter().xd() * C.getCenter().xd() + C.getCenter().yd() * C.getCenter().yd()
                + S.getOrigin().xd() * S.getOrigin().xd() + S.getOrigin().yd() * S.getOrigin().yd()
                - 2.0D * (C.getCenter().xd() * S.getOrigin().xd() + C.getCenter().yd() * S.getOrigin().yd())
                - C.getRadius() * C.getRadius();
        double delta = b * b - 4.0D * a * c;
        if (delta < -WB_Epsilon.EPSILON) {
            // no intersection
            return result;
        } else if (WB_Epsilon.isZero(delta)) {
            // tangent
            double u = -0.5D * (b / a);
            if (u >= 0.0D && u <= 1.0D) {
                double segLength = S.getLength();
                result.add(S.getPoint(u * segLength));
            }
            return result;
        } else {
            // may have 2 intersections
            double deltaSq = Math.sqrt(delta);
            double u1 = 0.5 * (-b + deltaSq) / a;
            double u2 = 0.5 * (-b - deltaSq) / a;
            double segLength = S.getLength();
            if (u1 >= 0.0D && u1 <= 1.0D) {
                result.add(S.getPoint(u1 * segLength));
            }
            if (u2 >= 0.0D && u2 <= 1.0D) {
                result.add(S.getPoint(u2 * segLength));
            }
            return result;
        }
    }

    /**
     * get intersection points of a circle and a polygon / polyline
     *
     * @param poly input polygon / polyline
     * @param C    input circle
     * @return java.util.List<wblut.geom.WB_Point>
     */
    public static List<WB_Point> polylineCircleIntersection(final WB_PolyLine poly, final WB_Circle C) {
        List<WB_Point> result = new ArrayList<>();
        if (poly instanceof WB_Polygon) {
            for (int i = 0; i < poly.getNumberSegments(); i++) {
                WB_Segment S = poly.getSegment(i);
                double a = WB_CoordOp2D.getSqLength2D(
                        S.getEndpoint().xd() - S.getOrigin().xd(),
                        S.getEndpoint().yd() - S.getOrigin().yd()
                );
                double b = 2.0D * (
                        (S.getEndpoint().xd() - S.getOrigin().xd()) * (S.getOrigin().xd() - C.getCenter().xd())
                                + (S.getEndpoint().yd() - S.getOrigin().yd()) * (S.getOrigin().yd() - C.getCenter().yd())
                );
                double c = C.getCenter().xd() * C.getCenter().xd() + C.getCenter().yd() * C.getCenter().yd()
                        + S.getOrigin().xd() * S.getOrigin().xd() + S.getOrigin().yd() * S.getOrigin().yd()
                        - 2.0D * (C.getCenter().xd() * S.getOrigin().xd() + C.getCenter().yd() * S.getOrigin().yd())
                        - C.getRadius() * C.getRadius();
                double delta = b * b - 4.0D * a * c;
                if (delta < -WB_Epsilon.EPSILON) {
                    // no intersection
                } else if (WB_Epsilon.isZero(delta)) {
                    // tangent
                    double u = -0.5D * (b / a);
                    if (u >= 0.0D && u < 1.0D) {
                        double segLength = S.getLength();
                        result.add(S.getPoint(u * segLength));
                    }
                } else {
                    // may have 2 intersections
                    double deltaSq = Math.sqrt(delta);
                    double u1 = 0.5 * (-b + deltaSq) / a;
                    double u2 = 0.5 * (-b - deltaSq) / a;
                    double segLength = S.getLength();
                    if (u1 >= 0.0D && u1 < 1.0D) {
                        result.add(S.getPoint(u1 * segLength));
                    }
                    if (u2 >= 0.0D && u2 < 1.0D) {
                        result.add(S.getPoint(u2 * segLength));
                    }
                }
            }
        } else {
            for (int i = 0; i < poly.getNumberSegments() - 1; i++) {
                WB_Segment S = poly.getSegment(i);
                double a = WB_CoordOp2D.getSqLength2D(
                        S.getEndpoint().xd() - S.getOrigin().xd(),
                        S.getEndpoint().yd() - S.getOrigin().yd()
                );
                double b = 2.0D * (
                        (S.getEndpoint().xd() - S.getOrigin().xd()) * (S.getOrigin().xd() - C.getCenter().xd())
                                + (S.getEndpoint().yd() - S.getOrigin().yd()) * (S.getOrigin().yd() - C.getCenter().yd())
                );
                double c = C.getCenter().xd() * C.getCenter().xd() + C.getCenter().yd() * C.getCenter().yd()
                        + S.getOrigin().xd() * S.getOrigin().xd() + S.getOrigin().yd() * S.getOrigin().yd()
                        - 2.0D * (C.getCenter().xd() * S.getOrigin().xd() + C.getCenter().yd() * S.getOrigin().yd())
                        - C.getRadius() * C.getRadius();
                double delta = b * b - 4.0D * a * c;
                if (delta < -WB_Epsilon.EPSILON) {
                    // no intersection
                } else if (WB_Epsilon.isZero(delta)) {
                    // tangent
                    double u = -0.5D * (b / a);
                    if (u >= 0.0D && u < 1.0D) {
                        double segLength = S.getLength();
                        result.add(S.getPoint(u * segLength));
                    }
                } else {
                    // may have 2 intersections
                    double deltaSq = Math.sqrt(delta);
                    double u1 = 0.5 * (-b + deltaSq) / a;
                    double u2 = 0.5 * (-b - deltaSq) / a;
                    double segLength = S.getLength();
                    if (u1 >= 0.0D && u1 < 1.0D) {
                        result.add(S.getPoint(u1 * segLength));
                    }
                    if (u2 >= 0.0D && u2 < 1.0D) {
                        result.add(S.getPoint(u2 * segLength));
                    }
                }
            }
            WB_Segment S = poly.getSegment(poly.getNumberSegments() - 1);
            double a = WB_CoordOp2D.getSqLength2D(
                    S.getEndpoint().xd() - S.getOrigin().xd(),
                    S.getEndpoint().yd() - S.getOrigin().yd()
            );
            double b = 2.0D * (
                    (S.getEndpoint().xd() - S.getOrigin().xd()) * (S.getOrigin().xd() - C.getCenter().xd())
                            + (S.getEndpoint().yd() - S.getOrigin().yd()) * (S.getOrigin().yd() - C.getCenter().yd())
            );
            double c = C.getCenter().xd() * C.getCenter().xd() + C.getCenter().yd() * C.getCenter().yd()
                    + S.getOrigin().xd() * S.getOrigin().xd() + S.getOrigin().yd() * S.getOrigin().yd()
                    - 2.0D * (C.getCenter().xd() * S.getOrigin().xd() + C.getCenter().yd() * S.getOrigin().yd())
                    - C.getRadius() * C.getRadius();
            double delta = b * b - 4.0D * a * c;
            if (delta < -WB_Epsilon.EPSILON) {
                // no intersection
            } else if (WB_Epsilon.isZero(delta)) {
                // tangent
                double u = -0.5D * (b / a);
                if (u >= 0.0D && u <= 1.0D) {
                    double segLength = S.getLength();
                    result.add(S.getPoint(u * segLength));
                }
            } else {
                // may have 2 intersections
                double deltaSq = Math.sqrt(delta);
                double u1 = 0.5 * (-b + deltaSq) / a;
                double u2 = 0.5 * (-b - deltaSq) / a;
                double segLength = S.getLength();
                if (u1 >= 0.0D && u1 <= 1.0D) {
                    result.add(S.getPoint(u1 * segLength));
                }
                if (u2 >= 0.0D && u2 <= 1.0D) {
                    result.add(S.getPoint(u2 * segLength));
                }
            }
        }
        return result;
    }

    /**
     * extend or trim the segment to polygon boundary
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static ZLine extendSegmentToPolygon(final ZPoint[] segment, final WB_Polygon poly) {
        List<ZPoint> interResult = rayPolygonIntersection2D(segment, poly);
        if (!interResult.isEmpty()) {
            for (int i = 0; i < interResult.size(); i++) {
                if (interResult.get(i).distance(segment[0]) < epsilon) {
                    interResult.remove(i--);
                }
            }
        }
        if (interResult.size() > 1) {
            double[] resultDist = new double[interResult.size()];
            for (int i = 0; i < interResult.size(); i++) {
                resultDist[i] = segment[0].distanceSq(interResult.get(i));
            }
            int[] ascending = ZMath.getArraySortedIndices(resultDist);
            return new ZLine(segment[0], interResult.get(ascending[0]));
        } else if (interResult.size() == 1) {
            return new ZLine(segment[0], interResult.get(0));
        } else {
            return null;
        }
    }

    /**
     * extend or trim the segment to polygon boundary
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static LineString extendSegmentToPolygon(final Vector2D[] segment, final Polygon poly) {
        List<Coordinate> interResult = rayPolygonIntersection2D(segment, poly);
        if (!interResult.isEmpty()) {
            for (int i = 0; i < interResult.size(); i++) {
                if (interResult.get(i).distance(segment[0].toCoordinate()) < epsilon) {
                    interResult.remove(i--);
                }
            }
        }
        if (interResult.size() > 1) {
            double[] resultDist = new double[interResult.size()];
            for (int i = 0; i < interResult.size(); i++) {
                resultDist[i] = segment[0].toCoordinate().distance(interResult.get(i));
            }
            int[] ascending = ZMath.getArraySortedIndices(resultDist);
            return jtsgf.createLineString(new Coordinate[]{
                    segment[0].toCoordinate(), interResult.get(ascending[0])
            });
        } else if (interResult.size() == 1) {
            return jtsgf.createLineString(new Coordinate[]{
                    segment[0].toCoordinate(), interResult.get(0)
            });
        } else {
            return null;
        }
    }

    /**
     * extend the segment both ends to polygon boundary
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static ZLine extendSegmentToPolygonBothSides(final ZPoint[] segment, final WB_Polygon poly) {
        assert WB_GeometryOp.contains2D(segment[0].toWB_Point(), poly) : "input point must be within the polygon";
        List<ZPoint> interResult = linePolygonIntersection2D(segment, poly);
        if (interResult.size() == 2) {
            return new ZLine(interResult.get(0), interResult.get(1));
        } else if (interResult.size() > 2) {
            double[] resultDist = new double[interResult.size()];
            for (int i = 0; i < interResult.size(); i++) {
                resultDist[i] = segment[0].distanceSq(interResult.get(i));
            }
            int[] ascending = ZMath.getArraySortedIndices(resultDist);
            return new ZLine(interResult.get(ascending[0]), interResult.get(ascending[1]));
        } else {
            return null;
        }
    }


    /*-------- distance 2D --------*/

    /**
     * find the closest point in a list of lines
     *
     * @param p     target point
     * @param lines list of lines
     * @return geometry.ZPoint
     */
    public static ZPoint closestPointToLineList(final ZPoint p, final List<? extends ZLine> lines) {
        ZPoint closest = new ZPoint(WB_GeometryOp2D.getClosestPoint2D(p.toWB_Point(), lines.get(0).toWB_Segment()));
        for (int i = 1; i < lines.size(); i++) {
            ZPoint curr = new ZPoint(WB_GeometryOp2D.getClosestPoint2D(p.toWB_Point(), lines.get(i).toWB_Segment()));
            if (p.distanceSq(closest) > p.distanceSq(curr)) {
                closest = curr;
            }
        }
        return closest;
    }

    /**
     * find the closest edge index in a polygon
     *
     * @param p    target point
     * @param poly input poltgon
     * @return int
     */
    public static int closestSegment(final WB_Point p, final WB_Polygon poly) {
        double[] dist = new double[poly.getNumberSegments()];
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            dist[i] = WB_GeometryOp.getDistance2D(p, poly.getSegment(i));
        }
        return ZMath.getMinIndex(dist);
    }

    /**
     * find the closest edge index in a list of segments
     *
     * @param p    target point
     * @param segs input list of segments
     * @return int
     */
    public static int closestSegment(final ZPoint p, final List<? extends ZLine> segs) {
        double[] dist = new double[segs.size()];
        WB_Point pt = p.toWB_Point();
        for (int i = 0; i < segs.size(); i++) {
            dist[i] = WB_GeometryOp.getDistance2D(pt, segs.get(i).toWB_Segment());
        }
        return ZMath.getMinIndex(dist);
    }


    /*-------- geometry relation 2D --------*/

    /**
     * check a point is on a line (float error included)
     *
     * @param p    input point
     * @param line input line
     * @return boolean
     */
    public static boolean pointOnLine(final ZPoint p, final ZLine line) {
        double crossValue = line.getDirection().cross2D(p.sub(line.getPt0()));
        return Math.abs(crossValue) < epsilon;
    }

    /**
     * check a point is on a ray (float error included)
     *
     * @param p   input point
     * @param ray input ray
     * @return boolean
     */
    public static boolean pointOnRay(final ZPoint p, final ZLine ray) {
        double crossValue = ray.getDirection().cross2D(p.sub(ray.getPt0()));
        if (Math.abs(crossValue) < epsilon) {
            double minX = Math.min(ray.getPt0().xd(), ray.getPt1().xd());
            double minY = Math.min(ray.getPt0().yd(), ray.getPt1().yd());
            return minX <= p.xd() && minY <= p.yd();
        } else {
            return false;
        }
    }

    /**
     * check a point is on a segment (float error included)
     *
     * @param p   input point
     * @param seg input segment
     * @return boolean
     */
    public static boolean pointOnSegment(final ZPoint p, final ZLine seg) {
        double crossValue = seg.getDirection().cross2D(p.sub(seg.getPt0()));
        if (Math.abs(crossValue) < epsilon) {
            double minX = Math.min(seg.getPt0().xd(), seg.getPt1().xd());
            double maxX = Math.max(seg.getPt0().xd(), seg.getPt1().xd());
            double minY = Math.min(seg.getPt0().yd(), seg.getPt1().yd());
            double maxY = Math.max(seg.getPt0().yd(), seg.getPt1().yd());
            return minX <= p.xd() && p.xd() <= maxX && minY <= p.yd() && p.yd() <= maxY;
        } else {
            return false;
        }
    }

    /**
     * check a point is on a segment (float error included)
     *
     * @param p   input point
     * @param seg input segment
     * @return boolean
     */
    public static boolean pointOnSegment(final WB_Point p, final WB_Segment seg) {
        double crossValue = WB_CoordOp2D.cross2D(seg.getDirection(), p.sub(seg.getOrigin()));
        if (Math.abs(crossValue) < epsilon) {
            double minX = Math.min(seg.getOrigin().xd(), seg.getEndpoint().xd());
            double maxX = Math.max(seg.getOrigin().xd(), seg.getEndpoint().xd());
            double minY = Math.min(seg.getOrigin().yd(), seg.getEndpoint().yd());
            double maxY = Math.max(seg.getOrigin().yd(), seg.getEndpoint().yd());
            return minX <= p.xd() && p.xd() <= maxX && minY <= p.yd() && p.yd() <= maxY;
        } else {
            return false;
        }
    }

    /**
     * find the point is on which segment (null)
     *
     * @param p    input point
     * @param poly input polygon
     * @return wblut.geom.WB_Segment - segment of input polygon
     */
    public static WB_Segment pointOnWhichWB_Segment(final WB_Point p, final WB_PolyLine poly) {
        WB_Segment result = null;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            WB_Segment segment = poly.getSegment(i);
            if (pointOnSegment(p, segment)) {
                result = segment;
                break;
            }
        }
        return result;
    }

    /**
     * find the point is on which ZEdge (return ZNode)
     *
     * @param p     input point
     * @param edges list of edges
     * @return geometry.ZNode[] - nodes of result edge
     */
    public static ZNode[] pointOnWhichZEdge(final ZPoint p, final List<? extends ZEdge> edges) {
        ZNode[] result = new ZNode[2];
        for (ZEdge edge : edges) {
            if (pointOnSegment(p, edge)) {
                result = edge.getNodes();
            }
        }
        return result;
    }

    /**
     * check if a point is on polygon boundary
     *
     * @param p    input point
     * @param poly input polygon
     * @return boolean
     */
    public static boolean checkPointOnPolygonEdge(final WB_Point p, final WB_PolyLine poly) {
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            if (pointOnSegment(p, poly.getSegment(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * find the point is on which polygon edge (-1)
     *
     * @param p    input point
     * @param poly input polygon
     * @return int[] - indices of result segment
     */
    public static int[] pointOnWhichEdgeIndices(final WB_Point p, final WB_PolyLine poly) {
        int[] result = new int[]{-1, -1};
        if (poly instanceof WB_Polygon) {
            // polygon
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                WB_Segment seg = new WB_Segment(poly.getPoint(i), poly.getPoint(i + 1));
                if (pointOnSegment(p, seg)) {
                    result[0] = i;
                    result[1] = (i + 1) % (poly.getNumberOfPoints() - 1);
                    if (i != poly.getNumberSegments() - 1 && p.getDistance2D(poly.getPoint(result[1])) < epsilon) {
                        // if it's not the last segment and the point is on the end of the segment
                        // then move on to next
                        result[0] = (i + 1) % (poly.getNumberOfPoints() - 1);
                        result[1] = (i + 2) % (poly.getNumberOfPoints() - 1);
                    }
                    break;
                }
            }
        } else {
            // polyline
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                WB_Segment seg = new WB_Segment(poly.getPoint(i), poly.getPoint(i + 1));
                if (pointOnSegment(p, seg)) {
                    result[0] = i;
                    result[1] = i + 1;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * find the point is on which LineString edge (-1)
     *
     * @param p  input point
     * @param ls input LineString
     * @return int[]
     */
    public static int[] pointOnWhichEdgeIndices(final Coordinate p, final LineString ls) {
        int[] result = new int[]{-1, -1};
        for (int i = 0; i < ls.getNumPoints() - 1; i++) {
            ZLine seg = new ZLine(ls.getCoordinateN(i), ls.getCoordinateN(i + 1));
            if (pointOnSegment(new ZPoint(p), seg)) {
                result[0] = i;
                result[1] = i + 1;
                break;
            }
        }
        return result;
    }

    /**
     * find the point is on which Polygon edge (-1)
     *
     * @param p    input point
     * @param poly input Polygon
     * @return int[]
     */
    public static int[] pointOnWhichEdgeIndices(final Coordinate p, final Polygon poly) {
        int[] result = new int[]{-1, -1};

        for (int i = 0; i < poly.getNumPoints() - 1; i++) {
            ZLine seg = new ZLine(poly.getCoordinates()[i], poly.getCoordinates()[i + 1]);
            if (pointOnSegment(new ZPoint(p), seg)) {
                result[0] = i;
                result[1] = (i + 1) % (poly.getNumPoints() - 1);
                // if it's not the last segment and the point is on the end of the segment
                // then move on to next
                if (i != poly.getNumPoints() - 2 && p.distance(poly.getCoordinates()[result[1]]) < epsilon) {
                    result[0] = (i + 1) % (poly.getNumPoints() - 1);
                    result[1] = (i + 2) % (poly.getNumPoints() - 1);
                }
            }
        }
        return result;
    }


    /*-------- boundary methods --------*/

    /**
     * calculate the distance from start point to given point along the polyline
     *
     * @param ls polyline
     * @param p  point on the polyline
     * @return double
     */
    public static double distFromStart(final LineString ls, final Coordinate p) {
        int[] edgeID = pointOnWhichEdgeIndices(p, ls);
        if (edgeID[0] > 0 && edgeID[1] > 0) {
            double dist = 0;
            for (int i = 0; i < edgeID[0]; i++) {
                Coordinate c1 = ls.getCoordinateN(i);
                Coordinate c2 = ls.getCoordinateN(i + 1);
                dist += c1.distance(c2);
            }
            dist += ls.getCoordinateN(edgeID[0]).distance(p);
            return dist;
        } else if (edgeID[0] == 0) {
            return ls.getCoordinateN(0).distance(p);
        } else {
            return 0;
        }
    }

    /**
     * calculate the distance from start point to given point along the polyline
     *
     * @param poly polyline
     * @param p    point on the polyline
     * @return double
     */
    public static double distFromStart(final WB_PolyLine poly, final WB_Point p) {
        int[] edgeID = pointOnWhichEdgeIndices(p, poly);
        if (edgeID[0] > 0 && edgeID[1] > 0) {
            double dist = 0;
            for (int i = 0; i < edgeID[0]; i++) {
                dist += poly.getSegment(i).getLength();
            }
            dist += poly.getPoint(edgeID[0]).getDistance2D(p);
            return dist;
        } else if (edgeID[0] == 0) {
            return poly.getPoint(0).getDistance2D(p);
        } else {
            return 0;
        }
    }

    /**
     * get the point from start point of a LineString by given distance
     *
     * @param ls   input LineString
     * @param dist distance from start
     */
    public static Coordinate pointFromStart(final LineString ls, final double dist) {
        if (dist >= ls.getLength()) {
            return ls.getCoordinateN(ls.getNumPoints() - 1);
        } else {
            double curr_dist = dist;
            Coordinate result = null;
            for (int i = 0; i < ls.getNumPoints() - 1; i++) {
                Coordinate p0 = ls.getCoordinateN(i);
                Coordinate p1 = ls.getCoordinateN(i + 1);
                Vector2D vec = new Vector2D(p0, p1).normalize();

                double segLength = p0.distance(p1);
                if (curr_dist <= segLength) {
                    Vector2D mul = vec.multiply(curr_dist);
                    result = new Coordinate(p0.getX() + mul.getX(), p0.getY() + mul.getY());
                    break;
                }
                curr_dist -= segLength;
            }
            return result;
        }
    }

    /**
     * calculate the distance between two given points along the edge
     *
     * @param p1 point 1 on edge
     * @param p2 point 2 on edge
     * @param ls LineString
     * @return double
     */
    public static double distAlongEdge(final Coordinate p1, final Coordinate p2, final LineString ls) {
        int[] onWhich1 = pointOnWhichEdgeIndices(p1, ls);
        int[] onWhich2 = pointOnWhichEdgeIndices(p2, ls);
        if (onWhich1[0] >= 0 && onWhich1[1] >= 0 && onWhich2[0] >= 0 && onWhich2[1] >= 0) {
            if (onWhich1[0] == onWhich2[0] && onWhich1[1] == onWhich2[1]) {
                // on same edge
                return p1.distance(p2);
            } else {
                // on different edges
                Coordinate forward, backward;
                int fi, bi;
                if (onWhich1[0] > onWhich2[0]) {
                    forward = p1;
                    backward = p2;
                    fi = onWhich1[0];
                    bi = onWhich2[0];
                } else {
                    forward = p2;
                    backward = p1;
                    fi = onWhich2[0];
                    bi = onWhich1[0];
                }
                double dist = 0;
                dist += backward.distance(ls.getCoordinateN(bi + 1));
                for (int i = bi + 1; i < fi; i++) {
                    dist += ls.getCoordinateN(i).distance(ls.getCoordinateN(i + 1));
                }
                dist += ls.getCoordinateN(fi).distance(forward);
                return dist;
            }
        } else {
            System.out.println("point not on edges");
            System.out.println(p1.toString());
            System.out.println(p2.toString());
            return -1;
        }
    }

    /**
     * calculate the distance between two given points along the edge
     * (from p1 to p2, along polygon edge direction)
     *
     * @param p1   point 1 on edge
     * @param p2   point 2 on edge
     * @param poly Polygon
     * @return double
     */
    public static double distAlongEdge(final Coordinate p1, final Coordinate p2, final Polygon poly) {
        int[] onWhich1 = pointOnWhichEdgeIndices(p1, poly);
        int[] onWhich2 = pointOnWhichEdgeIndices(p2, poly);
        if (onWhich1[0] >= 0 && onWhich1[1] >= 0 && onWhich2[0] >= 0 && onWhich2[1] >= 0) {
            if (onWhich1[0] == onWhich2[0] && onWhich1[1] == onWhich2[1]) {
                // on same edge
                Coordinate pStart = poly.getCoordinates()[onWhich1[0]];
                if (p2.distance(pStart) >= p1.distance(pStart)) {
                    return p1.distance(p2);
                } else {
                    return poly.getLength() - p1.distance(p2);
                }
            } else {
                // on different edges
                double dist = 0;
                dist += p1.distance(poly.getCoordinates()[onWhich1[1]]); // start
                dist += p2.distance(poly.getCoordinates()[onWhich2[0]]); // end
                // middle edges
                int index = onWhich1[1];
                while (index != onWhich2[0]) {
                    dist += poly.getCoordinates()[index].distance(poly.getCoordinates()[index + 1]);

                    if (index == poly.getNumPoints() - 2) {
                        index = 0;
                    } else {
                        index++;
                    }
                }
                return dist;
            }
        } else {
            System.out.println("point not on edges");
            System.out.println(p1.toString());
            System.out.println(p2.toString());
            return -1;
        }
    }

    /**
     * giving start point and distance, find two points along polygon / polyline boundary (0 forward, 1 backward)
     *
     * @param origin input point (should be on the edge of polygon / polyline)
     * @param poly   input polygon / polyline
     * @param dist   distance to move
     */
    public static WB_Point[] pointOnEdgeByDist(final WB_Point origin, final WB_PolyLine poly, double dist) {
        // find point on which edge
        int[] onWhich = pointOnWhichEdgeIndices(origin, poly);
        if (onWhich[0] >= 0 && onWhich[1] >= 0) {
            WB_Point forward;
            WB_Point backward;
            int start = onWhich[0];
            int end = onWhich[1];

            // start
            double cur_spanF = dist;
            double cur_spanB = dist;
            WB_Point f1 = origin;
            WB_Point b1 = origin;

            WB_Point f2 = poly.getPoint(end); // forward next
            WB_Point b2 = poly.getPoint(start); // backward next
            double cur_distF = f1.getDistance2D(f2); // distance with forward
            double cur_distB = b1.getDistance2D(b2); // distance with backward

            if (poly instanceof WB_Polygon) {
                // polygon
                forward = f1.add(normalizeWB(f2.sub(f1)).scale(cur_spanF));
                while (cur_spanF > cur_distF) {
                    f1 = f2;
                    end = (end + 1) % (poly.getNumberOfPoints() - 1);
                    f2 = poly.getPoint(end);

                    cur_spanF = cur_spanF - cur_distF;
                    cur_distF = f1.getDistance2D(f2);
                    forward = f1.add(normalizeWB(f2.sub(f1)).scale(cur_spanF));
                }

                backward = b1.add(normalizeWB(b2.sub(b1)).scale(cur_spanB));
                while (cur_spanB > cur_distB) {
                    b1 = b2;
                    start = start - 1;
                    if (start == -1) { // reverse order
                        start = poly.getNumberOfPoints() - 1 - 1;
                    }
                    b2 = poly.getPoint(start);

                    cur_spanB = cur_spanB - cur_distB;
                    cur_distB = b1.getDistance2D(b2);
                    backward = b1.add(normalizeWB(b2.sub(b1)).scale(cur_spanB));
                }
            } else {
                // polyline
                if (poly.getNumberOfPoints() > 2) {
                    forward = f1.add(normalizeWB(f2.sub(f1)).scale(cur_spanF));
                    if (end == poly.getNumberOfPoints() - 1 && cur_spanF > cur_distF) {
                        forward = poly.getPoint(poly.getNumberOfPoints() - 1);
                    } else {
                        while (cur_spanF > cur_distF && end < poly.getNumberOfPoints() - 1) {
                            f1 = f2;
                            end = end + 1;
                            f2 = poly.getPoint(end);

                            cur_spanF = cur_spanF - cur_distF;
                            cur_distF = f1.getDistance2D(f2);
                            forward = f1.add(normalizeWB(f2.sub(f1)).scale(cur_spanF));
                        }
                        if (cur_spanF > cur_distF) {
                            forward = poly.getPoint(poly.getNumberOfPoints() - 1);
                        }
                    }

                    backward = b1.add(normalizeWB(b2.sub(b1)).scale(cur_spanB));
                    if (start == 0 && cur_spanB > cur_distB) {
                        backward = poly.getPoint(0);
                    } else {
                        while (cur_spanB > cur_distB && start > 0) {
                            b1 = b2;
                            start = start - 1;
                            b2 = poly.getPoint(start);

                            cur_spanB = cur_spanB - cur_distB;
                            cur_distB = b1.getDistance2D(b2);
                            backward = b1.add(normalizeWB(b2.sub(b1)).scale(cur_spanB));
                        }
                        if (cur_spanB > cur_distB) {
                            backward = poly.getPoint(0);
                        }
                    }
                } else {
                    if (cur_spanF > cur_distF) {
                        forward = poly.getPoint(poly.getNumberOfPoints() - 1);
                    } else {
                        forward = f1.add(normalizeWB(f2.sub(f1)).scale(cur_spanF));
                    }
                    if (cur_spanB > cur_distB) {
                        backward = poly.getPoint(0);
                    } else {
                        backward = b1.add(normalizeWB(b2.sub(b1)).scale(cur_spanB));
                    }
                }

            }
            return new WB_Point[]{forward, backward};
        } else {
            System.out.println(origin.toString());
            throw new NullPointerException("point not on polygon edges");
        }
    }

    /**
     * giving start point and distance, find two points along LineString (0 forward, 1 backward)
     *
     * @param origin input point (should be on the edge of LineString)
     * @param ls     input LineString
     * @param dist   distance to move
     */
    public static Coordinate[] pointOnEdgeByDist(final Coordinate origin, final LineString ls, double dist) {
        WB_Point o = ZTransform.CoordinateToWB_Point(origin);
        WB_PolyLine pl = ZTransform.LineStringToWB_PolyLine(ls);
        WB_Point[] pts = pointOnEdgeByDist(o, pl, dist);

        Coordinate[] results = new Coordinate[pts.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = ZTransform.WB_CoordToCoordinate(pts[i]);
        }
        return results;
    }

    /**
     * giving start point id, distance and move direction, find the point along LineString
     *
     * @param ls         input LineString
     * @param dist       distance to move
     * @param coordIndex index of the start
     * @param forward    forward or backward
     */
    public static Coordinate pointOnEdgeByDist(final LineString ls, double dist, int coordIndex, boolean forward) {
        assert coordIndex >= 0 && coordIndex < ls.getNumPoints();
        Coordinate result;
        double curr_span = dist;
        if (forward) {
            // move forward
            if (coordIndex == ls.getNumPoints() - 1) {
                result = ls.getCoordinateN(coordIndex);
            } else {
                int next = coordIndex + 1;
                Vector2D f1 = Vector2D.create(ls.getCoordinateN(coordIndex));
                Vector2D f2 = Vector2D.create(ls.getCoordinateN(next)); // forward next
                double cur_distF = f1.distance(f2); // distance with forward

                result = f1.add(f2.subtract(f1).normalize().multiply(curr_span)).toCoordinate();
                if (next == ls.getNumPoints() - 1 && curr_span > cur_distF) {
                    result = ls.getCoordinateN(ls.getNumPoints() - 1);
                } else {
                    while (curr_span > cur_distF && next < ls.getNumPoints() - 1) {
                        f1 = f2;
                        next = next + 1;
                        f2 = Vector2D.create(ls.getCoordinateN(next));

                        curr_span = curr_span - cur_distF;
                        cur_distF = f1.distance(f2);
                        result = f1.add(f2.subtract(f1).normalize().multiply(curr_span)).toCoordinate();
                    }
                    if (curr_span > cur_distF) {
                        result = ls.getCoordinateN(ls.getNumPoints() - 1);
                    }
                }
            }
        } else {
            // move backward
            if (coordIndex == 0) {
                result = ls.getCoordinateN(coordIndex);
            } else {
                int prev = coordIndex - 1;
                Vector2D b1 = Vector2D.create(ls.getCoordinateN(coordIndex));
                Vector2D b2 = Vector2D.create(ls.getCoordinateN(prev)); // forward next
                double cur_distB = b1.distance(b2); // distance with forward

                result = b1.add(b2.subtract(b1).normalize().multiply(curr_span)).toCoordinate();
                if (prev == 0 && curr_span > cur_distB) {
                    result = ls.getCoordinateN(0);
                } else {
                    while (curr_span > cur_distB && prev > 0) {
                        b1 = b2;
                        prev = prev - 1;
                        b2 = Vector2D.create(ls.getCoordinateN(prev));

                        curr_span = curr_span - cur_distB;
                        cur_distB = b1.distance(b2);
                        result = b1.add(b2.subtract(b1).normalize().multiply(curr_span)).toCoordinate();
                    }
                    if (curr_span > cur_distB) {
                        result = ls.getCoordinateN(0);
                    }
                }
            }
        }

        return result;
    }

    /**
     * giving start point and distance, find two points along LineString (0 forward, 1 backward) with edge indices
     *
     * @param origin input point (should be on the edge of LineString)
     * @param ls     input LineString
     * @param dist   distance to move
     */
    public static Map<Coordinate, Integer> pointOnEdgeByDistWithIndex(final Coordinate origin, final LineString ls, double dist) {
        // find point on which edge
        int[] onWhich = pointOnWhichEdgeIndices(origin, ls);
        if (onWhich[0] >= 0 && onWhich[1] >= 0) {
            Coordinate forward;
            Coordinate backward;
            int start = onWhich[0];
            int end = onWhich[1];

            // start
            double cur_spanF = dist;
            double cur_spanB = dist;
            Vector2D f1 = Vector2D.create(origin);
            Vector2D b1 = Vector2D.create(origin);

            Vector2D f2 = Vector2D.create(ls.getCoordinateN(end)); // forward next
            Vector2D b2 = Vector2D.create(ls.getCoordinateN(start)); // backward next
            double cur_distF = f1.distance(f2); // distance with forward
            double cur_distB = b1.distance(b2); // distance with backward

            Map<Coordinate, Integer> result = new LinkedHashMap<>();

            if (ls.getNumPoints() > 2) {
                // the LineString is a polyline
                forward = f1.add(f2.subtract(f1).normalize().multiply(cur_spanF)).toCoordinate();
                if (end == ls.getNumPoints() - 1 && cur_spanF > cur_distF) {
                    forward = ls.getCoordinateN(ls.getNumPoints() - 1);
                } else {
                    while (cur_spanF > cur_distF && end < ls.getNumPoints() - 1) {
                        f1 = f2;
                        end = end + 1;
                        f2 = Vector2D.create(ls.getCoordinateN(end));

                        cur_spanF = cur_spanF - cur_distF;
                        cur_distF = f1.distance(f2);
                        forward = f1.add(f2.subtract(f1).normalize().multiply(cur_spanF)).toCoordinate();
                    }
                    if (cur_spanF > cur_distF) {
                        forward = ls.getCoordinateN(ls.getNumPoints() - 1);
                    }
                }
                result.put(forward, end - 1);

                backward = b1.add(b2.subtract(b1).normalize().multiply(cur_spanB)).toCoordinate();
                if (start == 0 && cur_spanB > cur_distB) {
                    backward = ls.getCoordinateN(0);
                } else {
                    while (cur_spanB > cur_distB && start > 0) {
                        b1 = b2;
                        start = start - 1;
                        b2 = Vector2D.create(ls.getCoordinateN(start));

                        cur_spanB = cur_spanB - cur_distB;
                        cur_distB = b1.distance(b2);
                        backward = b1.add(b2.subtract(b1).normalize().multiply(cur_spanB)).toCoordinate();
                    }
                    if (cur_spanB > cur_distB) {
                        backward = ls.getCoordinateN(0);
                    }
                }
                result.put(backward, start);
            } else {
                // the LineString is a segment
                if (cur_spanF > cur_distF) {
                    forward = ls.getCoordinateN(ls.getNumPoints() - 1);
                } else {
                    forward = f1.add(f2.subtract(f1).normalize().multiply(cur_spanF)).toCoordinate();
                }
                if (cur_spanB > cur_distB) {
                    backward = ls.getCoordinateN(0);
                } else {
                    backward = b1.add(b2.subtract(b1).normalize().multiply(cur_spanB)).toCoordinate();
                }
                result.put(forward, 0);
                result.put(backward, 0);
            }
            return result;
        } else {
            System.out.println(origin.toString());
            throw new NullPointerException("point not on linestring edges");
        }
    }

    /**
     * find the coordinate index with maximum curvature in a LineString
     *
     * @param ls input LineString
     * @return int
     */
    public static int maxCurvatureC(final LineString ls) {
        if (ls.getNumPoints() < 3) {
            return 0;
        } else {
            int result = 1;
            double flag = -Double.MAX_VALUE;
            Coordinate[] coords = ls.getCoordinates();
            for (int i = 1; i < ls.getNumPoints() - 1; i++) {
                Vector2D c0 = Vector2D.create(coords[i]);
                Vector2D c1 = Vector2D.create(coords[i - 1]);
                Vector2D c2 = Vector2D.create(coords[i + 1]);
                Vector2D v1 = c1.subtract(c0).normalize();
                Vector2D v2 = c2.subtract(c0).normalize();
                double dot = v1.dot(v2);
                if (dot > flag) {
                    flag = dot;
                    result = i;
                }
            }
            return result;
        }
    }

    /**
     * find the maximum curvature point by dividing a LineString
     *
     * @param ls      input LineString
     * @param density divide density
     */
    public static Coordinate maxCurvaturePt(final LineString ls, int density) {
        if (density < 3) {
            return ls.getCoordinateN(maxCurvatureC(ls));
        } else {
            int result = 1;
            double flag = -Double.MAX_VALUE;
            List<Coordinate> divide = dividePolyLineEdge(ls, density);
            for (int i = 1; i < divide.size() - 1; i++) {
                Vector2D v1 = Vector2D.create(divide.get(i - 1)).subtract(Vector2D.create(divide.get(i))).normalize();
                Vector2D v2 = Vector2D.create(divide.get(i + 1)).subtract(Vector2D.create(divide.get(i))).normalize();

                double dot = v1.dot(v2);
                if (dot > flag) {
                    flag = dot;
                    result = i;
                }
            }
            return divide.get(result);
        }
    }

    /**
     * core function of divide jts geometries
     *
     * @param coords input Coordinates
     * @param step   step to divide
     * @param type   type of geometry ("LineString""Polygon")
     */
    private static List<Coordinate> divideJts(final Coordinate[] coords, final double step, final String type) {
        // initialize
        Vector2D p1 = Vector2D.create(coords[0]);
        double curr_span = step;
        double curr_dist;

        List<Coordinate> result = new ArrayList<>();
        result.add(p1.toCoordinate());
        for (int i = 1; i < coords.length; i++) {
            Vector2D p2 = Vector2D.create(coords[i]);
            curr_dist = p1.distance(p2);

            while (curr_dist >= curr_span) {
                Vector2D p = p1.add(p2.subtract(p1).normalize().multiply(curr_span));
                result.add(p.toCoordinate());
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }
        if (type.equals("Polygon")) {
            if (result.get(0).distance(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else if (type.equals("LineString")) {
            result.add(coords[coords.length - 1]);
            if (result.get(result.size() - 1).distance(result.get(result.size() - 2)) < epsilon) {
                result.remove(result.size() - 2);
            }
        }

        return result;
    }

    /**
     * core function of divide jts geometries with edge index
     *
     * @param coords input Coordinates
     * @param step   step to divide
     * @param type   type of geometry ("LineString""Polygon")
     */
    private static Map<Coordinate, Integer> divideJtsWithIndex(final Coordinate[] coords, final double step, final String type) {
        // initialize
        Vector2D p1 = Vector2D.create(coords[0]);
        double curr_span = step;
        double curr_dist;

        List<Coordinate> ptList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        ptList.add(p1.toCoordinate());
        idList.add(0);
        for (int i = 1; i < coords.length; i++) {
            Vector2D p2 = Vector2D.create(coords[i]);
            curr_dist = p1.distance(p2);

            while (curr_dist >= curr_span) {
                Vector2D p = p1.add(p2.subtract(p1).normalize().multiply(curr_span));
                ptList.add(p.toCoordinate());
                idList.add(i - 1);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }
        if (type.equals("Polygon")) {
            if (ptList.get(0).distance(ptList.get(ptList.size() - 1)) < epsilon) {
                ptList.remove(ptList.size() - 1);
                idList.remove(idList.size() - 1);
            }
        } else if (type.equals("LineString")) {
            Coordinate end = coords[coords.length - 1];
            if (ptList.get(ptList.size() - 1).distance(end) > epsilon) {
                ptList.add(end);
                idList.add(coords.length - 2);
            }
        }
        Map<Coordinate, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < ptList.size(); i++) {
            result.put(ptList.get(i), idList.get(i));
        }
        return result;
    }

    /**
     * giving step to divide a polygon (Polygon)
     *
     * @param poly input polygon
     * @param step step to divide
     */
    public static List<Coordinate> dividePolygonEdgeByStep(final Polygon poly, final double step) {
        Coordinate[] polyPoints = poly.getCoordinates();
        return divideJts(polyPoints, step, "Polygon");
    }

    /**
     * giving step to divide a LineString (LineString)
     *
     * @param ls   input LineString
     * @param step step to divide
     */
    public static List<Coordinate> dividePolyLineByStep(final LineString ls, final double step) {
        Coordinate[] lsPoints = ls.getCoordinates();
        return divideJts(lsPoints, step, "LineString");
    }

    /**
     * giving step to divide a WB_PolyLine or WB_Polygon  (WB_PolyLine)
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     */
    public static List<WB_Point> dividePolyLineByStep(final WB_PolyLine poly, final double step) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // initialize
        WB_Point start = (WB_Point) polyPoints[0];
        WB_Point end = (WB_Point) polyPoints[polyPoints.length - 1];

        WB_Point p1 = start;
        double curr_span = step;
        double curr_dist;

        List<WB_Point> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            WB_Point p2 = (WB_Point) polyPoints[i];
            curr_dist = p1.getDistance2D(p2);
            while (curr_dist >= curr_span) {
                WB_Point p = p1.add(normalizeWB(p2.sub(p1)).scale(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step;
                curr_dist = p1.getDistance2D(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // close: pt num = seg num
        // open: pt num = seg num + 1
        if (poly instanceof WB_Ring) {
            if (start.getDistance2D(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else {
            if (end.getDistance2D(result.get(result.size() - 1)) > epsilon) {
                result.add(end);
            }
        }

        return result;
    }

    /**
     * giving step to divide a WB_PolyLine or WB_Polygon
     * return a LinkedHashMap of divide point and edge index
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     */
    public static Map<WB_Point, Integer> dividePolyLineByStepWithIndex(final WB_PolyLine poly, final double step) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // initialize
        WB_Point start = (WB_Point) polyPoints[0];
        WB_Point end = (WB_Point) polyPoints[polyPoints.length - 1];

        WB_Point p1 = start;
        double curr_span = step;
        double curr_dist;

        List<WB_Point> pointList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        pointList.add(p1);
        indexList.add(0);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            WB_Point p2 = (WB_Point) polyPoints[i];
            curr_dist = p1.getDistance2D(p2);
            while (curr_dist >= curr_span) {
                WB_Point p = p1.add(normalizeWB(p2.sub(p1)).scale(curr_span));
                pointList.add(p);
                indexList.add(i - 1);
                p1 = p;
                curr_span = step;
                curr_dist = p1.getDistance2D(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // close: pt num = seg num
        // open: pt num = seg num + 1
        if (poly instanceof WB_Ring) {
            if (start.getDistance2D(pointList.get(pointList.size() - 1)) < epsilon) {
                pointList.remove(pointList.size() - 1);
                indexList.remove(indexList.size() - 1);
            }
        } else {
            if (end.getDistance2D(pointList.get(pointList.size() - 1)) > epsilon) {
                pointList.add(end);
                indexList.add(poly.getNumberSegments() - 1);
            }
        }

        // create linkedHashMap
        Map<WB_Point, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < pointList.size(); i++) {
            result.put(pointList.get(i), indexList.get(i));
        }
        return result;
    }

    /**
     * giving step and shaking threshold to divide a WB_PolyLine or WB_Polygon (WB_PolyLine)
     *
     * @param poly  input polyline (polygon)
     * @param step  step to divide
     * @param shake threshold to shake
     */
    public static List<WB_Point> dividePolyLineByRandomStep(final WB_PolyLine poly, final double step, final double shake) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        WB_Point start = (WB_Point) polyPoints[0];
        WB_Point end = (WB_Point) polyPoints[polyPoints.length - 1];

        WB_Point p1 = start;
        double curr_span = step + ZMath.random(step - shake, step + shake);
        double curr_dist;

        List<WB_Point> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            WB_Point p2 = (WB_Point) polyPoints[i];
            curr_dist = p1.getDistance2D(p2);
            while (curr_dist >= curr_span) {
                WB_Point p = p1.add(normalizeWB(p2.sub(p1)).scale(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step + ZMath.random(step - shake, step + shake);
                curr_dist = p1.getDistance2D(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        if (poly instanceof WB_Ring) {
            if (start.getDistance2D(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else {
            if (end.getDistance2D(result.get(result.size() - 1)) > epsilon) {
                result.add(end);
            }
        }

        return result;
    }

    /**
     * giving step threshold to divide a polygon (Polygon)
     *
     * @param poly    input polygon
     * @param maxStep max step to divide
     * @param minStep min step to divide
     */
    public static List<Coordinate> dividePolygonEdgeByThreshold(final Polygon poly, final double maxStep, final double minStep) {
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = poly.getLength() / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new ArrayList<>();
            }
        }
        //        System.out.println("step:" + finalStep);
        return dividePolygonEdgeByStep(poly, finalStep);
    }

    /**
     * giving step threshold to divide a WB_PolyLine or WB_Polygon (WB_PolyLine)
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     */
    public static List<WB_Point> dividePolyLineByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = length / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                System.out.println("cannot generate divide point by this step!");
                return new ArrayList<>();
            }
        }
        return dividePolyLineByStep(poly, finalStep);
    }

    /**
     * giving step threshold to divide a WB_PolyLine or WB_Polygon (WB_PolyLine)
     *
     * @param ls       input LineString
     * @param maxStep  max step to divide
     * @param minStep  min step to divide
     * @param max1min0 maximum result - 1, minimum result - 0
     */
    public static List<Coordinate> dividePolyLineByThreshold(final LineString ls, final double maxStep, final double minStep, final int max1min0) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = ls.getLength();

        double finalStep = 0;
        if (max1min0 == 1) {
            // return the maximum result if exists
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                double curr_step = length / i;
                if (curr_step >= minStep && curr_step <= maxStep) {
                    finalStep = curr_step;
                    break;
                } else if (curr_step < minStep) {
                    System.out.println("cannot generate divide point by this step!");
                    return new ArrayList<>();
                }
            }
        } else {
            // return the minimum result if exists
            boolean flag = false;
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                double curr_step = length / i;
                if (curr_step >= minStep && curr_step <= maxStep) {
                    finalStep = curr_step;
                    flag = true;
                } else if (curr_step < minStep) {
                    if (flag) {
                        break;
                    } else {
                        System.out.println("cannot generate divide point by this step!");
                        return new ArrayList<>();
                    }
                }
            }
        }

        return divideJts(ls.getCoordinates(), finalStep, "LineString");
    }

    /**
     * giving step threshold to divide a WB_PolyLine or WB_Polygon
     * return a LinkedHashMap of divide point and edge index
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     */
    public static Map<WB_Point, Integer> dividePolyLineByThresholdWithIndex(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = length / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new LinkedHashMap<>();
            }
        }
        return dividePolyLineByStepWithIndex(poly, finalStep);
    }

    /**
     * giving step threshold to divide a LineString
     * return a LinkedHashMap of divide point and edge index
     *
     * @param ls      input LineString
     * @param maxStep max step to divide
     * @param minStep min step to divide
     */
    public static Map<Coordinate, Integer> dividePolyLineByThresholdWithIndex(final LineString ls, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        double length = ls.getLength();

        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = length / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new LinkedHashMap<>();
            }
        }
        return divideJtsWithIndex(ls.getCoordinates(), finalStep, "LineString");
    }

    /**
     * giving step threshold to divide each segment of a WB_PolyLine or WB_Polygon
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     */
    public static List<WB_Point> dividePolyLineEachEdgeByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        List<WB_Point> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            WB_Segment segment = poly.getSegment(i);
            double length = segment.getLength();
            double stepOnEdge = 0;
            int divideNum = -1;
            for (int j = 1; j < Integer.MAX_VALUE; j++) {
                double curr_step = length / j;
                if (curr_step >= minStep && curr_step <= maxStep) {
                    stepOnEdge = curr_step;
                    divideNum = j;
                    break;
                } else if (curr_step < minStep) {
                    break;
                }
            }

            if (stepOnEdge != 0) {
                WB_Point curr = (WB_Point) segment.getOrigin();
                result.add(curr);
                for (int j = 0; j < divideNum - 1; j++) {
                    curr = curr.add(segment.getDirection()).scale(stepOnEdge);
                    result.add(curr);
                }
            } else {
                result.add((WB_Point) segment.getOrigin());
            }
        }

        if (!poly.getPoint(0).equals(poly.getPoint(poly.getNumberOfPoints() - 1))) {
            result.add(poly.getPoint(poly.getNumberOfPoints() - 1));
        }
        return result;
    }

    /**
     * giving a divide number, divide equally (LineString)
     *
     * @param ls        input LineString
     * @param divideNum number to divide
     */
    public static List<Coordinate> dividePolyLineEdge(final LineString ls, final int divideNum) {
        double step = ls.getLength() / divideNum;
        return dividePolyLineByStep(ls, step);
    }

    /**
     * giving a divide number, divide equally(WB_PolyLine)
     *
     * @param poly      input polyline (polygon)
     * @param divideNum number to divide
     */
    public static List<WB_Point> dividePolyLineEdge(final WB_PolyLine poly, final int divideNum) {
        // get step
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double step = length / divideNum;

        return dividePolyLineByStep(poly, step);
    }

    /**
     * giving a divide number, divide equally(Polygon)
     *
     * @param poly      input polygon
     * @param divideNum number to divide
     */
    public static List<Coordinate> dividePolygonEdge(final Polygon poly, final int divideNum) {
        double step = poly.getLength() / divideNum;
        return dividePolygonEdgeByStep(poly, step);
    }

    /**
     * giving a divide number, divide equally(Polygon)
     * return a LinkedHashMap of divide point and edge index
     *
     * @param poly      input polygon
     * @param divideNum umber to divide
     */
    public static Map<Coordinate, Integer> dividePolygonWithIndex(final Polygon poly, final int divideNum) {
        double step = poly.getLength() / divideNum;
        return divideJtsWithIndex(poly.getCoordinates(), step, "Polygon");
    }


    /*-------- geometry modifier (WB) --------*/

    /**
     * calculate area from points
     *
     * @param pts input points
     * @return double
     */
    public static double areaFromPoints2D(final double... pts) {
        // must be 2D coordinate (x,y)
        // at least 3 pairs
        if (pts.length % 2 != 0 || pts.length < 6) {
            return 0;
        }

        double area = 0;
        for (int i = 0; i < pts.length - 4; i++) {
            double x1 = pts[i];
            double y1 = pts[i + 1];
            double x2 = pts[i + 2];
            double y2 = pts[i + 3];
            area += (x2 * y1 - x1 * y2);
        }
        double x1 = pts[pts.length - 2];
        double y1 = pts[pts.length - 1];
        double x2 = pts[0];
        double y2 = pts[1];
        area += (x2 * y1 - x1 * y2);

        return 0.5 * Math.abs(area);
    }

    /**
     * calculate area from a series of points, avoiding construct a polygon
     *
     * @param pts a series of points
     * @return double
     */
    public static double areaFromPoints(final WB_Point[] pts) {
        double area = 0;
        for (int i = 0; i < pts.length; i++) {
            WB_Point p = pts[i];
            WB_Point q = pts[(i + 1) % pts.length];
            area += (q.xd() * p.yd() - p.xd() * q.yd());
        }
        return 0.5 * Math.abs(area);
    }

    /**
     * get the whole length of the polyline (replace the method in HE_Mesh)
     *
     * @param poly polyline / polygon
     * @return double
     */
    public static double getPolyLength(final WB_PolyLine poly) {
        double plLength = 0;
        if (poly instanceof WB_Polygon) {
            WB_Polygon polygon = (WB_Polygon) poly;
            if (polygon.getNumberOfHoles() > 0) {
                // shell
                for (int i = 0; i < polygon.getNumberOfShellPoints() - 1; i++) {
                    plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
                }
                // holes
                int[] npc = polygon.getNumberOfPointsPerContour();
                int currNum = npc[0];
                for (int i = 1; i < npc.length; i++) {
                    for (int j = 0; j < npc[i] - 1; j++) {
                        plLength += poly.getPoint(currNum).getDistance2D(poly.getPoint(currNum + 1));
                        currNum++;
                    }
                    currNum++;
                }
            } else {
                for (int i = 0; i < polygon.getNumberOfShellPoints() - 1; i++) {
                    plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
                }
            }
        } else {
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
            }
        }
        return plLength;
    }


    /**
     * given distance. get the point along the polyline (replace the method in HE_Mesh)
     *
     * @param poly polyline
     * @param dist distance
     * @return wblut.geom.WB_Point
     */
    public static WB_Point getPointOnPolyEdge(final WB_PolyLine poly, final double dist) {
        if (dist <= 0) {
            return poly.getPoint(0);
        } else if (dist >= getPolyLength(poly)) {
            return poly.getPoint(poly.getNumberOfPoints() - 1);
        } else {
            double distTemp = dist;
            int finalIndex = 0;

            for (int i = 0; i < poly.getNumberSegments(); i++) {
                double segLength = poly.getSegment(i).getLength();
                if (distTemp > segLength) {
                    distTemp -= segLength;
                } else {
                    finalIndex = i;
                    break;
                }
            }
            WB_Vector v = new WB_Vector(poly.getPoint(finalIndex), poly.getPoint(finalIndex + 1));
            v.normalizeSelf();
            return poly.getPoint(finalIndex).add(v.scale(distTemp));
        }
    }

    /**
     * get the direction of a OBB
     *
     * @param polygon input polygon
     */
    public static WB_Vector obbDir(final WB_Polygon polygon) {
        Polygon rect = (Polygon) MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToPolygon(polygon));
        Coordinate c0 = rect.getCoordinates()[0];
        Coordinate c1 = rect.getCoordinates()[1];
        Coordinate c2 = rect.getCoordinates()[2];

        WB_Vector dir1 = new WB_Vector(
                new double[]{c1.getX(), c1.getY()},
                new double[]{c0.getX(), c0.getY()}
        );
        dir1.normalizeSelf();
        WB_Vector dir2 = new WB_Vector(
                new double[]{c1.getX(), c1.getY()},
                new double[]{c2.getX(), c2.getY()}
        );
        dir2.normalizeSelf();
        return c0.distance(c1) >= c1.distance(c2) ? dir2 : dir1;
    }

    /**
     * reverse the order of a polygon (holes supported)
     *
     * @param original input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon reversePolygon(final WB_Polygon original) {
        if (original.getNumberOfHoles() == 0) {
            WB_Point[] newPoints = new WB_Point[original.getNumberOfPoints()];
            for (int i = 0; i < newPoints.length; i++) {
                newPoints[i] = original.getPoint(newPoints.length - 1 - i);
            }
            return new WB_Polygon(newPoints);
        } else {
            WB_Point[] newExteriorPoints = new WB_Point[original.getNumberOfShellPoints()];
            for (int i = 0; i < original.getNumberOfShellPoints(); i++) {
                newExteriorPoints[i] = original.getPoint(original.getNumberOfShellPoints() - 1 - i);
            }

            final int[] npc = original.getNumberOfPointsPerContour();
            int index = npc[0];
            WB_Point[][] newInteriorPoints = new WB_Point[original.getNumberOfHoles()][];

            for (int i = 0; i < original.getNumberOfHoles(); i++) {
                WB_Point[] newHole = new WB_Point[npc[i + 1]];
                for (int j = 0; j < newHole.length; j++) {
                    newHole[j] = new WB_Point(original.getPoint(newHole.length - 1 - j + index));
                }
                newInteriorPoints[i] = newHole;
                index = index + npc[i + 1];
            }

            return new WB_Polygon(newExteriorPoints, newInteriorPoints);
        }
    }

    /**
     * reverse the order of a WB_PolyLine
     *
     * @param pl original WB_PolyLine
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine reversePolyLine(WB_PolyLine pl) {
        WB_Point[] newOrder = new WB_Point[pl.getNumberOfPoints()];
        for (int i = 0; i < newOrder.length; i++) {
            newOrder[i] = pl.getPoint(newOrder.length - 1 - i);
        }
        return new WB_PolyLine(newOrder);
    }


    /**
     * check if two polygon have same direction
     *
     * @param p1 polygon1
     * @param p2 polygon2
     * @return boolean
     */
    public static boolean isNormalEquals(final WB_Polygon p1, final WB_Polygon p2) {
        return p1.getNormal().equals(p2.getNormal());
    }

    /**
     * make a polygon face up (normal vector is in the z direction) (holes supported)
     *
     * @param polygon input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon polygonFaceUp(final WB_Polygon polygon) {
        if (polygon.getNormal().zd() < 0) {
            return reversePolygon(polygon);
        } else {
            return polygon;
        }
    }

    /**
     * make a polygon face up (normal vector is in the reverse z direction) (holes supported)
     *
     * @param polygon input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon polygonFaceDown(final WB_Polygon polygon) {
        if (polygon.getNormal().zd() > 0) {
            return reversePolygon(polygon);
        } else {
            return polygon;
        }
    }

    /**
     * find the longest segment and the shortest segment in a polygon
     *
     * @param polygon input polygon
     * @return int[]  0 -> longest 1 -> shortest
     */
    public static int[] getLongestAndShortestSegment(final WB_Polygon polygon) {
        WB_Polygon valid = ZTransform.validateWB_Polygon(polygon);
        int maxIndex = 0;
        int minIndex = 0;
        double maxLength = valid.getPoint(0).getSqDistance(valid.getPoint(1));
        double minLength = maxLength;
        for (int i = 1; i < valid.getNumberOfShellPoints() - 1; i++) {
            double currLength = valid.getPoint(i).getSqDistance(valid.getPoint(i + 1));
            if (currLength > maxLength) {
                maxLength = currLength;
                maxIndex = i;
            }
            if (currLength < minLength) {
                minLength = currLength;
                minIndex = i;
            }
        }
        return new int[]{maxIndex, minIndex};
    }

    /**
     * offset one segment of a polygon (input valid, face up polygon)
     *
     * @param poly  input polygon
     * @param index segment index to be offset
     * @param dist  offset distance
     * @return geometry.ZLine
     */
    public static WB_Segment offsetWB_PolygonSegment(final WB_Polygon poly, final int index, final double dist) {
        // make sure polygon's start and end point are coincident
        WB_Polygon polygon = ZTransform.validateWB_Polygon(poly);
        assert index <= polygon.getNumberSegments() && index >= 0 : "index out of polygon point number";

        int next = (index + 1) % polygon.getNumberSegments();
        int prev = (index + polygon.getNumberSegments() - 1) % polygon.getNumberSegments();

        WB_Point p1 = (WB_Point) polygon.getSegment(index).getOrigin();
        WB_Vector v1 = new WB_Vector(polygon.getSegment(prev).getEndpoint(), polygon.getSegment(prev).getOrigin());
        WB_Vector v2 = new WB_Vector(p1, polygon.getSegment(index).getEndpoint());
        WB_Vector bisector1 = getAngleBisectorOrdered(v1, v2);
        WB_Point point1 = p1.add(bisector1.scale(dist / Math.abs(WB_CoordOp2D.cross2D(normalizeWB(v1), bisector1))));

        WB_Point p2 = (WB_Point) polygon.getSegment(index).getEndpoint();
        WB_Vector v3 = new WB_Vector(p2, polygon.getSegment(index).getOrigin());
        WB_Vector v4 = new WB_Vector(polygon.getSegment(next).getOrigin(), polygon.getSegment(next).getEndpoint());
        WB_Vector bisector2 = getAngleBisectorOrdered(v3, v4);
        WB_Point point2 = p2.add(bisector2.scale(dist / Math.abs(WB_CoordOp2D.cross2D(normalizeWB(v3), bisector2))));

        return new WB_Segment(point1, point2);
    }

    /**
     * offset several segments of a polygon (input valid, face up polygon)
     * return polyline or polygon
     *
     * @param poly  input polygon
     * @param index segment indices to be offset
     * @param dist  offset distance
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine offsetWB_PolygonSegments(final WB_Polygon poly, final int[] index, final double dist) {
        // make sure polygon's start and end point are coincident
        WB_Polygon polygon = ZTransform.validateWB_Polygon(poly);

        WB_Point[] linePoints = new WB_Point[index.length + 1];
        for (int i = 0; i < index.length; i++) {
            int prev = (index[i] + polygon.getNumberSegments() - 1) % polygon.getNumberSegments();

            WB_Point p = (WB_Point) polygon.getSegment(index[i]).getOrigin();

            WB_Vector v1 = new WB_Vector(polygon.getSegment(prev).getEndpoint(), polygon.getSegment(prev).getOrigin());
            WB_Vector v2 = new WB_Vector(p, polygon.getSegment(index[i]).getEndpoint());
            WB_Vector bisector1 = getAngleBisectorOrdered(v1, v2);
            WB_Point point1 = p.add(bisector1.scale(dist / Math.abs(WB_CoordOp2D.cross2D(normalizeWB(v1), bisector1))));

            linePoints[i] = point1;
        }

        int next = (index[index.length - 1] + 1) % polygon.getNumberSegments();
        WB_Point p = (WB_Point) polygon.getSegment(index[index.length - 1]).getEndpoint();
        WB_Vector v3 = new WB_Vector(polygon.getSegment(index[index.length - 1]).getEndpoint(), polygon.getSegment(index[index.length - 1]).getOrigin());
        WB_Vector v4 = new WB_Vector(polygon.getSegment(next).getOrigin(), polygon.getSegment(next).getEndpoint());
        WB_Vector bisector2 = getAngleBisectorOrdered(v3, v4);
        WB_Point point2 = p.add(bisector2.scale(dist / Math.abs(WB_CoordOp2D.cross2D(normalizeWB(v3), bisector2))));
        linePoints[linePoints.length - 1] = point2;

        if (linePoints[0].equals(linePoints[linePoints.length - 1])) {
            return new WB_Polygon(linePoints);
        } else {
            return new WB_PolyLine(linePoints);
        }
    }

    /*-------- geometry modifier (jts) --------*/

    /**
     * get the direction of a OBB
     *
     * @param polygon input polygon
     */
    public static Vector2D obbDir(final Polygon polygon) {
        Polygon rect = (Polygon) MinimumDiameter.getMinimumRectangle(polygon);
        Coordinate c0 = rect.getCoordinates()[0];
        Coordinate c1 = rect.getCoordinates()[1];
        Coordinate c2 = rect.getCoordinates()[2];

        Vector2D dir1 = new Vector2D(c1, c0).normalize();
        Vector2D dir2 = new Vector2D(c1, c2).normalize();

        return c0.distance(c1) >= c1.distance(c2) ? dir2 : dir1;
    }

    /**
     * reverse the order of a LineString
     *
     * @param ls original LineString
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString reverseLineString(LineString ls) {
        Coordinate[] newOrder = new Coordinate[ls.getNumPoints()];
        for (int i = 0; i < newOrder.length; i++) {
            newOrder[i] = ls.getCoordinateN(newOrder.length - 1 - i);
        }
        return jtsgf.createLineString(newOrder);
    }


    /**
     * smooth LineString by connecting divided points
     *
     * @param ls        input LineString
     * @param divideNum divide num for each edge
     * @param times     times to smooth
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString smoothLineString(final LineString ls, final int divideNum, final int times) {
        if (divideNum < 2 || times < 1 || ls.getCoordinates().length < 3) {
            return ls;
        } else if (divideNum == 2) {
            // half divide
            LineString temp = ls;
            if (ls.isClosed()) {
                for (int i = 0; i < times; i++) {
                    List<Coordinate> coords = new ArrayList<>();
                    for (int j = 0; j < temp.getCoordinates().length - 1; j++) {
                        Coordinate start = temp.getCoordinateN(j);
                        Coordinate end = temp.getCoordinateN((j + 1) % (temp.getCoordinates().length - 1));
                        Coordinate center = new Coordinate(
                                (start.getX() + end.getX()) / 2,
                                (start.getY() + end.getY()) / 2
                        );
                        coords.add(center);
                    }
                    coords.add(coords.get(0));
                    temp = ZFactory.createLineStringFromList(coords);
                }
            } else {
                for (int i = 0; i < times; i++) {
                    List<Coordinate> coords = new ArrayList<>();
                    coords.add(temp.getCoordinateN(0));
                    for (int j = 0; j < temp.getCoordinates().length - 1; j++) {
                        Coordinate start = temp.getCoordinateN(j);
                        Coordinate end = temp.getCoordinateN(j + 1);
                        Coordinate center = new Coordinate(
                                (start.getX() + end.getX()) / 2,
                                (start.getY() + end.getY()) / 2
                        );
                        coords.add(center);
                    }
                    coords.add(temp.getCoordinateN(temp.getCoordinates().length - 1));
                    temp = ZFactory.createLineStringFromList(coords);
                }
            }
            return temp;
        } else {
            LineString temp = ls;
            if (ls.isClosed()) {
                for (int i = 0; i < times; i++) {
                    List<Coordinate> coords = new ArrayList<>();
                    for (int j = 0; j < temp.getCoordinates().length - 1; j++) {
                        Vector2D start = Vector2D.create(temp.getCoordinateN(j));
                        Vector2D end = Vector2D.create(temp.getCoordinateN((j + 1) % (temp.getCoordinates().length - 1)));
                        Vector2D segVec = end.subtract(start).normalize();
                        double step = start.distance(end) / divideNum;

                        coords.add(start.add(segVec.multiply(step)).toCoordinate());
                        coords.add(end.add(segVec.multiply(-1 * step)).toCoordinate());
                    }
                    coords.add(coords.get(0));
                    temp = ZFactory.createLineStringFromList(coords);
                }
            } else {
                for (int i = 0; i < times; i++) {
                    List<Coordinate> coords = new ArrayList<>();
                    coords.add(temp.getCoordinateN(0));

                    Vector2D startA = Vector2D.create(temp.getCoordinateN(0));
                    Vector2D endA = Vector2D.create(temp.getCoordinateN(1));
                    Vector2D segVecA = endA.subtract(startA).normalize();
                    double stepA = startA.distance(endA) / divideNum;
                    coords.add(endA.add(segVecA.multiply(-1 * stepA)).toCoordinate());

                    for (int j = 1; j < temp.getCoordinates().length - 2; j++) {
                        Vector2D start = Vector2D.create(temp.getCoordinateN(j));
                        Vector2D end = Vector2D.create(temp.getCoordinateN(j + 1));
                        Vector2D segVec = end.subtract(start).normalize();
                        double step = start.distance(end) / divideNum;

                        coords.add(start.add(segVec.multiply(step)).toCoordinate());
                        coords.add(end.add(segVec.multiply(-1 * step)).toCoordinate());
                    }

                    Vector2D startB = Vector2D.create(temp.getCoordinateN(0));
                    Vector2D endB = Vector2D.create(temp.getCoordinateN(1));
                    Vector2D segVecB = endB.subtract(startB).normalize();
                    double stepB = startB.distance(endB) / divideNum;
                    coords.add(endB.add(segVecB.multiply(-1 * stepB)).toCoordinate());

                    coords.add(temp.getCoordinateN(temp.getCoordinates().length - 1));
                    temp = ZFactory.createLineStringFromList(coords);
                }
            }
            return temp;
        }
    }

    /**
     * smooth polygon edge by connecting divided points
     *
     * @param polygon   input polygon
     * @param divideNum divide num for each edge
     * @param times     times to smooth
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon smoothPolygon(final Polygon polygon, final int divideNum, final int times) {
        if (divideNum < 2 || times < 1) {
            return polygon;
        } else {
            if (polygon.getNumInteriorRing() > 0) {
                LineString ex = smoothLineString(polygon.getExteriorRing(), divideNum, times);
                LineString[] in = new LineString[polygon.getNumInteriorRing()];
                for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                    in[i] = smoothLineString(polygon.getInteriorRingN(i), divideNum, times);
                }
                return ZFactory.createPolygonWithHoles(ex, in);
            } else {
                Polygon temp = polygon;
                if (divideNum == 2) {
                    // half divide
                    for (int i = 0; i < times; i++) {
                        List<Coordinate> coords = new ArrayList<>();
                        for (int j = 0; j < temp.getCoordinates().length - 1; j++) {
                            Coordinate start = temp.getCoordinates()[j];
                            Coordinate end = temp.getCoordinates()[(j + 1) % (temp.getCoordinates().length - 1)];
                            Coordinate center = new Coordinate(
                                    (start.getX() + end.getX()) / 2,
                                    (start.getY() + end.getY()) / 2
                            );
                            coords.add(center);
                        }
                        coords.add(coords.get(0));
                        temp = ZFactory.createPolygonFromList(coords);
                    }
                } else {
                    for (int i = 0; i < times; i++) {
                        List<Coordinate> coords = new ArrayList<>();
                        for (int j = 0; j < temp.getCoordinates().length - 1; j++) {
                            Vector2D start = Vector2D.create(temp.getCoordinates()[j]);
                            Vector2D end = Vector2D.create(temp.getCoordinates()[(j + 1) % (temp.getCoordinates().length - 1)]);
                            Vector2D segVec = end.subtract(start).normalize();
                            double step = start.distance(end) / divideNum;

                            coords.add(start.add(segVec.multiply(step)).toCoordinate());
                            coords.add(end.add(segVec.multiply(-1 * step)).toCoordinate());
                        }
                        coords.add(coords.get(0));
                        temp = ZFactory.createPolygonFromList(coords);
                    }
                }
                return temp;
            }
        }
    }

    /**
     * make polygon corner round
     *
     * @param polygon input polygon
     * @param r       radius
     * @param segNum  number of subdivision
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon roundPolygon(final Polygon polygon, double r, int segNum) {
        // exterior
        LineString exterior = polygon.getExteriorRing();
        List<Coordinate> newCoordsE = roundPolygonCore(r, segNum, exterior);
        newCoordsE.add(newCoordsE.get(0));

        // hole or not
        if (polygon.getNumInteriorRing() > 0) {
            LinearRing shell = ZFactory.createLinearRingFromList(newCoordsE);
            // interior
            LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                LineString hole = polygon.getInteriorRingN(i);
                List<Coordinate> newCoordsI = roundPolygonCore(r, segNum, hole);
                newCoordsI.add(newCoordsI.get(0));
                holes[i] = ZFactory.createLinearRingFromList(newCoordsI);
            }
            return jtsgf.createPolygon(shell, holes);
        } else {
            return ZFactory.createPolygonFromList(newCoordsE);
        }
    }

    /**
     * core function to round a polygon
     *
     * @param r      radius
     * @param segNum number of subdivision
     * @param oriLs  original LineString
     * @return void
     */
    private static List<Coordinate> roundPolygonCore(double r, int segNum, LineString oriLs) {
        List<Coordinate> coords = new ArrayList<>();
        for (int j = 0; j < oriLs.getNumPoints() - 1; j++) {
            Coordinate base = oriLs.getCoordinates()[j];
            Vector2D p0 = Vector2D.create(base);
            Vector2D p1 = Vector2D.create(oriLs.getCoordinates()[(j + oriLs.getNumPoints() - 1 - 1) % (oriLs.getNumPoints() - 1)]);
            Vector2D p2 = Vector2D.create(oriLs.getCoordinates()[(j + 1) % (oriLs.getNumPoints() - 1)]);

            Vector2D v1 = p1.subtract(p0).normalize();
            Vector2D v2 = p2.subtract(p0).normalize();
            double dot = v1.dot(v2);
            double halfTan = ZMath.halfTan(dot, false);
            double d = r / halfTan;

            double cross = cross2D(v1, v2);
            if (cross == 0) {
                // 0 or 180
                coords.add(base);
            } else {
                if (p0.distance(p1) > d * 2 && p0.distance(p2) > d * 2) {
                    // edge length should be enough to round
                    double halfSin = ZMath.halfSin(dot);
                    Vector2D bisector = v1.add(v2).normalize();
                    Vector2D arcCenter = p0.add(bisector.multiply(r / halfSin));

                    Vector2D arcStart = p0.add(v1.multiply(d));
                    Vector2D arcEnd = p0.add(v2.multiply(d));

                    Coordinate[] arc;
                    if (cross < 0) {
                        arc = ZFactory.createArc(arcCenter.toCoordinate(), arcStart.toCoordinate(), arcEnd.toCoordinate(), segNum, true);
                    } else {
                        arc = ZFactory.createArc(arcCenter.toCoordinate(), arcStart.toCoordinate(), arcEnd.toCoordinate(), segNum, false);
                    }
                    coords.addAll(Arrays.asList(arc));
                } else {
                    coords.add(base);
                }
            }
        }
        return coords;
    }

    /*-------- 3D methods --------*/

    /**
     * extrude a WB_Polygon and create a HE_Mesh
     *
     * @param base        base WB_Polygon
     * @param extrudeSize extrude size
     * @return wblut.hemesh.HE_Mesh
     */
    public static HE_Mesh extrudePolygon(WB_Polygon base, double extrudeSize) {
        if (base == null) return null;

        // use a face-down polygon as the base face of the mesh
        double signedArea = base.getSignedArea();
        WB_Polygon basePoly;
        WB_Polygon basePolyRev;
        if (signedArea > 0) {
            basePoly = ZTransform.validateWB_Polygon(reversePolygon(base));
            basePolyRev = ZTransform.validateWB_Polygon(base);
        } else {
            basePoly = ZTransform.validateWB_Polygon(base);
            basePolyRev = ZTransform.validateWB_Polygon(reversePolygon(base));
        }

        // mesh creator
        HEC_FromPolygons creator = new HEC_FromPolygons();
        List<WB_Polygon> meshPolyFaceList = new ArrayList<>();

        // base
        meshPolyFaceList.add(ZFactory.copySimple_WB_Polygon(basePoly));
        // side
        for (int i = 0; i < basePoly.getNumberOfPoints() - 1; i++) {
            WB_Point p0 = basePoly.getPoint(i + 1).copy();
            WB_Point p1 = basePoly.getPoint(i).copy();
            WB_Point p2 = p1.add(0, 0, extrudeSize);
            WB_Point p3 = p0.add(0, 0, extrudeSize);

            WB_Polygon sideFace = wbgf.createSimplePolygon(p0, p1, p2, p3, p0);
            meshPolyFaceList.add(sideFace);
        }
        // top
        WB_Point[] topFacePts = new WB_Point[basePolyRev.getNumberOfPoints()];
        for (int i = 0; i < basePolyRev.getNumberOfPoints(); i++) {
            WB_Point _p = basePolyRev.getPoint(i);
            WB_Point p = new WB_Point(_p.xd(), _p.yd(), _p.zd() + extrudeSize);
            topFacePts[i] = p;
        }
        WB_Polygon topFace = wbgf.createSimplePolygon(topFacePts);
        meshPolyFaceList.add(topFace);

        creator.setPolygons(meshPolyFaceList);
        return new HE_Mesh(creator);
    }

    /**
     * extrude a WB_PolyLine with thickness and create a HE_Mesh (default buffer style: BufferParameters.CAP_FLAT)
     *
     * @param pl          base WB_PolyLine
     * @param thickness   thickness of the polyline
     * @param extrudeSize extrude size
     * @return wblut.hemesh.HE_Mesh
     */
    public static HE_Mesh extrudePolylineWithThickness(WB_PolyLine pl, double thickness, double extrudeSize) {
        LineString ls = ZTransform.WB_PolyLineToLineString(pl);
        BufferOp bufferOp = new BufferOp(ls);
        bufferOp.setEndCapStyle(BufferParameters.CAP_FLAT);
        bufferOp.setQuadrantSegments(1);
        Polygon buffer = (Polygon) bufferOp.getResultGeometry(thickness);
        WB_Polygon poly = ZTransform.PolygonToWB_Polygon(buffer);
        return extrudePolygon(poly, extrudeSize);
    }

    /*-------- other methods --------*/

    /**
     * get the center of a series of points
     *
     * @param pts points
     * @return wblut.geom.WB_Point
     */
    public static WB_Point centerFromPoints(WB_Point[] pts) {
        int length = pts.length;
        double x = 0, y = 0, z = 0;
        for (WB_Point pt : pts) {
            x += pt.xd();
            y += pt.yd();
            z += pt.zd();
        }
        return new WB_Point(x / length, y / length, z / length);
    }

    /**
     * set jts precision model (FLOAT, FLOAT_SINGLE, FIXED)
     *
     * @param geometry geometry to be applied
     * @param pm       precision model
     */
    public static void applyJtsPrecisionModel(final Geometry geometry, final PrecisionModel pm) {
        Coordinate[] coordinates = geometry.getCoordinates();
        for (Coordinate c : coordinates) {
            pm.makePrecise(c);
        }
    }

    // FIXME: 2021/4/29 bug

    /**
     * halving a OBB
     *
     * @param obb input OBB rectangle
     * @return org.locationtech.jts.geom.Polygon[]
     */
    public static Polygon[] halvingOBB(final Polygon obb) {
        assert obb.getCoordinates().length == 5 : "not a valid rectangle";
        Polygon[] result = new Polygon[2];
        double l1 = obb.getCoordinates()[0].distance(obb.getCoordinates()[1]);
        double l2 = obb.getCoordinates()[1].distance(obb.getCoordinates()[2]);

        Coordinate mid1;
        Coordinate mid2;
        if (l1 >= l2) {
            mid1 = new Coordinate(
                    (obb.getCoordinates()[0].getX() + obb.getCoordinates()[1].getX()) * 0.5,
                    (obb.getCoordinates()[0].getY() + obb.getCoordinates()[1].getY()) * 0.5,
                    (obb.getCoordinates()[0].getZ() + obb.getCoordinates()[1].getZ()) * 0.5
            );
            mid2 = new Coordinate(
                    (obb.getCoordinates()[2].getX() + obb.getCoordinates()[3].getX()) * 0.5,
                    (obb.getCoordinates()[2].getY() + obb.getCoordinates()[3].getY()) * 0.5,
                    (obb.getCoordinates()[2].getZ() + obb.getCoordinates()[3].getZ()) * 0.5
            );
            result[0] = jtsgf.createPolygon(
                    new Coordinate[]{obb.getCoordinates()[0], mid1, mid2, obb.getCoordinates()[3], obb.getCoordinates()[0]}
            );
            result[1] = jtsgf.createPolygon(
                    new Coordinate[]{mid1, obb.getCoordinates()[1], obb.getCoordinates()[2], mid2, mid1}
            );
        } else {
            mid1 = new Coordinate(
                    (obb.getCoordinates()[1].getX() + obb.getCoordinates()[2].getX()) * 0.5,
                    (obb.getCoordinates()[1].getY() + obb.getCoordinates()[2].getY()) * 0.5,
                    (obb.getCoordinates()[1].getZ() + obb.getCoordinates()[2].getZ()) * 0.5
            );
            mid2 = new Coordinate(
                    (obb.getCoordinates()[3].getX() + obb.getCoordinates()[4].getX()) * 0.5,
                    (obb.getCoordinates()[3].getY() + obb.getCoordinates()[4].getY()) * 0.5,
                    (obb.getCoordinates()[3].getZ() + obb.getCoordinates()[4].getZ()) * 0.5
            );
            result[0] = jtsgf.createPolygon(
                    new Coordinate[]{obb.getCoordinates()[0], obb.getCoordinates()[1], mid1, mid2, obb.getCoordinates()[0]}
            );
            result[1] = jtsgf.createPolygon(
                    new Coordinate[]{mid2, mid1, obb.getCoordinates()[2], obb.getCoordinates()[3], mid2}
            );
        }
        return result;
    }

    /**
     * get a simple OBB tree of a geometry
     *
     * @param geo   input geometry
     * @param count tree levels count
     * @return java.util.List<org.locationtech.jts.geom.Polygon>
     */
    public static List<Polygon> performOBBTree(final Geometry geo, final int count) {
        List<Geometry> geos = new ArrayList<>();
        geos.add(geo);

        List<Polygon> result = new ArrayList<>();
        result.add((Polygon) MinimumDiameter.getMinimumRectangle(geo));

        if (count <= 0) {
            return result;
        } else {
            for (int i = 0; i < count; i++) {
                result.clear();
                List<Geometry> currGeos = new ArrayList<>();
                for (Geometry geometry : geos) {
                    Geometry rect = MinimumDiameter.getMinimumRectangle(geometry);
                    Polygon halfOBB = halvingOBB((Polygon) rect)[0];
                    Geometry half1 = geometry.intersection(halfOBB); // halfOBB cover
                    Geometry half2 = geometry.difference(halfOBB); // the other half

                    Geometry rect1 = MinimumDiameter.getMinimumRectangle(half1);
                    Geometry rect2 = MinimumDiameter.getMinimumRectangle(half2);
//                    System.out.println(Arrays.toString(half2.getCoordinates()));

                    currGeos.add(half1);
                    currGeos.add(half2);
                    result.add((Polygon) rect1);
                    result.add((Polygon) rect2);
                }
                geos.clear();
                geos.addAll(currGeos);
            }
        }

        return result;
    }

    /**
     * make z ordinate to 0 if NaN
     *
     * @param coords original Coordinates
     * @return org.locationtech.jts.geom.Coordinate[]
     */
    public static Coordinate[] filterNaN(Coordinate... coords) {
        Coordinate[] result = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            double z = coords[i].getZ();
            result[i] = new Coordinate(coords[i].getX(), coords[i].getY(), Double.isNaN(z) ? 0 : z);
        }
        return result;
    }

}