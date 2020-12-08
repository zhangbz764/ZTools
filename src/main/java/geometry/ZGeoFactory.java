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
    public static LineString createLineString(List<? extends ZLine> lines) {
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
    public static WB_PolyLine createWB_PolyLine(List<? extends ZLine> lines) {
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
}
