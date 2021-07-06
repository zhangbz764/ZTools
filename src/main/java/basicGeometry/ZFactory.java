package basicGeometry;

import math.ZMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.linemerge.LineMergeGraph;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.planargraph.Edge;
import org.locationtech.jts.planargraph.Node;
import transform.ZTransform;
import wblut.geom.*;

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
 * <p>
 * #### create geometries
 * create a LineString from a list of segments, if the result is MultiLineString, choose the longest one
 * create a list of WB_PolyLine from a list of segments
 * create a WB_PolyLine from a list of segments, if the result is MultiLineString, choose the longest one
 * break WB_PolyLine by giving point indices to break
 * break LineString by giving point indices to break
 * cut out a WB_PolyLine from a WB_Polygon by giving indices
 * extend both ends of a LineString
 * <p>
 * #### create graphs
 * create a ZGraph from a list of segments
 * create a mini-spanning tree from a list of points (Prim algorithm)
 */
public class ZFactory {
    public static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    public static final GeometryFactory jtsgf = new GeometryFactory();
    private static final double epsilon = 0.0001;

    /*-------- create geometries --------*/

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
        if (lineMerger.getMergedLineStrings().size() > 0) {
            for (Object ls : lineMerger.getMergedLineStrings()) {
                if (ls instanceof LineString) {
                    result.add(ZTransform.LineStringToWB_PolyLine((LineString) ls));
                }
            }
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

            ZPoint p0 = new ZPoint(coords[0]);
            ZPoint p1 = new ZPoint(coords[1]);
            ZPoint p2 = new ZPoint(coords[coords.length - 2]);
            ZPoint p3 = new ZPoint(coords[coords.length - 1]);

            ZPoint v1 = p0.sub(p1).normalize();
            ZPoint v2 = p3.sub(p2).normalize();

            Coordinate newC0 = p0.add(v1.scaleTo(dist)).toJtsCoordinate();
            Coordinate newC3 = p3.add(v2.scaleTo(dist)).toJtsCoordinate();

            newCoords[0] = newC0;
            System.arraycopy(coords, 1, newCoords, 1, coords.length - 1 - 1);
            newCoords[coords.length - 1] = newC3;

            return jtsgf.createLineString(newCoords);
        } else if (coords.length == 2) {
            ZPoint p0 = new ZPoint(coords[0]);
            ZPoint p1 = new ZPoint(coords[1]);

            ZPoint v1 = p0.sub(p1).normalize();
            ZPoint v2 = p1.sub(p0).normalize();

            Coordinate newC0 = p0.add(v1.scaleTo(dist)).toJtsCoordinate();
            Coordinate newC1 = p1.add(v2.scaleTo(dist)).toJtsCoordinate();

            return jtsgf.createLineString(new Coordinate[]{newC0, newC1});
        } else {
            return ls;
        }
    }

    /*-------- create graphs --------*/

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
            nodes.add((ZNode) pt);
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
}
