package basicGeometry;

import math.ZGeoMath;
import math.ZMath;
import org.locationtech.jts.algorithm.Distance;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.math.Vector2D;
import org.locationtech.jts.operation.linemerge.LineMergeGraph;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.planargraph.Edge;
import org.locationtech.jts.planargraph.Node;
import transform.ZTransform;
import wblut.geom.*;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_MeshOp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * a class to create geometries and graphs
 * including GeometryFactory and WB_GeometryFactory
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/8
 * @time 22:04
 */
public class ZFactory {
    public static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    public static final GeometryFactory jtsgf = new GeometryFactory();

    /*-------- create geometries --------*/

    /**
     * create random point within a range
     *
     * @param xmin
     * @param xmax
     * @param ymin
     * @param ymax
     * @return org.locationtech.jts.geom.Point
     */
    public static Point createRandomPoint(final double xmin, final double xmax, final double ymin, final double ymax) {
        return jtsgf.createPoint(new Coordinate(
                xmin + (Math.random() * (xmax - xmin)),
                ymin + (Math.random() * (ymax - ymin))
        ));
    }

    /**
     * generate random simple polygon around origin
     *
     * @param ptNum     number of polygon points, excluding the last point
     * @param scale     scale of the polygon
     * @param threshold threshold to random
     * @return Polygon
     */
    public static Polygon createRandomPolygon(int ptNum, double scale, double threshold) {
        double thre = threshold > 1 || threshold < 0 ? 0.5 : threshold;
        double angle = (Math.PI * 2) / ptNum;

        Coordinate[] coords = new Coordinate[ptNum + 1];
        for (int i = 0; i < ptNum; i++) {
            double ran = (Math.random() * 2 * thre + 1 - thre) * scale;
            Coordinate coord = new Coordinate(Math.cos(angle * i) * ran, Math.sin(angle * i) * ran);
            coords[i] = coord;
        }
        coords[coords.length - 1] = new Coordinate(coords[0].x, coords[0].y);

        return ZFactory.jtsgf.createPolygon(coords);
    }

    /**
     * generate random simple WB_Polygon around origin
     *
     * @param ptNum     number of polygon points, excluding the last point
     * @param scale     scale of the polygon
     * @param threshold threshold to random
     * @return WB_Polygon
     */
    public static WB_Polygon createRandomPolygonWB(int ptNum, double scale, double threshold) {
        double thre = threshold > 1 || threshold < 0 ? 0.5 : threshold;
        double angle = (Math.PI * 2) / ptNum;

        WB_Point[] pts = new WB_Point[ptNum + 1];
        for (int i = 0; i < ptNum; i++) {
            double ran = (Math.random() * 2 * thre + 1 - thre) * scale;
            WB_Point p = new WB_Point(Math.cos(angle * i) * ran, Math.sin(angle * i) * ran);
            pts[i] = p;
        }
        pts[pts.length - 1] = new WB_Point(pts[0].xd(), pts[0].yd());

        return new WB_Polygon(pts);
    }

