package transform;

import igeo.ICurve;
import igeo.IPoint;
import org.locationtech.jts.geom.*;
import wblut.geom.*;
import wblut.geom.WB_GeometryFactory;

import java.util.List;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/9
 * @time 17:27
 * @description 常用库几何数据的相互转换
 * 目前仅涉及简单多边形
 * ** IGeo <-> WB **
 * IPoint -> WB_Point
 * IPoint -> WB_Point 带缩放
 * ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment
 * ICurve -> WB_Geometry 根据点数和闭合与否返回WB_Polygon / WB_Polyline / WB_Segment，带缩放
 * ** IGeo <-> jts **
 * IPoint -> Coordinate
 * IPoint -> Point
 * ICurve -> Geometry 根据点数和闭合与否返回Polygon / LineString
 * ** WB <-> jts **
 * WB_Polygon -> Polygon 如果WB_Polygon第一点与最后一点不重合，就加上最后一点
 * Polygon -> WB_Polygon
 * LineString -> WB_PolyLine
 * WB_PolyLine -> LineString
 * WB_Segment -> LineString
 * ** WB <-> WB **
 * WB_Polygon -> WB_Polygon 检查WB_Polygon第一点与最后一点是否重合，不重合则加上
 * WB_Polygon -> WB_PolyLine
 * WB_AABB -> WB_AABB offset WB_AABB
 * <p>
 * ...增加中
 */
public class ZTransform {
    private static final GeometryFactory gf = new GeometryFactory();
    private static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    private static final double epsilon = 0.00000001;

    /*-------- IGeo <-> WB --------*/

    /**
     * @return wblut.geom.WB_Point
     * @description load a single point to WB_Point
     */
    public static WB_Point IPointToWB(final IPoint point) {
        return new WB_Point(point.x(), point.y(), point.z());
    }

    /**
     * @return wblut.geom.WB_Point
     * @description load a single point to WB_Point
     */
    public static WB_Point IPointToWB(final IPoint point, final double scale) {
        return new WB_Point(point.x(), point.y(), point.z()).scale(scale);
    }

