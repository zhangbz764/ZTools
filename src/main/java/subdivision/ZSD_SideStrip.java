package subdivision;

import geometry.ZGeoFactory;
import geometry.ZLine;
import geometry.ZPoint;
import math.ZGeoMath;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import processing.core.PApplet;
import transform.ZTransform;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/12/10
 * @time 21:37
 */
public class ZSD_SideStrip extends ZSubdivision {
    private List<WB_PolyLine> finalPolyLines;
    private List<ZLine> divideLines;

    private int[] offestIndices;
    private double span = 11;
    private double offsetDist = -12;

    /* ------------- constructor ------------- */

    public ZSD_SideStrip(WB_Polygon originPolygon) {
        super(originPolygon);
        this.offestIndices = new int[]{1, 2,3};
    }

    @Override
    public void performDivide() {
        this.divideLines = new ArrayList<>();
        offsetEdges();
        createDivideLines();

        // TODO: 2020/12/11 目前只支持polyline
        // use Polygonizer to divide
        Polygonizer pr = new Polygonizer();
        Geometry allGeometry = ZTransform.PolygonToLineString(ZTransform.WB_PolygonToJtsPolygon(super.getOriginPolygon())).get(0);
        for (WB_PolyLine pl : finalPolyLines) {
            allGeometry = allGeometry.union(ZTransform.WB_PolyLineToJtsLineString(pl));
        }
        for (ZLine l : divideLines) {
            allGeometry = allGeometry.union(l.toJtsLineString());
        }
        pr.add(allGeometry);
        Collection<Polygon> dividedPolys = pr.getPolygons();
//        for(Polygon p: dividedPolys){
//            if(p.contains(ZTransform.WB_PointToPoint(super.getOriginPolygon().getPoint(offestIndices[])))){
//
//            }
//        }
//
//        pr = new Polygonizer();

        // use a non-offset point to remove rest part
        List<Polygon> allSubPolygonJts = (List<Polygon>) dividedPolys;
//        for (int i = 0; i < super.getOriginPolygon().getNumberOfShellPoints(); i++) {
//            boolean flag = false;
//            for (int j : offestIndices) {
//                if (i == j) {
//                    flag = true;
//                    break;
//                }
//            }
//            System.out.println(i + " " + flag);
//            if (!flag) {
//                for (Polygon p : dividedPolys) {
//                    if (p.contains(ZTransform.WB_PointToPoint(super.getOriginPolygon().getCenter()))) {
//                        System.out.println("remove!");
//                        allSubPolygonJts.remove(p);
//                        break;
//                    }
//                }
//                break;
//            }
//        }

        List<WB_Polygon> allSubPolygons = new ArrayList<>();
        for (Polygon p : allSubPolygonJts) {
            allSubPolygons.add(ZTransform.jtsPolygonToWB_Polygon(p));
        }
        super.setAllSubPolygons(allSubPolygons);
        super.setRandomColor();
    }

    /* ------------- compute function ------------- */

    /**
     * split each offset polyLines, find perpendicular divide lines
     *
     * @param
     * @return void
     */
    private void createDivideLines() {
        for (WB_PolyLine pl : finalPolyLines) {
            Map<ZPoint, Integer> pointDirMap = ZGeoMath.splitWB_PolyLineEdgeByThresholdWithPos(pl, span + 3, span - 3);
            List<ZLine> divideLinesOnPolyLine = new ArrayList<>();
            for (Map.Entry<ZPoint, Integer> pair : pointDirMap.entrySet()) {
                ZPoint origin = pair.getKey();
                int segIndex = pair.getValue();
                ZPoint segVec = new ZPoint(pl.getSegment(segIndex).getDirection());
                ZPoint start = origin.add(segVec.rotate2D(Math.PI * -0.5).scaleTo(-1.1));
                ZPoint end = origin.add(segVec.rotate2D(Math.PI * 0.5).scaleTo(offsetDist * 1.1));
                divideLinesOnPolyLine.add(new ZLine(start, end));
            }
            divideLinesOnPolyLine.remove(0);
            divideLinesOnPolyLine.remove(divideLinesOnPolyLine.size() - 1);
            divideLines.addAll(divideLinesOnPolyLine);
        }
        System.out.println("final divide segments: " + divideLines.size());
    }