    /**
     * create a LineString from a Coordinate list
     *
     * @param list Coordinate list
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString createLineStringFromList(final List<Coordinate> list) {
        Coordinate[] array = new Coordinate[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return jtsgf.createLineString(array);
    }

    /**
     * create a LinearRing from a Coordinate list    *
     *
     * @param list Coordinate list
     * @return org.locationtech.jts.geom.LinearRing
     */
    public static LinearRing createLinearRingFromList(final List<Coordinate> list) {
        Coordinate[] array = new Coordinate[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return jtsgf.createLinearRing(array);
    }

    /**
     * create a Polygon from a Coordinate list
     *
     * @param list Coordinate list
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon createPolygonFromList(final List<Coordinate> list) {
        Coordinate[] array = new Coordinate[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return jtsgf.createPolygon(array);
    }

    /**
     * create a Polygon from a base + wh + angle
     *
     * @param x base x (bottom left)
     * @param y base y
     * @param w width
     * @param h height
     * @param a rotate angle
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon createRectFromXYWHA(
            final double x,
            final double y,
            final double w,
            final double h,
            final double a
    ) {
        Coordinate base = new Coordinate(x, y);
        Vector2D dir1 = new Vector2D(Math.cos(a), Math.sin(a));
        double angle = Math.PI * 0.5;
        Vector2D dir2 = dir1.rotate(angle);
        Vector2D dir1Mul = dir1.multiply(w);
        Coordinate base2 = new Coordinate(base.getX() + dir1Mul.getX(), base.getY() + dir1Mul.getY());

        Coordinate[] coords = new Coordinate[5];
        coords[0] = base;
        coords[1] = base2;
        Vector2D dir2Mul = dir2.multiply(h);
        coords[2] = new Coordinate(base2.getX() + dir2Mul.getX(), base2.getY() + dir2Mul.getY());
        coords[3] = new Coordinate(base.getX() + dir2Mul.getX(), base.getY() + dir2Mul.getY());
        coords[4] = base;

        return ZFactory.jtsgf.createPolygon(coords);
    }

    /**
     * create a Polygon with holes from Coordinate[] and Coordinate[][]
     *
     * @param exterior exterior Coordinates
     * @param interior interior Coordinates
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon createPolygonWithHoles(final Coordinate[] exterior, final Coordinate[][] interior) {
        LinearRing ex = jtsgf.createLinearRing(exterior);
        LinearRing[] in = new LinearRing[interior.length];
        for (int i = 0; i < interior.length; i++) {
            in[i] = jtsgf.createLinearRing(interior[i]);
        }
        return jtsgf.createPolygon(ex, in);
    }

    /**
     * create a Polygon with holes from LineString and LineString[]
     *
     * @param exterior exterior LineString
     * @param interior interior LineString
     * @return org.locationtech.jts.geom.Polygon
     */
    public static Polygon createPolygonWithHoles(final LineString exterior, final LineString[] interior) {
        LinearRing ex = ZTransform.LineStringToLinearRing(exterior);
        LinearRing[] in = new LinearRing[interior.length];
        for (int i = 0; i < interior.length; i++) {
            in[i] = ZTransform.LineStringToLinearRing(interior[i]);
        }
        return jtsgf.createPolygon(ex, in);
    }

    /**
     * create OBB from WB_Polygon
     *
     * @param geo WB_Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon createWB_PolygonOBB(WB_Polygon geo) {
        Geometry minimumRectangle = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolygonToPolygon(geo));
        return ZTransform.PolygonToWB_Polygon((Polygon) minimumRectangle);
    }

    /**
     * create OBB from WB_PolyLine
     *
     * @param geo WB_PolyLine
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon createWB_PolylineOBB(WB_PolyLine geo) {
        Geometry minimumRectangle = MinimumDiameter.getMinimumRectangle(ZTransform.WB_PolyLineToLineString(geo));
        return ZTransform.PolygonToWB_Polygon((Polygon) minimumRectangle);
    }

    /**
     * create a LineString from a list of segments
     * if the result is MultiLineString, choose the longest one
     *
     * @param lines list of lines
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString createLineString(final List<? extends ZLine> lines) {
        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (ZLine line : lines) {
            lineStrings.add(line.toJtsLineString());
        }
        lineMerger.add(lineStrings);

        Collection mergeResult = lineMerger.getMergedLineStrings();
        Object[] result = mergeResult.toArray();
        if (mergeResult.size() > 1) {
            double[] lineStringLengths = new double[result.length];
            for (int i = 0; i < result.length; i++) {
                LineString l = (LineString) result[i];
                lineStringLengths[i] = l.getLength();
            }
            return (LineString) result[ZMath.getMaxIndex(lineStringLengths)];
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            return (LineString) result[0];
        } else {
            return null;
        }
    }

    /**
     * create a WB_PolyLine from a list of segments
     * if the result is MultiLineString, choose the longest one
     *
     * @param lines list of LineString
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString mergeLineStrings(final List<? extends LineString> lines) {
        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>(lines);
        lineMerger.add(lineStrings);

        if (lineMerger.getMergedLineStrings().size() > 1) {
            double[] lineStringLengths = new double[lineMerger.getMergedLineStrings().toArray().length];
            for (int i = 0; i < lineMerger.getMergedLineStrings().toArray().length; i++) {
                LineString l = (LineString) lineMerger.getMergedLineStrings().toArray()[i];
                lineStringLengths[i] = l.getLength();
            }
//            System.out.println("lines:"+lineMerger.getMergedLineStrings().toArray().length);

            return (LineString) lineMerger.getMergedLineStrings().toArray()[ZMath.getMaxIndex(lineStringLengths)];
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            return (LineString) lineMerger.getMergedLineStrings().toArray()[0];
        } else {
            return null;
        }
    }


    /**
     * create a WB_PolyLine from a list of segments
     * if the result is MultiLineString, choose the longest one
     *
     * @param lines list of lines
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine mergeWB_PolyLineFromZLines(final List<? extends ZLine> lines) {
        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (ZLine line : lines) {
            lineStrings.add(line.toJtsLineString());
        }
        lineMerger.add(lineStrings);
        if (lineMerger.getMergedLineStrings().size() > 1) {
            double[] lineStringLengths = new double[lineMerger.getMergedLineStrings().toArray().length];
            for (int i = 0; i < lineMerger.getMergedLineStrings().toArray().length; i++) {
                LineString l = (LineString) lineMerger.getMergedLineStrings().toArray()[i];
                lineStringLengths[i] = l.getLength();
            }
//            System.out.println("lines:"+lineMerger.getMergedLineStrings().toArray().length);

            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[ZMath.getMaxIndex(lineStringLengths)];
            return ZTransform.LineStringToWB_PolyLine(merged);
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[0];
            return ZTransform.LineStringToWB_PolyLine(merged);
        } else {
            return null;
        }
    }

    /**
     * create a WB_PolyLine from a list of WB_Segment
     * if the result is MultiLineString, choose the longest one
     *
     * @param segs list of WB_Segment
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine mergeWB_PolyLineFromSegs(final List<? extends WB_Segment> segs) {
        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (WB_Segment seg : segs) {
            lineStrings.add(ZTransform.WB_SegmentToLineString(seg));
        }
        lineMerger.add(lineStrings);
        if (lineMerger.getMergedLineStrings().size() > 1) {
            double[] lineStringLengths = new double[lineMerger.getMergedLineStrings().toArray().length];
            for (int i = 0; i < lineMerger.getMergedLineStrings().toArray().length; i++) {
                LineString l = (LineString) lineMerger.getMergedLineStrings().toArray()[i];
                lineStringLengths[i] = l.getLength();
            }
//            System.out.println("lines:"+lineMerger.getMergedLineStrings().toArray().length);

            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[ZMath.getMaxIndex(lineStringLengths)];
            return ZTransform.LineStringToWB_PolyLine(merged);
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[0];
            return ZTransform.LineStringToWB_PolyLine(merged);
        } else {
            return null;
        }
    }

    /**
     * create a list of WB_PolyLine from a list of segments
     *
     * @param lines list of lines
     * @return java.util.List<wblut.geom.WB_PolyLine>
     */
    public static List<WB_PolyLine> createWB_PolyLineList(final List<? extends ZLine> lines) {
        List<WB_PolyLine> result = new ArrayList<>();

        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (ZLine line : lines) {
            lineStrings.add(line.toJtsLineString());
        }
        lineMerger.add(lineStrings);
        if (!lineMerger.getMergedLineStrings().isEmpty()) {
            for (Object ls : lineMerger.getMergedLineStrings()) {
                if (ls instanceof LineString) {
                    result.add(ZTransform.LineStringToWB_PolyLine((LineString) ls));
                }
            }
        }
        return result;
    }

    /**
     * create a list of WB_PolyLine from a list of segments
     *
     * @param segs list of lines
     * @return java.util.List<wblut.geom.WB_PolyLine>
     */
    public static List<WB_PolyLine> createWB_PolyLineListFromSegments(final List<WB_Segment> segs) {
        List<WB_PolyLine> result = new ArrayList<>();

        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (WB_Segment seg : segs) {
            lineStrings.add(ZTransform.WB_SegmentToLineString(seg));
        }
        lineMerger.add(lineStrings);
        if (!lineMerger.getMergedLineStrings().isEmpty()) {
            for (Object ls : lineMerger.getMergedLineStrings()) {
                if (ls instanceof LineString) {
                    result.add(ZTransform.LineStringToWB_PolyLine((LineString) ls));
                }
            }
        }
        return result;
    }

    /**
     * break a polyline to a list of ZLine
     *
     * @param polyLine polyline to break
     * @return java.util.List<basicGeometry.ZLine>
     */
    public static List<ZLine> breakWB_PolyLine(final WB_PolyLine polyLine) {
        List<ZLine> result = new ArrayList<>();
        for (int i = 0; i < polyLine.getNumberSegments(); i++) {
            result.add(new ZLine(polyLine.getSegment(i)));
        }
        return result;
    }

    /**
     * break WB_PolyLine by giving point indices to break
     *
     * @param polyLine   polyLine to be break
     * @param breakPoint indices of break point
     * @return java.util.List<wblut.geom.WB_PolyLine>
     */
    public static List<WB_PolyLine> breakWB_PolyLine(final WB_PolyLine polyLine, final int[] breakPoint) {
        List<WB_PolyLine> result = new ArrayList<>();
        if (polyLine instanceof WB_Ring) {
            for (int i = 0; i < breakPoint.length; i++) {
                assert breakPoint[i] > 0 && breakPoint[i] < polyLine.getNumberOfPoints() - 1 : "index must among the middle points";
                WB_Point[] polyPoints = new WB_Point[
                        (breakPoint[(i + 1) % breakPoint.length] + polyLine.getNumberOfPoints() - 1 - breakPoint[i])
                                % (polyLine.getNumberOfPoints() - 1)
                                + 1];
                for (int j = 0; j < polyPoints.length; j++) {
                    polyPoints[j] = polyLine.getPoint((j + breakPoint[i]) % (polyLine.getNumberOfPoints() - 1));
                }
                result.add(wbgf.createPolyLine(polyPoints));
            }
        } else {
            int count = 0;
            for (int index : breakPoint) {
                assert index > 0 && index < polyLine.getNumberOfPoints() - 1 : "index must among the middle points";
                List<WB_Point> polyPoints = new ArrayList<>();
                for (int i = count; i < index + 1; i++) {
                    polyPoints.add(polyLine.getPoint(i));
                }
                result.add(wbgf.createPolyLine(polyPoints));
                count = index;
            }
            // add last one
            List<WB_Point> polyPoints = new ArrayList<>();
            for (int i = count; i < polyLine.getNumberOfPoints(); i++) {
                polyPoints.add(polyLine.getPoint(i));
            }
            result.add(wbgf.createPolyLine(polyPoints));
        }
        return result;
    }

    /**
     * break LineString by giving point indices to break
     *
     * @param lineString lineString to break
     * @param breakPoint indices of break point
     * @return java.util.List<org.locationtech.jts.geom.LineString>
     */
    public static List<LineString> breakLineString(final LineString lineString, final int[] breakPoint) {
        List<LineString> result = new ArrayList<>();
        int count = 0;
        for (int index : breakPoint) {
            assert index > 0 && index < lineString.getNumPoints() - 1 : "index must among the middle points";
            Coordinate[] coords = new Coordinate[index + 1 - count];
            for (int i = 0; i < coords.length; i++) {
                coords[i] = lineString.getCoordinateN(i + count);
            }
            result.add(jtsgf.createLineString(coords));
            count = index;
        }
        // add last one
        Coordinate[] coords = new Coordinate[lineString.getNumPoints() - count];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = lineString.getCoordinateN(i + count);
        }
        result.add(jtsgf.createLineString(coords));
        return result;
    }

