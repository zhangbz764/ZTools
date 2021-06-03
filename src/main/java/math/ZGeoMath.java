package math;

import basicGeometry.*;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import transform.ZTransform;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * geometry math tools
 * <p>
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/9/29
 * @time 15:38
 * <p>
 * #### vector angle
 * get angle bisector by the order of v0 -> v1 (reflex angle includes)
 * sort a list of vectors by polar coordinates (return indices)
 * sort a list of vectors by polar coordinates (return vectors or normalized vectors)
 * find all concave points(or indices) in a polygon (WB_Polygon)
 * find the nearest vector in a list of vector
 *
 * #### intersection 2D
 * check if two WB_Segments are intersecting
 * check a ray and a segment are intersecting
 * check a line and a segment are intersecting
 * check a ray and a polyline are intersecting
 * get intersection points: line, ray or segment
 * get intersection points of a ray / line and a polygon ([--) for each edge)
 * get intersection points of a ray and a polygon (return indices by distance order)
 * get intersection points of a segment and a polyline ([--) for each edge, [--] for the last one)
 * extend or trim the segment to polygon boundary
 * extend the segment both ends to polygon boundary
 *
 * #### distance 2D
 * find the closest point in a list of lines
 * find the closest edge index in a polygon / list of segments
 * #### geometry relation 2D
 * check a point is on a line / ray / segment (float error included)
 * check if a point is on polygon boundary
 * find the point is on which segment (return WB_Segment, ZLine, or indices of two points)
 * find the point is within which polygon(-1)
 *
 * #### boundary methods
 * giving start point and distance, find two points along polygon boundary (0 forward, 1 backward)
 * giving step to split a polygon
 * giving step and shaking threshold to split a WB_PolyLine or WB_Polygon (WB_PolyLine)
 * giving step to split a WB_PolyLine or WB_Polygon, return a LinkedHashMap of split point and edge index
 * giving step threshold to split a polygon / polyline
 * giving step threshold to split a WB_PolyLine or WB_Polygon, return a LinkedHashMap of split point and edge index
 * giving step threshold to split each segment of a WB_PolyLine or WB_Polygon
 * giving a split number, split a polygon or a polyline equally
 *
 * #### polygon tools
 * get the direction of a OBB
 * reverse the order of a polygon (holes supported)
 * check if two polygon have same direction
 * make a polygon face up or down (holes supported)
 * find the longest segment and the shortest segment in a polygon
 * offset one segment of a polygon (input valid, face up polygon)
 * offset several segments of a polygon (input valid, face up polygon), return polyline or polygon
 *
 * #### other methods
 * set jts precision model (FLOAT, FLOAT_SINGLE, FIXED)
 * halving a OBB
 * get a simple OBB tree of a geometry
 * <p>
 */
public final class ZGeoMath {
    private static final GeometryFactory gf = new GeometryFactory();
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();

    public static final double epsilon = 0.00000001;

    /*-------- vector angle --------*/

    /**
     * get angle bisector by the order of v0 -> v1 (reflex angle includes)
     *
     * @param v0 first vector
     * @param v1 second vector
     * @return geometry.ZPoint
     */
    public static ZPoint getAngleBisectorOrdered(final ZPoint v0, final ZPoint v1) {
        if (v0.cross2D(v1) > 0) {
            return v0.normalize().add(v1.normalize()).normalize();
        } else if (v0.cross2D(v1) < 0) {
            return v0.normalize().add(v1.normalize()).normalize().scaleTo(-1);
        } else {
            if (v0.dot2D(v1) > 0) {
                return v0.normalize();
            } else {
                ZPoint perpendicular = new ZPoint(v0.yd(), -v0.xd());
                if (!(v0.cross2D(perpendicular) > 0)) {
                    perpendicular.scaleSelf(-1);
                }
                return perpendicular.normalize();
            }
        }
    }

