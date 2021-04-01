package geometry;

import math.ZMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.linemerge.LineMerger;
import transform.ZTransform;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他创建命令
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/8
 * @time 22:04
 * <p>
 * 将一系列首尾相接线段合成一条WB_PolyLine,若有多条，则取最长
 * 将一系列首尾相接线段合成一组WB_PolyLine list
 * 将一系列首尾相接线段合成一条LineString,若有多条，则取最长
 * 将WB_PolyLine在端点处断开，创建一组新折线
 * 将LineString在端点处断开，创建一组新折线
 * 给定线段序号，从WB_Polygon中创建一截WB_PloyLine
 * 将一条LineString向两端头微微延长一定距离（规避误差）
 *
 * 从一组线段创建ZGraph
 */
public class ZFactory {
    public static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    public static final GeometryFactory jtsgf = new GeometryFactory();
    private static final double epsilon = 0.00000001;

    /*-------- create geometries --------*/

    /**
     * 将一系列首尾相接线段合成一条LineString
     * 若有多条，则取最长一条
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
        if (lineMerger.getMergedLineStrings().size() > 1) {
            double[] lineStringLengths = new double[lineMerger.getMergedLineStrings().toArray().length];
            for (int i = 0; i < lineMerger.getMergedLineStrings().toArray().length; i++) {
                LineString l = (LineString) lineMerger.getMergedLineStrings().toArray()[i];
                lineStringLengths[i] = l.getLength();
            }
            return (LineString) lineMerger.getMergedLineStrings().toArray()[ZMath.getMaxIndex(lineStringLengths)];
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            return (LineString) lineMerger.getMergedLineStrings().toArray()[0];
        } else {
            return null;
        }
    }

    /**
     * 将一系列首尾相接线段合成一条WB_PolyLine
     * 若有多条，则取最长一条
     *
     * @param lines list of lines
     * @return wblut.geom.WB_PolyLine
     */
    public static WB_PolyLine createWB_PolyLine(final List<? extends ZLine> lines) {
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
            return ZTransform.jtsLineStringToWB_PolyLine(merged);
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[0];
            return ZTransform.jtsLineStringToWB_PolyLine(merged);
        } else {
            return null;
        }
    }

    /**
     * 将一系列首尾相接线段合成若干段WB_PolyLine
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
        if (lineMerger.getMergedLineStrings().size() > 0) {
            for (Object ls : lineMerger.getMergedLineStrings()) {
                if (ls instanceof LineString) {
                    result.add(ZTransform.jtsLineStringToWB_PolyLine((LineString) ls));
                }
            }
        }
        return result;
    }

    /**
     * 将WB_PolyLine在端点处断开，创建一组新折线
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
     * 将LineString在端点处断开，创建一组新折线
     *
     * @param lineString lineString to be break
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
     * 给定线段序号，从WB_Polygon中截取一段WB_PloyLine
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
     * 将一条LineString向两端头微微延长一定距离（规避误差）
     *
     * @param ls   input LineString to extend
     * @param dist extend distance
     * @return org.locationtech.jts.geom.LineString
     */
    public static LineString createExtendedLineString(final LineString ls, double dist) {
        Coordinate[] coords = ls.getCoordinates();

        if (coords.length > 2) {
            Coordinate[] newCoords = new Coordinate[coords.length];

            ZPoint p0 = new ZPoint(coords[0]);
            ZPoint p1 = new ZPoint(coords[1]);
            ZPoint p2 = new ZPoint(coords[coords.length - 2]);
            ZPoint p3 = new ZPoint(coords[coords.length - 1]);

            ZPoint v1 = p0.sub(p1).unit();
            ZPoint v2 = p3.sub(p2).unit();

            Coordinate newC0 = p0.add(v1.scaleTo(dist)).toJtsCoordinate();
            Coordinate newC3 = p3.add(v2.scaleTo(dist)).toJtsCoordinate();

            newCoords[0] = newC0;
            System.arraycopy(coords, 1, newCoords, 1, coords.length - 1 - 1);
            newCoords[coords.length - 1] = newC3;

            return jtsgf.createLineString(newCoords);
        } else if (coords.length == 2) {
            ZPoint p0 = new ZPoint(coords[0]);
            ZPoint p1 = new ZPoint(coords[1]);

            ZPoint v1 = p0.sub(p1).unit();
            ZPoint v2 = p1.sub(p0).unit();

            Coordinate newC0 = p0.add(v1.scaleTo(dist)).toJtsCoordinate();
            Coordinate newC1 = p1.add(v2.scaleTo(dist)).toJtsCoordinate();

            return jtsgf.createLineString(new Coordinate[]{newC0, newC1});
        } else {
            return ls;
        }
    }

    /*-------- create graphs --------*/

    /**
     * 从一组线段创建ZGraph
     *
     * @param lines input segments list
     * @return geometry.ZGraph
     */
    public static ZGraph createZGraphFromSegments(final List<? extends ZLine> lines) {
        List<ZNode> nodes = new ArrayList<>();
        List<ZEdge> edges = new ArrayList<>();

        List<ZPoint> checkList = new ArrayList<>();
        for (ZLine l : lines) {
            ZNode start = new ZNode(l.getPt0().xd(), l.getPt0().yd(), l.getPt0().zd());
            ZNode end = new ZNode(l.getPt1().xd(), l.getPt1().yd(), l.getPt1().zd());
            start.setRelationReady();
            end.setRelationReady();

            // 去重
            for (ZNode n : nodes) {
                if (n.equals(start)) {
                    start = n;
                    break;
                }
                if (n.equals(end)) {
                    end = n;
                    break;
                }
            }

            ZEdge edge = new ZEdge(start, end);

            // 添加连接关系
            start.addNeighbor(end);
            start.addLinkedEdge(edge);
            end.addNeighbor(start);
            end.addLinkedEdge(edge);

            // 添加至列表 避免重复
            if (!nodes.contains(start)) {
                nodes.add(start);
            }
            if (!nodes.contains(end)) {
                nodes.add(end);
            }
            edges.add(edge);
        }
        return new ZGraph(nodes, edges);
    }
}