    /**
     * break Geometry to segments (list of LineString)
     *
     * @param geo input Geometry
     * @return java.util.List<org.locationtech.jts.geom.LineString>
     */
    public static List<LineString> breakGeometryToSegments(final Geometry geo) {
        List<LineString> result = new ArrayList<>();
        switch (geo.getGeometryType()) {
            case "Point":
                break;
            case "LineString":
            case "Polygon":
                if (geo.getCoordinates().length > 1) {
                    for (int i = 0; i < geo.getCoordinates().length - 1; i++) {
                        result.add(jtsgf.createLineString(new Coordinate[]{
                                geo.getCoordinates()[i], geo.getCoordinates()[i + 1]
                        }));
                    }
                }
                break;
            case "GeometryCollection":
                for (int i = 0; i < geo.getNumGeometries(); i++) {
                    result.addAll(breakGeometryToSegments(geo.getGeometryN(i)));
                }
                break;
        }
        return result;
    }

    /**
     * cut LineString with two point on it
     *
     * @param lineString lineString to cut
     * @param p1         point 1
     * @param p2         point 2
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString cutLineString2Points(final LineString lineString, final Coordinate p1, final Coordinate p2) {
        int[] p1Edge = ZGeoMath.pointOnWhichEdgeIndices(p1, lineString);
        int[] p2Edge = ZGeoMath.pointOnWhichEdgeIndices(p2, lineString);
        if (p1Edge[0] > -1 && p1Edge[1] > -1 && p2Edge[0] > -1 && p2Edge[1] > -1) {
            List<Coordinate> coords = new ArrayList<>();
            if (p1Edge[0] < p2Edge[0]) {
                // p1 is in front of p2
                coords.add(p1);
                for (int i = p1Edge[1]; i < p2Edge[1]; i++) {
                    coords.add(lineString.getCoordinateN(i));
                }
                coords.add(p2);
            } else if (p1Edge[0] > p2Edge[0]) {
                // p2 is in front of p1
                coords.add(p2);
                for (int i = p2Edge[1]; i < p1Edge[1]; i++) {
                    coords.add(lineString.getCoordinateN(i));
                }
                coords.add(p1);
            } else {
                // on same edge
                Coordinate p0 = lineString.getCoordinateN(p1Edge[0]);
                if (p0.distance(p1) <= p0.distance(p2)) {
                    // p1 is in front of p2
                    coords.add(p1);
                    coords.add(p2);
                } else {
                    // p2 is in front of p1
                    coords.add(p2);
                    coords.add(p1);
                }
            }
            return createLineStringFromList(coords);
        } else {
            return lineString;
        }
    }

    /**
     * cut out a WB_PolyLine from a WB_Polygon by giving indices
     *
     * @param polygon polygon to be extracted
     * @param index   segment indices to extract
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine createPolylineFromPolygon(final WB_Polygon polygon, final int[] index) {
        WB_Point[] points = new WB_Point[index.length + 1];
        for (int i = 0; i < index.length; i++) {
            points[i] = polygon.getPoint(index[i]);
        }
        points[index.length] = polygon.getPoint(
                (index[index.length - 1] + 1) % polygon.getNumberOfShellPoints()
        );
        return new WB_PolyLine(points);
    }

    /**
     * extend both ends of a LineString
     *
     * @param ls   input LineString to extend
     * @param dist extend distance
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString createExtendedLineString(final LineString ls, final double dist) {
        Coordinate[] coords = ls.getCoordinates();

        if (coords.length > 2) {
            Coordinate[] newCoords = new Coordinate[coords.length];

            Coordinate p0 = coords[0];
            Coordinate p1 = coords[1];
            Coordinate p2 = coords[coords.length - 2];
            Coordinate p3 = coords[coords.length - 1];

            Vector2D v1 = new Vector2D(p1, p0).normalize();
            Vector2D v2 = new Vector2D(p2, p3).normalize();

            Coordinate newC0 = Vector2D.create(p0).add(v1.multiply(dist)).toCoordinate();
            Coordinate newC3 = Vector2D.create(p3).add(v2.multiply(dist)).toCoordinate();

            newCoords[0] = newC0;
            System.arraycopy(coords, 1, newCoords, 1, coords.length - 1 - 1);
            newCoords[coords.length - 1] = newC3;

            return jtsgf.createLineString(newCoords);
        } else if (coords.length == 2) {
            Coordinate p0 = coords[0];
            Coordinate p1 = coords[1];

            Vector2D v1 = new Vector2D(p1, p0).normalize();
            Vector2D v2 = new Vector2D(p0, p1).normalize();

            Coordinate newC0 = Vector2D.create(p0).add(v1.multiply(dist)).toCoordinate();
            Coordinate newC1 = Vector2D.create(p1).add(v2.multiply(dist)).toCoordinate();

            return jtsgf.createLineString(new Coordinate[]{newC0, newC1});
        } else {
            return ls;
        }
    }

    /**
     * create an arc by giving center, start, end
     *
     * @param center center of the arc
     * @param start  start of the arc
     * @param end    start of the arc
     * @param segNum number of segments to divide
     * @param ccw    counter-clockwise or clockwise
     */
    public static Coordinate[] createArc(final Coordinate center, final Coordinate start, final Coordinate end, final int segNum, final boolean ccw) {
        double radius = start.distance(center);
        Vector2D cen = Vector2D.create(center);


        Vector2D v2 = Vector2D.create(end).subtract(Vector2D.create(center)).normalize();
        Vector2D v1 = Vector2D.create(start).subtract(Vector2D.create(center)).normalize();

        Coordinate[] arcPoints = new Coordinate[segNum + 1];
        double cross = v1.getX() * v2.getY() - v1.getY() * v2.getX();
        double dot = v1.dot(v2);
        if (ccw) {
            // generate the arc counter-clockwise
            if (cross > 0) {
                // inferior angle
                double angle = Math.acos(dot);
                double step = angle / segNum;
                for (int i = 0; i < segNum + 1; i++) {
                    arcPoints[i] = v1.rotate(step * i).multiply(radius).add(cen).toCoordinate();
                }
            } else if (cross < 0) {
                // reflex angle
                double angle = Math.PI * 2 - Math.acos(dot);
                double step = angle / segNum;
                for (int i = 0; i < segNum + 1; i++) {
                    arcPoints[i] = v1.rotate(step * i).multiply(radius).add(cen).toCoordinate();
                }
            } else {
                if (dot >= 0) {
                    // collinear
                    arcPoints = new Coordinate[]{start};
                } else {
                    // 180 degrees
                    double step = Math.PI / segNum;
                    for (int i = 0; i < segNum + 1; i++) {
                        arcPoints[i] = v1.rotate(step * i).multiply(radius).add(cen).toCoordinate();
                    }
                }
            }
        } else {
            // generate the arc clockwise
            if (cross > 0) {
                // inferior angle
                double angle = Math.PI * 2 - Math.acos(dot);
                double step = angle / segNum;
                for (int i = 0; i < segNum + 1; i++) {
                    arcPoints[i] = v1.rotate(-step * i).multiply(radius).add(cen).toCoordinate();
                }
            } else if (cross < 0) {
                // reflex angle
                double angle = Math.acos(dot);
                double step = angle / segNum;
                for (int i = 0; i < segNum + 1; i++) {
                    arcPoints[i] = v1.rotate(-step * i).multiply(radius).add(cen).toCoordinate();
                }
            } else {
                if (dot >= 0) {
                    // collinear
                    arcPoints = new Coordinate[]{start};
                } else {
                    // 180 degrees
                    double step = Math.PI / segNum;
                    for (int i = 0; i < segNum + 1; i++) {
                        arcPoints[i] = v1.rotate(-step * i).multiply(radius).add(cen).toCoordinate();
                    }
                }
            }
        }
        return arcPoints;
    }