    /**
     * offset edges of offset indices
     *
     * @param
     * @return void
     */
    private void offsetEdges() {
        WB_PolyLine initPolyLine = ZGeoMath.offsetWB_PolygonSegments(super.getOriginPolygon(), offestIndices, offsetDist);

        // find concave points and break polyline
        if (!(initPolyLine instanceof WB_Polygon)) {
            // polygon
            List<Integer> concavePointsIndices = new ArrayList<>();
            for (int i = 1; i < initPolyLine.getNumberOfPoints() - 1; i++) {
                ZPoint prev = new ZPoint(initPolyLine.getPoint(i - 1).sub(initPolyLine.getPoint(i)));
                ZPoint next = new ZPoint(initPolyLine.getPoint(i + 1).sub(initPolyLine.getPoint(i)));
                if (next.cross2D(prev) < 0) {
                    concavePointsIndices.add(i);
                    // add divide line at concave points
                    divideLines.add(new ZLine(
                            new ZPoint(initPolyLine.getPoint(i)),
                            new ZPoint(super.getOriginPolygon().getPoint((i + offestIndices[0]) % super.getOriginPolygon().getNumberOfShellPoints())))
                    );
                }
            }
            int[] indices = new int[concavePointsIndices.size()];
            for (int i = 0; i < concavePointsIndices.size(); i++) {
                indices[i] = concavePointsIndices.get(i);
            }
            finalPolyLines = ZGeoFactory.breakWB_PolyLine(initPolyLine, indices);

            // extend to boundary and create new polyline
            // if boundary point is concave, connect offset point to boundary point instead of extending
            finalPolyLines = extendPolylineToBoundary(finalPolyLines);

        } else {
            // polyline
            System.out.println("offset result is a polygon");
            List<Integer> concavePointsIndices = new ArrayList<>();
            for (int i = 0; i < initPolyLine.getNumberOfPoints() - 1; i++) {
                ZPoint prev = new ZPoint(initPolyLine.getPoint(
                        (i + initPolyLine.getNumberOfPoints() - 2) % (initPolyLine.getNumberOfPoints() - 1)
                ).sub(initPolyLine.getPoint(i)));
                ZPoint next = new ZPoint(initPolyLine.getPoint(i + 1).sub(initPolyLine.getPoint(i)));
                if (next.cross2D(prev) < 0) {
                    concavePointsIndices.add(i);
                    // add divide line at concave points
                    divideLines.add(new ZLine(
                            new ZPoint(initPolyLine.getPoint(i)),
                            new ZPoint(super.getOriginPolygon().getPoint((i + offestIndices[0]) % super.getOriginPolygon().getNumberOfShellPoints())))
                    );
                }
            }
            int[] indices = new int[concavePointsIndices.size()];
            for (int i = 0; i < concavePointsIndices.size(); i++) {
                indices[i] = concavePointsIndices.get(i);
            }
            finalPolyLines = ZGeoFactory.breakWB_PolyLine(initPolyLine, indices);

        }
    }

