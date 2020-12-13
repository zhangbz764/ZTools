package geometry;

import math.ZMath;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.linemerge.LineMerger;
import transform.ZTransform;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含了jts的GeometryFactory和HE_Mesh的WB_GeometryFactory，以及其他create命令
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/11/8
 * @time 22:04
 */
public class ZGeoFactory {
    public static final WB_GeometryFactory wbgf = new WB_GeometryFactory();
    public static final GeometryFactory jtsgf = new GeometryFactory();
    private static final double epsilon = 0.00000001;

    /**
     * 将一系列首尾相接线段合成一条LineString
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
            return ZTransform.JtsLineStringToWB_PolyLine(merged);
        } else if (lineMerger.getMergedLineStrings().size() == 1) {
            LineString merged = (LineString) lineMerger.getMergedLineStrings().toArray()[0];
            return ZTransform.JtsLineStringToWB_PolyLine(merged);
        } else {
            return null;
        }
    }

    /**
     * 将WB_PolyLine在端点处断开，返回若干条新折线
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
     * 将LineString在端点处断开，返回若干条新折线
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
}