    /**
     * create an arc by giving center and radius, return a series of points for geometry creation
     * can be used to create circle or regular polygon
     *
     * @param center center of the circle
     * @param r      radius of the circle
     * @param segNum number of segments to divide
     */
    public static Coordinate[] createCircle(final Coordinate center, final double r, final int segNum) {
        Coordinate[] cirPoints = new Coordinate[segNum + 1];
        double step = (Math.PI * 2) / segNum;
        for (int i = 0; i < segNum; i++) {
            cirPoints[i] = new Coordinate(center.getX() + r * Math.cos(step * segNum), center.getX() + r * Math.sin(step * segNum));
        }
        cirPoints[cirPoints.length - 1] = cirPoints[0];
        return cirPoints;
    }

    /**
     * create an AABB for jts Geometry
     * return 2 ordinates of bottom-left coordinate and 2 ordinates of top-right coordinate
     *
     * @param g input Geometry
     * @return double[]
     */
    public static double[] createJtsAABB2D(final Geometry g) {
        // minX, minY, maxX, maxY
        double[] result = new double[]{
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                -Double.MAX_VALUE,
                -Double.MAX_VALUE
        };
        Coordinate[] coordinates = g.getCoordinates();
        if (coordinates == null) {
            throw new NullPointerException("Array not initialized.");
        } else if (coordinates.length == 0) {
            throw new IllegalArgumentException("Array has zero size.");
        } else {
            Coordinate c;
            for (Coordinate coordinate : coordinates) {
                c = coordinate;
                if (c == null) {
                    throw new NullPointerException("Array point not initialized.");
                }

                // min x
                if (result[0] > c.getOrdinate(0)) {
                    result[0] = c.getOrdinate(0);
                }
                // min y
                if (result[1] > c.getOrdinate(1)) {
                    result[1] = c.getOrdinate(1);
                }

                // max x
                if (result[2] < c.getOrdinate(0)) {
                    result[2] = c.getOrdinate(0);
                }
                // max y
                if (result[3] < c.getOrdinate(1)) {
                    result[3] = c.getOrdinate(1);
                }
            }
        }
        return result;
    }