    /**
     * find all concave points in a polygon (WB_Polygon)
     *
     * @param polygon input WB_Polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> getConcavePoints(final WB_Polygon polygon) {
        List<ZPoint> concavePoints = new ArrayList<>();
        WB_Polygon faceUp = polygonFaceUp(polygon); // 保证正向
        for (int i = 1; i < faceUp.getNumberOfPoints(); i++) {
            ZPoint prev = new ZPoint(faceUp.getPoint(i - 1).sub(faceUp.getPoint(i)));
            ZPoint next = new ZPoint(faceUp.getPoint((i + 1) % (faceUp.getNumberOfPoints() - 1)).sub(faceUp.getPoint(i)));
            double crossValue = next.cross2D(prev);
            if (crossValue < 0) {
                concavePoints.add(new ZPoint(faceUp.getPoint(i)));
            }
        }
        return concavePoints;
    }

    /**
     * find all concave points in a polygon (jts Polygon)
     *
     * @param polygon input jts Polygon
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> getConcavePoints(final Polygon polygon) {
        WB_Polygon wbPolygon = ZTransform.PolygonToWB_Polygon(polygon);
        return getConcavePoints(wbPolygon);
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
    public static int[] sortPolarAngleIndices(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
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
     * @return geometry.ZPoint[]
     */
    public static ZPoint[] sortPolarAngle(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        ZPoint[] sorted = new ZPoint[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = vectors.get(newOrder[i]);
        }
        return sorted;
    }

    /**
     * sort a list of vectors by polar coordinates, return new list of normalized vectors
     *
     * @param vectors vector list to be sorted
     * @return geometry.ZPoint[] - normalized
     */
    public static ZPoint[] sortPolarAngleNor(final List<? extends ZPoint> vectors) {
        assert vectors.size() > 0 : "input list must at least include 1 vector";
        int[] newOrder = sortPolarAngleIndices(vectors);
        ZPoint[] sorted = new ZPoint[vectors.size()];
        for (int i = 0; i < newOrder.length; i++) {
            sorted[i] = vectors.get(newOrder[i]).normalize();
        }
        return sorted;
    }

    /**
     * find the nearest vector in a list of vector
     *
     * @param target target vector
     * @param other  vector list
     * @return geometry.ZPoint
     */
    public static ZPoint getClosestVec(final ZPoint target, final List<? extends ZPoint> other) {
        assert other != null && other.size() != 0 : "invalid input vectors";
        double[] dotValue = new double[other.size()];
        for (int i = 0; i < other.size(); i++) {
            dotValue[i] = target.normalize().dot2D(other.get(i).normalize());
        }
        int maxIndex = ZMath.getMaxIndex(dotValue);
        return other.get(maxIndex);
    }


    /*-------- intersection 2D --------*/

    // TODO: 2021/1/4 intersection check

    /**
     * check if two WB_Segments are intersecting
     *
     * @param seg0 first segment
     * @param seg1 second segment
     * @return boolean
     */
    public static boolean checkWB_SegmentIntersect(final WB_Segment seg0, final WB_Segment seg1) {
        return WB_GeometryOp2D.checkIntersection2DProper(seg0.getOrigin(), seg0.getEndpoint(), seg1.getOrigin(), seg1.getEndpoint());
    }