    /**
     * @return wblut.geom.WB_Geometry
     * @description load ICurve to WB_Polyline or WB_Polygon or WB_Segment
     */
    public static WB_Geometry2D ICurveToWB(final ICurve curve) {
        if (curve.cpNum() > 2 && !curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createPolyLine(points);
        } else if (curve.cpNum() > 2 && curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createSimplePolygon(points);
        } else if (curve.cpNum() == 2) {
            WB_Point start = new WB_Point(curve.cp(0).x(), curve.cp(0).y(), curve.cp(0).z());
            WB_Point end = new WB_Point(curve.cp(1).x(), curve.cp(1).y(), curve.cp(1).z());
            return new WB_Segment(start, end);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /**
     * @return wblut.geom.WB_Geometry
     * @description load ICurve to WB_Polyline or WB_Polygon or WB_Segment (with a scale rate)
     */
    public static WB_Geometry2D ICurveToWB(final ICurve curve, final double scale) {
        if (curve.cpNum() > 2 && !curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z()).scale(scale);
            }
            return wbgf.createPolyLine(points);
        } else if (curve.cpNum() > 2 && curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z()).scale(scale);
            }
            return wbgf.createSimplePolygon(points);
        } else if (curve.cpNum() == 2) {
            WB_Point start = new WB_Point(curve.cp(0).x(), curve.cp(0).y(), curve.cp(0).z()).scale(scale);
            WB_Point end = new WB_Point(curve.cp(1).x(), curve.cp(1).y(), curve.cp(1).z()).scale(scale);
            return new WB_Segment(start, end);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /*-------- IGeo <-> Jts --------*/

    /**
     * @return org.locationtech.jts.geom.Coordinate
     * @description load a single point to Jts Coordinate
     */
    public static Coordinate IPointToJtsCoordinate(final IPoint point) {
        return new Coordinate(point.x(), point.y(), point.z());
    }

    /**
     * @return org.locationtech.jts.geom.Coordinate
     * @description load a single point to Jts Point
     */
    public static Point IPointToJtsPoint(final IPoint point) {
        return gf.createPoint(IPointToJtsCoordinate(point));
    }

    /**
     * @return org.locationtech.jts.geom.Geometry
     * @description load ICurve to Polygon, LineString
     */
    public static Geometry ICurveToJts(final ICurve curve) {
        if (curve.cpNum() > 2 && curve.isClosed()) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createPolygon(curvePts);
        } else if (curve.cpNum() > 2 && !curve.isClosed()) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createLineString(curvePts);
        } else if (curve.cpNum() == 2) {
            Coordinate[] curvePts = new Coordinate[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                curvePts[i] = new Coordinate(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return gf.createLineString(curvePts);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /*-------- WB <-> Jts --------*/

    /**
     * @return com.vividsolutions.jts.geom.Polygon
     * @description transform WB_Polygon to jts Polygon
     */
    public static Polygon WB_PolygonToJtsPolygon(final WB_Polygon wbp) {


        if (wbp.getPoint(0).getDistance2D(wbp.getPoint(wbp.getNumberOfPoints() - 1)) < epsilon) {
            Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
            for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
            }
            return gf.createPolygon(coords);
        } else {
            Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints() + 1];
            for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
            }
            coords[wbp.getNumberOfPoints()] = coords[0];

            return gf.createPolygon(coords);
        }
    }

    /**
     * @return wblut.geom.WB_Polygon
     * @description transform jts Polygon to WB_Polygon (could contain holes)
     */
    public static WB_Polygon jtsPolygonToWB_Polygon(final Polygon p) {
        if (p.getNumInteriorRing() == 0) {
            WB_Coord[] points = new WB_Point[p.getNumPoints()];
            for (int i = 0; i < p.getNumPoints(); i++) {
                points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            return new WB_Polygon(points).getSimplePolygon();
        } else {
            // exterior
            WB_Coord[] exteriorPoints = new WB_Point[p.getExteriorRing().getNumPoints()];
            for (int i = 0; i < p.getExteriorRing().getNumPoints(); i++) {
                exteriorPoints[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            // interior
            int index = p.getExteriorRing().getNumPoints();
            WB_Coord[][] interiorHoles = new WB_Point[p.getNumInteriorRing()][];
            for (int i = 0; i < p.getNumInteriorRing(); i++) {
                LineString curr = p.getInteriorRingN(i);
                WB_Coord[] holePoints = new WB_Point[curr.getNumPoints()];
                for (int j = 0; j < curr.getNumPoints(); j++) {
                    WB_Point point = new WB_Point(curr.getCoordinates()[j].x, curr.getCoordinates()[j].y, curr.getCoordinates()[j].z);
                    holePoints[j] = point;
                }
                interiorHoles[i] = holePoints;
            }
            return new WB_Polygon(exteriorPoints, interiorHoles);
        }
    }

    /**
     * @return wblut.geom.WB_Polygon
     * @description transform jts Polygon to WB_Polygon
     */
    public static WB_PolyLine JtsLineStringToWB_PolyLine(final LineString p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_PolyLine(points);
    }

    /**
     * @return org.locationtech.jts.geom.LineString
     * @description transform WB_PolyLine to jts LineString
     */
    public static LineString WB_PolyLineToJtsLineString(final WB_PolyLine wbp) {
        Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
        for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
            coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
        }
        return gf.createLineString(coords);
    }

    /**
     * @return org.locationtech.jts.geom.LineSegment
     * @description transform WB_Segment to Jts LineString
     */
    public static LineString WB_SegmentToJtsLineString(final WB_Segment seg) {
        Coordinate[] coords = new Coordinate[2];
        coords[0] = new Coordinate(seg.getOrigin().xd(), seg.getOrigin().yd(), seg.getOrigin().zd());
        coords[1] = new Coordinate(seg.getEndpoint().xd(), seg.getEndpoint().yd(), seg.getEndpoint().zd());
        return gf.createLineString(coords);
    }

    /*-------- WB <-> WB --------*/

    /**
     * @return wblut.geom.WB_Polygon
     * @description verify that if the first Point and the last point's superposition
     */
    public static WB_Polygon verifyWB_Polygon(final WB_Polygon polygon) {
        if (polygon.getPoint(0).getDistance2D(polygon.getPoint(polygon.getNumberOfPoints() - 1)) < epsilon) {
            return polygon;
        } else {
            List<WB_Coord> points = polygon.getPoints().toList();
            points.add(polygon.getPoint(0));
            return wbgf.createSimplePolygon(points);
        }
    }

    /**
     * @return wblut.geom.WB_PolyLine
     * @description transform WB_Polygon to WB_PolyLine
     */
    public static WB_PolyLine WB_PolygonToPolyLine(final WB_Polygon polygon) {
        WB_Point[] points = new WB_Point[polygon.getNumberOfPoints()];
        for (int i = 0; i < points.length; i++) {
            points[i] = polygon.getPoint(i);
        }
        return wbgf.createPolyLine(points);
    }

    /**
     * @return wblut.geom.WB_AABB
     * @description 给 WB_AABB offset
     */
    public static WB_AABB offsetAABB(final WB_AABB aabb, final double t) {
        WB_Point min = aabb.getMin();
        WB_Point max = aabb.getMax();
        WB_Point newMin = min.add(min.sub(aabb.getCenter()).scale(t));
        WB_Point newMax = max.add(max.sub(aabb.getCenter()).scale(t));
        return new WB_AABB(newMin, newMax);
    }
}