    /**
     * create an offset LineString (direction depends on the +- of distance)
     *
     * @param ls   original LineString
     * @param dist distance to offset
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString createOffsetLineString(final LineString ls, final double dist) {
        if (dist == 0) {
            return (LineString) ls.copy();
        }

        Coordinate[] coords = ls.getCoordinates();
        int coordNum = coords.length;
        Vector2D[] bisectorVecs = new Vector2D[coordNum];

        // the first and last vector
        bisectorVecs[0] = new Vector2D(
                coords[1].getX() - coords[0].getX(),
                coords[1].getY() - coords[0].getY()
        ).rotate(Math.PI * 0.5).normalize();
        bisectorVecs[coordNum - 1] = new Vector2D(
                coords[coordNum - 2].getX() - coords[coordNum - 1].getX(),
                coords[coordNum - 2].getY() - coords[coordNum - 1].getY()
        ).rotate(Math.PI * -0.5).normalize();

        // middle vectors
        if (ls.getNumPoints() > 2) {
            for (int i = 1; i < coordNum - 1; i++) {
                Vector2D prev = new Vector2D(
                        coords[i - 1].getX() - coords[i].getX(),
                        coords[i - 1].getY() - coords[i].getY()
                ).normalize();
                Vector2D next = new Vector2D(
                        coords[i + 1].getX() - coords[i].getX(),
                        coords[i + 1].getY() - coords[i].getY()
                ).normalize();
                double halfAngle = Math.abs(next.angleTo(prev) * 0.5);
                Vector2D norBisec = ZGeoMath.getAngleBisectorOrdered(next, prev);
                bisectorVecs[i] = norBisec.multiply(1 / Math.sin(halfAngle));
            }
        }

        // offset direction depends on positive/negative value of distance
        boolean dir = true;
        if (dist < 0) {
            for (int i = 0; i < bisectorVecs.length; i++) {
                bisectorVecs[i] = bisectorVecs[i].multiply(-1);
            }
            dir = false;
        }

        // calculate the intersection point of each neighbor pair of vectors
        Vector2D[][] rays = new Vector2D[coordNum][];
        for (int i = 0; i < coordNum; i++) {
            rays[i] = new Vector2D[]{
                    new Vector2D(coords[i]),
                    bisectorVecs[i]
            };
        }
        Coordinate[] intersections = new Coordinate[coordNum - 1];
        for (int i = 0; i < coordNum - 1; i++) {
            Coordinate inter = ZGeoMath.rayIntersection2D(rays[i], rays[i + 1]);
            intersections[i] = inter;
        }

        // record the distance of each intersection point
        double[] distOfInter = new double[coordNum - 1];
        boolean nonInterFlag = true;
        for (int i = 0; i < coordNum - 1; i++) {
            Coordinate inter = intersections[i];
            if (inter != null) {
                double distToSeg = Distance.pointToSegment(inter, coords[i], coords[i + 1]);
                distOfInter[i] = distToSeg;
                nonInterFlag = false;
            } else {
                distOfInter[i] = Double.MAX_VALUE;
            }
        }

        double minDist = Double.MAX_VALUE;
        List<Integer> minIDs = new ArrayList<>();
        if (!nonInterFlag) {
            // intersection points exist, find the minimum distance (1 or more)
            int minIndex = ZMath.getMinIndex(distOfInter);
            minDist = distOfInter[minIndex];
            for (int i = 0; i < coordNum - 1; i++) {
                if (distOfInter[i] == minDist) {
                    minIDs.add(i);
                }
            }
        }

        double absDist = Math.abs(dist);
        if (!nonInterFlag && absDist > minDist) {
            // intersection point disappears
            // create new LineString, record the redundant distance
            List<Coordinate> newCoords = new ArrayList<>();
            for (int i = 0; i < coordNum; i++) {
                Vector2D scaledVec = bisectorVecs[i].multiply(minDist);
                Coordinate offsetTemp = new Coordinate(
                        coords[i].getX() + scaledVec.getX(),
                        coords[i].getY() + scaledVec.getY()
                );
                newCoords.add(offsetTemp);
                if (minIDs.contains(i)) {
                    i++; // jump the next
                }
            }
            LineString newLS = ZFactory.createLineStringFromList(newCoords);
            double newDist = absDist - minDist;
            if (!dir) {
                newDist *= -1;
            }
            return createOffsetLineString(newLS, newDist);
        } else {
            // no intersection point disappears
            Coordinate[] resultCoords = new Coordinate[ls.getNumPoints()];
            for (int i = 0; i < ls.getNumPoints(); i++) {
                Vector2D scaledVec = bisectorVecs[i].multiply(Math.abs(dist));
                resultCoords[i] = new Coordinate(
                        coords[i].getX() + scaledVec.getX(),
                        coords[i].getY() + scaledVec.getY()
                );
            }
            return jtsgf.createLineString(resultCoords);
        }
    }

    /*-------- create graphs --------*/