    /**
     * extend offset polyLines to the boundary polygon
     *
     * @param finalPolyLine list of initial offset polyLines
     * @return java.util.List<wblut.geom.WB_PolyLine>
     */
    private List<WB_PolyLine> extendPolylineToBoundary(final List<WB_PolyLine> finalPolyLine) {
        List<WB_PolyLine> result = new ArrayList<>();
        if (finalPolyLine.size() > 1) {
            // more than one polyline, then extend the first one and the last one
            WB_PolyLine polyLine1 = finalPolyLine.get(0);
            WB_PolyLine polyLine2 = finalPolyLine.get(finalPolyLine.size() - 1);
            ZPoint[] seg1 = new ZPoint[]{
                    new ZPoint(polyLine1.getPoint(1)),
                    new ZPoint(polyLine1.getPoint(0).sub(polyLine1.getPoint(1)))
            };
            ZPoint[] seg2 = new ZPoint[]{
                    new ZPoint(polyLine2.getPoint(polyLine2.getNumberOfPoints() - 2)),
                    new ZPoint(polyLine2.getPoint(polyLine2.getNumberOfPoints() - 1).sub(polyLine2.getPoint(polyLine2.getNumberOfPoints() - 2)))
            };
            // rebuild new polyline
            List<Integer> concaveIndices = ZGeoMath.getConcavePointIndices(super.getOriginPolygon());
            List<WB_Point> newPolyPoints1 = new ArrayList<>();
            for (int i = 0; i < polyLine1.getNumberOfPoints(); i++) {
                newPolyPoints1.add(polyLine1.getPoint(i));
            }
            List<WB_Point> newPolyPoints2 = new ArrayList<>();
            for (int i = 0; i < polyLine2.getNumberOfPoints(); i++) {
                newPolyPoints2.add(polyLine2.getPoint(i));
            }
            if (concaveIndices.contains(offestIndices[0])) {
                divideLines.add(new ZLine(
                        new ZPoint(polyLine1.getPoint(0)),
                        new ZPoint(super.getOriginPolygon().getPoint(offestIndices[0]))
                ));
            } else {
                ZLine extendSeg = ZGeoMath.extendSegmentToPolygon(seg1, super.getOriginPolygon());
                assert extendSeg != null;
                newPolyPoints1.remove(0);
                newPolyPoints1.add(0, extendSeg.getPt0().toWB_Point());
                polyLine1 = new WB_PolyLine(newPolyPoints1);
            }
            result.add(polyLine1);

            // add middle polyLines
            if (finalPolyLine.size() > 2) {
                for (int i = 1; i < finalPolyLine.size() - 1; i++) {
                    result.add(finalPolyLine.get(i));
                }
            }

            if (concaveIndices.contains(offestIndices[offestIndices.length - 1] + 1)) {
                divideLines.add(new ZLine(
                        new ZPoint(polyLine2.getPoint(polyLine2.getNumberOfPoints() - 1)),
                        new ZPoint(super.getOriginPolygon().getPoint(offestIndices[offestIndices.length - 1] + 1))
                ));
            } else {
                ZLine extendSeg = ZGeoMath.extendSegmentToPolygon(seg2, super.getOriginPolygon());
                assert extendSeg != null;
                newPolyPoints2.remove(newPolyPoints2.size() - 1);
                newPolyPoints2.add(extendSeg.getPt1().toWB_Point());
                polyLine2 = new WB_PolyLine(newPolyPoints2);
            }

            result.add(polyLine2);
        } else {
            // only one polyline, then extend itself
            WB_PolyLine polyLine = finalPolyLine.get(0);
            ZPoint[] seg1 = new ZPoint[]{
                    new ZPoint(polyLine.getPoint(1)),
                    new ZPoint(polyLine.getPoint(0).sub(polyLine.getPoint(1)))
            };
            ZPoint[] seg2 = new ZPoint[]{
                    new ZPoint(polyLine.getPoint(polyLine.getNumberOfPoints() - 2)),
                    new ZPoint(polyLine.getPoint(polyLine.getNumberOfPoints() - 1).sub(polyLine.getPoint(polyLine.getNumberOfPoints() - 2)))
            };

            // rebuild new polyline
            List<Integer> concaveIndices = ZGeoMath.getConcavePointIndices(super.getOriginPolygon());
            List<WB_Point> newPolyPoints = new ArrayList<>();
            for (int i = 0; i < polyLine.getNumberOfPoints(); i++) {
                newPolyPoints.add(polyLine.getPoint(i));
            }
            if (concaveIndices.contains(offestIndices[0])) {
                newPolyPoints.add(0, super.getOriginPolygon().getPoint(offestIndices[0]));
                divideLines.add(new ZLine(
                        new ZPoint(polyLine.getPoint(0)),
                        new ZPoint(super.getOriginPolygon().getPoint(offestIndices[0]))
                ));
            } else {
                ZLine extendSeg = ZGeoMath.extendSegmentToPolygon(seg1, super.getOriginPolygon());
                assert extendSeg != null;
                newPolyPoints.remove(0);
                newPolyPoints.add(0, extendSeg.getPt1().toWB_Point());
            }
            if (concaveIndices.contains(offestIndices[offestIndices.length - 1] + 1)) {
                newPolyPoints.add(super.getOriginPolygon().getPoint(offestIndices[offestIndices.length - 1] + 1));
                divideLines.add(new ZLine(
                        new ZPoint(polyLine.getPoint(polyLine.getNumberOfPoints() - 1)),
                        new ZPoint(super.getOriginPolygon().getPoint(offestIndices[offestIndices.length - 1] + 1))
                ));
            } else {
                ZLine extendSeg = ZGeoMath.extendSegmentToPolygon(seg2, super.getOriginPolygon());
                assert extendSeg != null;
                newPolyPoints.remove(newPolyPoints.size() - 1);
                newPolyPoints.add(extendSeg.getPt1().toWB_Point());
            }
            polyLine = new WB_PolyLine(newPolyPoints);
            result.add(polyLine);
        }
        return result;
    }

    /* ------------- setter & getter ------------- */

    @Override
    public void setCellConstraint(double constraint) {

    }

    /* ------------- draw ------------- */

    @Override
    public void display(PApplet app, WB_Render render) {
        app.pushStyle();
        super.displayWithColor(app, render);

        app.noFill();
        app.strokeWeight(1);
        render.drawPolygonEdges2D(super.getOriginPolygon());
//        for (WB_PolyLine pl : finalPolyLines) {
//            render.drawPolylineEdges(pl);
//        }
        app.strokeWeight(3);
        for (ZLine l : divideLines) {
            l.display(app);
        }
        app.popStyle();
    }
}