    /**
     * check a ray and a segment are intersecting
     *
     * @param ray ray {point P, direction d}
     * @param seg segment {point P, direction d}
     * @return boolean
     */
    public static boolean checkRaySegmentIntersection(final ZPoint[] ray, final ZPoint[] seg) {
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
    public static boolean checkLineSegmentIntersection(final ZPoint[] line, final ZPoint[] seg) {
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
     * check a ray and a polyline are intersecting
     *
     * @param ray ray {point P, direction d}
     * @param pl  polyline
     * @return boolean
     */
    public static boolean checkRayPolyLineIntersection(final ZPoint[] ray, final WB_PolyLine pl) {
        for (int i = 0; i < pl.getNumberSegments(); i++) {
            if (checkRaySegmentIntersection(ray, new ZLine(pl.getSegment(i)).toLinePD())) {
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
    public static boolean checkRayPolyLineIntersection(final ZPoint[] ray, final LineString ls) {
        for (int i = 0; i < ls.getCoordinates().length - 1; i++) {
            if (checkRaySegmentIntersection(ray, new ZLine(ls.getCoordinateN(i), ls.getCoordinateN(i + 1)).toLinePD())) {
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
    public static ZPoint simpleLineElementsIntersect2D(final ZLine l0, final String type0, final ZLine l1, final String type1) {
        return simpleLineElementsIntersect2D(l0.toLinePD(), type0, l1.toLinePD(), type1);
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
    public static ZPoint simpleLineElementsIntersect2D(final ZPoint[] l0, final String type0, final ZPoint[] l1, final String type1) {
        if (type0.equals("line") && type1.equals("line")) {
            return lineIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("segment")) {
            return segmentIntersect2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("ray")) {
            return rayIntersect2D(l0, l1);
        } else if (type0.equals("line") && type1.equals("ray")) {
            return lineRayIntersect2D(l0, l1);
        } else if (type0.equals("ray") && type1.equals("line")) {
            return lineRayIntersect2D(l1, l0);
        } else if (type0.equals("line") && type1.equals("segment")) {
            return lineSegmentIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("line")) {
            return lineSegmentIntersect2D(l1, l0);
        } else if (type0.equals("ray") && type1.equals("segment")) {
            return raySegmentIntersect2D(l0, l1);
        } else if (type0.equals("segment") && type1.equals("ray")) {
            return lineRayIntersect2D(l1, l0);
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
    public static ZPoint lineIntersect2D(final ZPoint[] line0, final ZPoint[] line1) {
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
    public static ZPoint rayIntersect2D(final ZPoint[] ray0, final ZPoint[] ray1) {
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
    public static ZPoint segmentIntersect2D(final ZPoint[] seg0, final ZPoint[] seg1) {
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
    public static ZPoint lineRayIntersect2D(final ZPoint[] line, final ZPoint[] ray) {
        ZPoint delta = ray[0].sub(line[0]);
        double crossBase = line[1].cross2D(ray[1]);
        double crossDelta0 = delta.cross2D(line[1]);
        double crossDelta1 = delta.cross2D(ray[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // line
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
    public static ZPoint lineSegmentIntersect2D(final ZPoint[] line, final ZPoint[] seg) {
        ZPoint delta = seg[0].sub(line[0]);
        double crossBase = line[1].cross2D(seg[1]);
        double crossDelta0 = delta.cross2D(line[1]);
        double crossDelta1 = delta.cross2D(seg[1]);

        if (Math.abs(crossBase) < epsilon) {
//            System.out.println("parallel or same or overlap");
            return null;
        } else {
            double s = crossDelta1 / crossBase; // line
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
    public static ZPoint raySegmentIntersect2D(final ZPoint[] ray, final ZPoint[] seg) {
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
    public static List<ZPoint> segmentPolyLineIntersect2D(final ZPoint[] seg, final WB_PolyLine pl) {
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
    public static List<ZPoint> rayPolygonIntersect2D(final ZPoint[] ray, final WB_Polygon poly) {
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
    public static List<ZPoint> linePolygonIntersect2D(final ZPoint[] line, final WB_Polygon poly) {
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZPoint[] polySeg = new ZLine(poly.getSegment(i)).toLinePD();
            ZPoint intersect = null;

            ZPoint delta = polySeg[0].sub(line[0]);
            double crossBase = line[1].cross2D(polySeg[1]);
            double crossDelta0 = delta.cross2D(line[1]);
            double crossDelta1 = delta.cross2D(polySeg[1]);

            if (Math.abs(crossBase) >= epsilon) {
                double s = crossDelta1 / crossBase; // line
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
    public static List<Integer> rayPolygonIntersectIndices2D(final ZPoint[] ray, final WB_Polygon poly) {
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
     * extend or trim the segment to polygon boundary
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static ZLine extendSegmentToPolygon(final ZPoint[] segment, final WB_Polygon poly) {
        List<ZPoint> interResult = rayPolygonIntersect2D(segment, poly);
        if (interResult.size() > 0) {
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
     * extend the segment both ends to polygon boundary
     *
     * @param segment segment {point P, direction d}
     * @param poly    input polygon
     * @return geometry.ZLine
     */
    public static ZLine extendSegmentToPolygonBothSides(final ZPoint[] segment, final WB_Polygon poly) {
        assert WB_GeometryOp.contains2D(segment[0].toWB_Point(), poly) : "input point must be within the polygon";
        List<ZPoint> interResult = linePolygonIntersect2D(segment, poly);
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
    public static int closestSegment(final ZPoint p, final WB_Polygon poly) {
        double[] dist = new double[poly.getNumberSegments()];
        WB_Point pt = p.toWB_Point();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            dist[i] = WB_GeometryOp.getDistance2D(pt, poly.getSegment(i));
        }
        return ZMath.getMinIndex(dist);
    }

    /**
     * find the closest edge index in a list of segments
     *
     * @param p    target point
     * @param segs input list ofsegments
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
     * find the point is on which segment (null)
     *
     * @param p    input point
     * @param poly input polygon
     * @return wblut.geom.WB_Segment - segment of input polygon
     */
    public static WB_Segment pointOnWhichWB_Segment(final ZPoint p, final WB_Polygon poly) {
        WB_Segment result = null;
        for (WB_Segment segment : poly.toSegments()) {
            ZLine seg = new ZLine(segment);
            if (pointOnSegment(p, seg)) {
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
    public static boolean checkPointOnPolygonEdge(final ZPoint p, final WB_Polygon poly) {
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZLine seg = new ZLine(poly.getSegment(i));
            if (pointOnSegment(p, seg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * find the point is on which polygon edge (null)
     *
     * @param p    input point
     * @param poly input polygon
     * @return geometry.ZLine
     */
    public static ZLine pointOnWhichPolyEdge(final ZPoint p, final WB_Polygon poly) {
        ZLine result = null;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            ZLine seg = new ZLine(poly.getSegment(i));
            if (pointOnSegment(p, seg)) {
                result = seg;
                break;
            }
        }
        return result;
    }

    /**
     * find the point is on which polygon edge (-1)
     *
     * @param p    input point
     * @param poly input polygon
     * @return int[] - indices of result segment
     */
    public static int[] pointOnWhichPolyEdgeIndices(final ZPoint p, final WB_Polygon poly) {
        int[] result = new int[]{-1, -1};
        for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
            ZLine seg = new ZLine(poly.getPoint(i), poly.getPoint(i + 1));
            if (pointOnSegment(p, seg)) {
                result[0] = i;
                result[1] = (i + 1) % (poly.getNumberOfPoints() - 1);
                break;
            }
        }
        return result;
    }

    /**
     * find the point is within which polygon (-1)
     *
     * @param p     input point
     * @param polys list of polygons
     * @return int - index of input list
     */
    public static int pointInWhichPolygon(final ZPoint p, final List<Polygon> polys) {
        int index = -1; // default
        for (int i = 0; i < polys.size(); i++) {
            if (p.toJtsPoint().within(polys.get(i))) {
                index = i;
            }
        }
        return index;
    }

    /**
     * find the point is within which polygon(-1)
     *
     * @param p     input point
     * @param polys array of polygons
     * @return int - index of input array
     */
    public static int pointInWhichPolygon(final ZPoint p, final Polygon[] polys) {
        int index = -1; // default
        for (int i = 0; i < polys.length; i++) {
            if (p.toJtsPoint().within(polys[i])) {
                index = i;
            }
        }
        return index;
    }


    /*-------- boundary methods --------*/

    /**
     * giving start point and distance, find two points along polygon boundary (0 forward, 1 backward)
     *
     * @param origin input point (should be on the edge of polygon)
     * @param poly   input polygon
     * @param dist   distance to move
     * @return geometry.ZPoint[] - forwards and backwards
     */
    public static ZPoint[] pointsOnEdgeByDist(final ZPoint origin, final WB_Polygon poly, double dist) {
        // find point on which edge
        int[] onWhich = pointOnWhichPolyEdgeIndices(origin, poly);
        if (onWhich[0] >= 0 && onWhich[1] >= 0) {
            ZPoint forward;
            ZPoint backward;
            int start = onWhich[0];
            int end = onWhich[1];

            // start
            double cur_spanF = dist;
            double cur_spanB = dist;
            ZPoint f1 = origin;
            ZPoint b1 = origin;

            ZPoint f2 = new ZPoint(poly.getPoint(end)); // forward next
            ZPoint b2 = new ZPoint(poly.getPoint(start)); // backward next
            double cur_distF = f1.distance(f2); // distance with forward
            double cur_distB = b1.distance(b2); // distance with backward

            forward = f1.add(f2.sub(f1).normalize().scaleTo(cur_spanF));
            while (cur_spanF > cur_distF) {
                f1 = f2;
                end = (end + 1) % (poly.getNumberOfPoints() - 1);
                f2 = new ZPoint(poly.getPoint(end));

                cur_spanF = cur_spanF - cur_distF;
                cur_distF = f1.distance(f2);
                forward = f1.add(f2.sub(f1).normalize().scaleTo(cur_spanF));
            }

            backward = b1.add(b2.sub(b1).normalize().scaleTo(cur_spanB));
            while (cur_spanB > cur_distB) {
                b1 = b2;
                start = start - 1;
                if (start == -1) { // reverse order
                    start = poly.getNumberOfPoints() - 1 - 1;
                }
                b2 = new ZPoint(poly.getPoint(start));

                cur_spanB = cur_spanB - cur_distB;
                cur_distB = b1.distance(b2);
                backward = b1.add(b2.sub(b1).normalize().scaleTo(cur_spanB));
            }

            return new ZPoint[]{forward, backward};
        } else {
            System.out.println(origin.toString());
            throw new NullPointerException("point not on polygon edges");
        }
    }

    /**
     * core function of split
     *
     * @param coords input Coordinates
     * @param step   step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    private static List<ZPoint> splitJTS(final Coordinate[] coords, final double step, final String type) {
        // initialize
        ZPoint p1 = new ZPoint(coords[0]);
        double curr_span = step;
        double curr_dist;

        List<ZPoint> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < coords.length; i++) {
            ZPoint p2 = new ZPoint(coords[i]);
            curr_dist = p1.distance(p2);

            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).normalize().scaleTo(curr_span));
                result.add(p);
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
            result.add(new ZPoint(coords[coords.length - 1]));
            if (result.get(result.size() - 1).distance(result.get(result.size() - 2)) < epsilon) {
                result.remove(result.size() - 2);
            }
        }

        return result;
    }

    /**
     * giving step to split a polygon (Polygon)
     *
     * @param poly input polygon
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdgeByStep(final Polygon poly, final double step) {
        Coordinate[] polyPoints = poly.getCoordinates();
        return splitJTS(polyPoints, step, "Polygon");
    }

    /**
     * giving step to split a LineString (LineString)
     *
     * @param ls   input LineString
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineByStep(final LineString ls, final double step) {
        Coordinate[] lsPoints = ls.getCoordinates();
        return splitJTS(lsPoints, step, "LineString");
    }

    /**
     * giving step to split a WB_PolyLine or WB_Polygon  (WB_PolyLine)
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineByStep(final WB_PolyLine poly, final double step) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // initialize
        ZPoint start = new ZPoint(polyPoints[0]);
        ZPoint end = new ZPoint(polyPoints[polyPoints.length - 1]);

        ZPoint p1 = start;
        double curr_span = step;
        double curr_dist;

        List<ZPoint> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);
            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).normalize().scaleTo(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // close: pt num = seg num
        // open: pt num = seg num + 1
        if (poly instanceof WB_Ring) {
            if (start.distance(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else {
            if (end.distance(result.get(result.size() - 1)) > epsilon) {
                result.add(end);
            }
        }

        return result;
    }

    /**
     * giving step to split a WB_PolyLine or WB_Polygon
     * return a LinkedHashMap of split point and edge index
     *
     * @param poly input polyline (polygon)
     * @param step step to divide
     * @return java.util.Map<geometry.ZPoint, java.lang.Integer>
     */
    public static Map<ZPoint, Integer> splitPolyLineByStepWithDir(final WB_PolyLine poly, final double step) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        // initialize
        ZPoint start = new ZPoint(polyPoints[0]);
        ZPoint end = new ZPoint(polyPoints[polyPoints.length - 1]);

        ZPoint p1 = start;
        double curr_span = step;
        double curr_dist;

        List<ZPoint> pointList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        pointList.add(p1);
        indexList.add(0);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);
            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).normalize().scaleTo(curr_span));
                pointList.add(p);
                indexList.add(i - 1);
                p1 = p;
                curr_span = step;
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        // close: pt num = seg num
        // open: pt num = seg num + 1
        if (poly instanceof WB_Ring) {
            if (start.distance(pointList.get(pointList.size() - 1)) < epsilon) {
                pointList.remove(pointList.size() - 1);
                indexList.remove(indexList.size() - 1);
            }
        } else {
            if (end.distance(pointList.get(pointList.size() - 1)) > epsilon) {
                pointList.add(end);
                indexList.add(poly.getNumberSegments() - 1);
            }
        }

        // create linkedHashMap
        Map<ZPoint, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < pointList.size(); i++) {
            result.put(pointList.get(i), indexList.get(i));
        }
        return result;
    }

    /**
     * giving step and shaking threshold to split a WB_PolyLine or WB_Polygon (WB_PolyLine)
     *
     * @param poly  input polyline (polygon)
     * @param step  step to divide
     * @param shake threshold to shake
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineByRandomStep(final WB_PolyLine poly, final double step, final double shake) {
        WB_Coord[] polyPoints = poly.getPoints().toArray();

        ZPoint start = new ZPoint(polyPoints[0]);
        ZPoint end = new ZPoint(polyPoints[polyPoints.length - 1]);

        ZPoint p1 = start;
        double curr_span = step + ZMath.random(step - shake, step + shake);
        double curr_dist;

        List<ZPoint> result = new ArrayList<>();
        result.add(p1);
        for (int i = 1; i < poly.getNumberOfPoints(); i++) {
            ZPoint p2 = new ZPoint(polyPoints[i]);
            curr_dist = p1.distance(p2);
            while (curr_dist >= curr_span) {
                ZPoint p = p1.add(p2.sub(p1).normalize().scaleTo(curr_span));
                result.add(p);
                p1 = p;
                curr_span = step + ZMath.random(step - shake, step + shake);
                curr_dist = p1.distance(p2);
            }
            p1 = p2;
            curr_span = curr_span - curr_dist;
        }

        if (poly instanceof WB_Ring) {
            if (start.distance(result.get(result.size() - 1)) < epsilon) {
                result.remove(result.size() - 1);
            }
        } else {
            if (end.distance(result.get(result.size() - 1)) > epsilon) {
                result.add(end);
            }
        }

        return result;
    }

    /**
     * giving step threshold to split a polygon (Polygon)
     *
     * @param poly    input polygon
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdgeByThreshold(final Polygon poly, final double maxStep, final double minStep) {
        double finalStep = 0;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            double curr_step = poly.getLength() / i;
            if (curr_step >= minStep && curr_step <= maxStep) {
                finalStep = curr_step;
                break;
            } else if (curr_step < minStep) {
                return new ArrayList<ZPoint>();
            }
        }
        //        System.out.println("step:" + finalStep);
        return splitPolygonEdgeByStep(poly, finalStep);
    }

    /**
     * giving step threshold to split a WB_PolyLine or WB_Polygon (WB_PolyLine)
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
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
                System.out.println("cannot generate split point by this step!");
                return new ArrayList<ZPoint>();
            }
        }
        return splitPolyLineByStep(poly, finalStep);
    }

    /**
     * giving step threshold to split a WB_PolyLine or WB_Polygon
     * return a LinkedHashMap of split point and edge index
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.Map<geometry.ZPoint, java.lang.Integer>
     */
    public static Map<ZPoint, Integer> splitPolyLineByThresholdWithDir(final WB_PolyLine poly, final double maxStep, final double minStep) {
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
        return splitPolyLineByStepWithDir(poly, finalStep);
    }

    /**
     * giving step threshold to split each segment of a WB_PolyLine or WB_Polygon
     *
     * @param poly    input polyline (polygon)
     * @param maxStep max step to divide
     * @param minStep min step to divide
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineEachEdgeByThreshold(final WB_PolyLine poly, final double maxStep, final double minStep) {
        assert maxStep >= minStep : "please input valid threshold";
        List<ZPoint> result = new ArrayList<>();
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            WB_Segment segment = poly.getSegment(i);
            double length = segment.getLength();
            double stepOnEdge = 0;
            int splitNum = -1;
            for (int j = 1; j < Integer.MAX_VALUE; j++) {
                double curr_step = length / j;
                if (curr_step >= minStep && curr_step <= maxStep) {
                    stepOnEdge = curr_step;
                    splitNum = j;
                    break;
                } else if (curr_step < minStep) {
                    break;
                }
            }

            if (stepOnEdge != 0) {
                ZPoint curr = new ZPoint(segment.getOrigin());
                result.add(curr);
                for (int j = 0; j < splitNum - 1; j++) {
                    curr = curr.add(new ZPoint(segment.getDirection()).scaleTo(stepOnEdge));
                    result.add(curr);
                }
            } else {
                result.add(new ZPoint(segment.getOrigin()));
            }
        }

        if (!poly.getPoint(0).equals(poly.getPoint(poly.getNumberOfPoints() - 1))) {
            result.add(new ZPoint(poly.getPoint(poly.getNumberOfPoints() - 1)));
        }
        return result;
    }

    /**
     * giving a split number, split equally (LineString)
     *
     * @param ls       input LineString
     * @param splitNum number to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineEdge(final LineString ls, final int splitNum) {
        double step = ls.getLength() / splitNum;
        return splitPolyLineByStep(ls, step);
    }

    /**
     * giving a split number, split equally(WB_PolyLine)
     *
     * @param poly     input polyline (polygon)
     * @param splitNum number to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolyLineEdge(final WB_PolyLine poly, final int splitNum) {
        // get step
        double length = 0;
        for (int i = 0; i < poly.getNumberSegments(); i++) {
            length = length + poly.getSegment(i).getLength();
        }
        double step = length / splitNum;

        return splitPolyLineByStep(poly, step);
    }

    /**
     * giving a split number, split equally(Polygon)
     *
     * @param poly     input polygon
     * @param splitNum number to split
     * @return java.util.List<geometry.ZPoint>
     */
    public static List<ZPoint> splitPolygonEdge(final Polygon poly, final int splitNum) {
        double step = poly.getLength() / splitNum;
        return splitPolygonEdgeByStep(poly, step);
    }


    /*-------- polygon tools --------*/

    /**
     * get the direction of a OBB
     *
     * @param polygon input polygon
     * @return geometry.ZPoint
     */
    public static ZPoint miniRectDir(final WB_Polygon polygon) {
        Polygon rect = (Polygon) MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToPolygon(polygon));
        Coordinate c0 = rect.getCoordinates()[0];
        Coordinate c1 = rect.getCoordinates()[1];
        Coordinate c2 = rect.getCoordinates()[2];

        ZPoint dir1 = new ZPoint(c0).sub(new ZPoint(c1)).normalize();
        ZPoint dir2 = new ZPoint(c2).sub(new ZPoint(c1)).normalize();

        return c0.distance(c1) >= c1.distance(c2) ? dir2 : dir1;
    }

    /**
     * get the direction of a OBB
     *
     * @param polygon input polygon
     * @return geometry.ZPoint
     */
    public static ZPoint miniRectDir(final Polygon polygon) {
        Polygon rect = (Polygon) MinimumDiameter.getMinimumRectangle(polygon);
        Coordinate c0 = rect.getCoordinates()[0];
        Coordinate c1 = rect.getCoordinates()[1];
        Coordinate c2 = rect.getCoordinates()[2];

        ZPoint dir1 = new ZPoint(c0).sub(new ZPoint(c1)).normalize();
        ZPoint dir2 = new ZPoint(c2).sub(new ZPoint(c1)).normalize();

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
    public static ZLine offsetWB_PolygonSegment(final WB_Polygon poly, final int index, final double dist) {
        // make sure polygon's start and end point are coincident
        WB_Polygon polygon = ZTransform.validateWB_Polygon(poly);
        assert index <= polygon.getNumberSegments() && index >= 0 : "index out of polygon point number";

        int next = (index + 1) % polygon.getNumberSegments();
        int prev = (index + polygon.getNumberSegments() - 1) % polygon.getNumberSegments();

        ZPoint v1 = new ZPoint(polygon.getSegment(prev).getOrigin()).sub(new ZPoint(polygon.getSegment(prev).getEndpoint()));
        ZPoint v2 = new ZPoint(polygon.getSegment(index).getEndpoint()).sub(new ZPoint(polygon.getSegment(index).getOrigin()));
        ZPoint bisector1 = getAngleBisectorOrdered(v1, v2);
        ZPoint point1 = new ZPoint(polygon.getSegment(index).getOrigin()).add(bisector1.scaleTo(dist / Math.abs(v1.normalize().cross2D(bisector1))));

        ZPoint v3 = new ZPoint(polygon.getSegment(index).getOrigin()).sub(new ZPoint(polygon.getSegment(index).getEndpoint()));
        ZPoint v4 = new ZPoint(polygon.getSegment(next).getEndpoint()).sub(new ZPoint(polygon.getSegment(next).getOrigin()));
        ZPoint bisector2 = getAngleBisectorOrdered(v3, v4);
        ZPoint point2 = new ZPoint(polygon.getSegment(index).getEndpoint()).add(bisector2.scaleTo(dist / Math.abs(v3.normalize().cross2D(bisector2))));

        return new ZLine(point1, point2);
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

            ZPoint v1 = new ZPoint(polygon.getSegment(prev).getOrigin()).sub(new ZPoint(polygon.getSegment(prev).getEndpoint()));
            ZPoint v2 = new ZPoint(polygon.getSegment(index[i]).getEndpoint()).sub(new ZPoint(polygon.getSegment(index[i]).getOrigin()));
            ZPoint bisector1 = getAngleBisectorOrdered(v1, v2);
            ZPoint point1 = new ZPoint(polygon.getSegment(index[i]).getOrigin()).add(bisector1.scaleTo(dist / Math.abs(v1.normalize().cross2D(bisector1))));

            linePoints[i] = point1.toWB_Point();
        }

        int next = (index[index.length - 1] + 1) % polygon.getNumberSegments();
        ZPoint v3 = new ZPoint(polygon.getSegment(index[index.length - 1]).getOrigin()).sub(new ZPoint(polygon.getSegment(index[index.length - 1]).getEndpoint()));
        ZPoint v4 = new ZPoint(polygon.getSegment(next).getEndpoint()).sub(new ZPoint(polygon.getSegment(next).getOrigin()));
        ZPoint bisector2 = getAngleBisectorOrdered(v3, v4);
        ZPoint point2 = new ZPoint(polygon.getSegment(index[index.length - 1]).getEndpoint()).add(bisector2.scaleTo(dist / Math.abs(v3.normalize().cross2D(bisector2))));
        linePoints[linePoints.length - 1] = point2.toWB_Point();

        if (linePoints[0].equals(linePoints[linePoints.length - 1])) {
            return new WB_Polygon(linePoints);
        } else {
            return new WB_PolyLine(linePoints);
        }
    }


    /*-------- other methods --------*/

    /**
     * set jts precision model (FLOAT, FLOAT_SINGLE, FIXED)
     *
     * @param geometry geometry to be applied
     * @param pm       precision model
     * @return void
     */
    public static void applyJtsPrecisionModel(final Geometry geometry, final PrecisionModel pm) {
        Coordinate[] coordinates = geometry.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            Coordinate coordinate = coordinates[i];
            pm.makePrecise(coordinate);
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
                    (obb.getCoordinates()[0].x + obb.getCoordinates()[1].x) * 0.5,
                    (obb.getCoordinates()[0].y + obb.getCoordinates()[1].y) * 0.5,
                    (obb.getCoordinates()[0].z + obb.getCoordinates()[1].z) * 0.5
            );
            mid2 = new Coordinate(
                    (obb.getCoordinates()[2].x + obb.getCoordinates()[3].x) * 0.5,
                    (obb.getCoordinates()[2].y + obb.getCoordinates()[3].y) * 0.5,
                    (obb.getCoordinates()[2].z + obb.getCoordinates()[3].z) * 0.5
            );
            result[0] = ZFactory.jtsgf.createPolygon(
                    new Coordinate[]{obb.getCoordinates()[0], mid1, mid2, obb.getCoordinates()[3], obb.getCoordinates()[0]}
            );
            result[1] = ZFactory.jtsgf.createPolygon(
                    new Coordinate[]{mid1, obb.getCoordinates()[1], obb.getCoordinates()[2], mid2, mid1}
            );
        } else {
            mid1 = new Coordinate(
                    (obb.getCoordinates()[1].x + obb.getCoordinates()[2].x) * 0.5,
                    (obb.getCoordinates()[1].y + obb.getCoordinates()[2].y) * 0.5,
                    (obb.getCoordinates()[1].z + obb.getCoordinates()[2].z) * 0.5
            );
            mid2 = new Coordinate(
                    (obb.getCoordinates()[3].x + obb.getCoordinates()[4].x) * 0.5,
                    (obb.getCoordinates()[3].y + obb.getCoordinates()[4].y) * 0.5,
                    (obb.getCoordinates()[3].z + obb.getCoordinates()[4].z) * 0.5
            );
            result[0] = ZFactory.jtsgf.createPolygon(
                    new Coordinate[]{obb.getCoordinates()[0], obb.getCoordinates()[1], mid1, mid2, obb.getCoordinates()[0]}
            );
            result[1] = ZFactory.jtsgf.createPolygon(
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

}