    /**
     * create a ZGraph from a list of segments
     *
     * @param lines input segments list
     * @return geometry.ZGraph
     */
    public static ZGraph createZGraphFromSegments(final LineString[] lines) {
        // create LineMergeGraph
        LineMergeGraph lmg = new LineMergeGraph();
        for (LineString l : lines) {
            lmg.addEdge(l);
        }
        List<ZNode> nodes = new ArrayList<>();
        List<ZEdge> edges = new ArrayList<>();
        List<Node> tempNodes = new ArrayList<>();

        // convert to ZGraph
        for (Object o : lmg.getEdges()) {
            if (o instanceof Edge) {
                Edge e = (Edge) o;

                Node from = e.getDirEdge(0).getFromNode();
                ZNode start = new ZNode(from.getCoordinate());
                Node to = e.getDirEdge(0).getToNode();
                ZNode end = new ZNode(to.getCoordinate());

                start.setRelationReady();
                end.setRelationReady();

                if (!tempNodes.contains(from)) {
                    tempNodes.add(from);
                    nodes.add(start);
                } else {
                    start = nodes.get(tempNodes.indexOf(from));
                }

                if (!tempNodes.contains(to)) {
                    tempNodes.add(to);
                    nodes.add(end);
                } else {
                    end = nodes.get(tempNodes.indexOf(to));
                }

                ZEdge edge = new ZEdge(start, end);
                start.addNeighbor(end);
                start.addLinkedEdge(edge);
                end.addNeighbor(start);
                end.addLinkedEdge(edge);

                edges.add(edge);
            }
        }
        return new ZGraph(nodes, edges);
    }

    /**
     * create a ZGraph from a list of segments
     *
     * @param lines input segments list
     * @return geometry.ZGraph
     */
    public static ZGraph createZGraphFromSegments(final List<? extends ZLine> lines) {
        // create LineMergeGraph
        LineMergeGraph lmg = new LineMergeGraph();
        for (ZLine l : lines) {
            lmg.addEdge(l.toJtsLineString());
        }
        List<ZNode> nodes = new ArrayList<>();
        List<ZEdge> edges = new ArrayList<>();
        List<Node> tempNodes = new ArrayList<>();

        // convert to ZGraph
        for (Object o : lmg.getEdges()) {
            if (o instanceof Edge) {
                Edge e = (Edge) o;

                Node from = e.getDirEdge(0).getFromNode();
                ZNode start = new ZNode(from.getCoordinate());
                Node to = e.getDirEdge(0).getToNode();
                ZNode end = new ZNode(to.getCoordinate());

                start.setRelationReady();
                end.setRelationReady();

                if (!tempNodes.contains(from)) {
                    tempNodes.add(from);
                    nodes.add(start);
                } else {
                    start = nodes.get(tempNodes.indexOf(from));
                }

                if (!tempNodes.contains(to)) {
                    tempNodes.add(to);
                    nodes.add(end);
                } else {
                    end = nodes.get(tempNodes.indexOf(to));
                }

                ZEdge edge = new ZEdge(start, end);
                start.addNeighbor(end);
                start.addLinkedEdge(edge);
                end.addNeighbor(start);
                end.addLinkedEdge(edge);

                edges.add(edge);
            }
        }
        return new ZGraph(nodes, edges);
    }

    /**
     * create a mini-spanning tree from a list of points (Prim algorithm)
     *
     * @param generator points to generate mini spanning tree
     * @return geometry.ZGraph
     */
    public static ZGraph createMiniSpanningTree(final List<? extends ZPoint> generator) {
        List<ZNode> nodes = new ArrayList<>();
        for (ZPoint pt : generator) {
            nodes.add(new ZNode(pt.xd(), pt.yd()));
        }

        // create adjacency matrix based on distance
        double[][] adjMatrix = new double[generator.size()][generator.size()];
        for (int i = 0; i < generator.size(); i++) {
            for (int j = i; j < generator.size(); j++) {
                if (i == j) {
                    adjMatrix[i][j] = -1;
                } else {
                    adjMatrix[i][j] = adjMatrix[j][i] = generator.get(i).distance(generator.get(j));
                }
            }
        }

        // generate tree
        // list to count each node
        List<Integer> list = new ArrayList<>();
        list.add(0);

        int begin = 0;
        int end = 0;
        float weight;

        // final tree indices (n-1)
        int[] parent = new int[adjMatrix.length];
        for (int i = 0; i < adjMatrix.length; i++) {
            parent[i] = -1;
        }
        while (list.size() < adjMatrix.length) {
            weight = Float.MAX_VALUE;
            for (Integer row : list) {
                for (int i = 0; i < adjMatrix.length; i++) {
                    if (!list.contains(i)) {
                        if (i >= row + 1) {
                            if (adjMatrix[row][i] >= 0 && adjMatrix[row][i] < weight) {
                                begin = row;
                                end = i;
                                weight = (float) adjMatrix[row][i];
                            }
                        } else if (i <= row - 1) {
                            if (adjMatrix[i][row] >= 0 && adjMatrix[i][row] < weight) {
                                begin = row;
                                end = i;
                                weight = (float) adjMatrix[i][row];
                            }
                        }
                    }
                }
            }
            list.add(end);
            parent[end] = begin;
        }

        int[][] matrix = new int[parent.length - 1][];
        for (int i = 1; i < parent.length; i++) {
            matrix[i - 1] = new int[]{i, parent[i]};
        }
        return new ZGraph(nodes, matrix);
    }

    /*-------- copy geometries --------*/

    /**
     * copy a simple WB_Polygon
     *
     * @param polygon original WB_Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon copySimple_WB_Polygon(WB_Polygon polygon) {
        List<WB_Point> cs = new ArrayList<>();
        int numberOfPoints = polygon.getNumberOfPoints();
        for (int i = 0; i < numberOfPoints; i++) {
            WB_Point p = polygon.getPoint(i);
            cs.add(new WB_Point(p.xd(), p.yd(), p.zd()));
        }
        return wbgf.createSimplePolygon(cs);
    }
}
