package advancedGeometry.rectCover;

import basicGeometry.ZLine;
import basicGeometry.ZPoint;
import math.ZGeoMath;
import math.ZMath;
import math.ZPermuCombi;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import transform.ZTransform;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * cover a polygon with tightest rectangles
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/30
 * @time 13:09
 */
public class ZRectCover {
    private WB_Polygon boundary;

    // rays
    private int rayDensity = 100;
    private List<ZLine> rayExtends;

    private List<Integer> validI;

    // rectangles and nets
    private int rectNum = 3;
    private List<Polygon> bestRects;
    private List<List<ZLine>> grid;
    private int[] threshold = {7, 9};

    /* ------------- constructor ------------- */

    public ZRectCover(WB_Polygon boundary, int rectNum, int[] threshold) {
        setBoundary(boundary);
        setRectNum(rectNum);
        setThreshold(threshold);
        init();
    }

    public ZRectCover(WB_Polygon boundary, int rectNum, int[] threshold, int rayDensity) {
        setBoundary(boundary);
        setRectNum(rectNum);
        setThreshold(threshold);
        setRayDensity(rayDensity);
        init();
    }

    /* ------------- member function ------------- */

    public void init() {
        initRays();
        coverRectangles();
    }

    /**
     * generate rays inside the boundary
     *
     * @return void
     */
    private void initRays() {
        // generate rays
        ZPoint[] dirs = new ZPoint[this.rayDensity];
        double step = (Math.PI * 2) / dirs.length;
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = new ZPoint(Math.cos(step * i), Math.sin(step * i));
        }

        // TODO: 2021/4/30 no concave?
        // find concave points
        List<ZPoint> concave = ZGeoMath.getConcavePoints(boundary);

        // rays extends to boundary
        this.rayExtends = new ArrayList<>();
        for (ZPoint c : concave) {
            for (ZPoint dir : dirs) {
                ZPoint[] ray = new ZPoint[]{c, dir};
                ZLine extendRay = ZGeoMath.extendSegmentToPolygon(ray, boundary);
                if (extendRay != null) {
                    rayExtends.add(new ZLine(c, extendRay.getPt1()));
                }
            }
        }
        System.out.println("total rays num : " + rayExtends.size());
    }

    /**
     * find tightest covering rectangle by the giving number
     *
     * @return void
     */
    private void coverRectangles() {
        // generate combinations of rays
        ZPermuCombi pc = new ZPermuCombi();
        int[] indices = ZMath.createIntegerSeries(0, rayExtends.size());
        pc.combination(indices, rectNum - 1, 0, 0);
        List<List<Integer>> rayGroups = pc.getCombinationResults();
        System.out.println("total ray (group) num : " + rayGroups.size());

        // polygonizer by rays
        List<Collection<Polygon>> allSplitPolygons = new ArrayList<>();
        Polygonizer pr;
        int invalidCount = 0;
        for (List<Integer> list : rayGroups) {
            pr = new Polygonizer();
            Geometry nodedLineStrings = ZTransform.WB_PolyLineToJtsLineString(boundary);
            for (int index : list) {
                try {
                    nodedLineStrings = nodedLineStrings.union(rayExtends.get(index).extendTwoSidesSlightly(1).toJtsLineString());
                } catch (TopologyException e) {
                    invalidCount++;
                }
            }
            pr.add(nodedLineStrings);
            Collection<Polygon> allPolys = pr.getPolygons();
            allSplitPolygons.add(allPolys);
        }
        System.out.println("invalid during polygonizer: " + invalidCount);

        // filter by rect number
        this.validI = new ArrayList<>();
        for (int i = 0; i < allSplitPolygons.size(); i++) {
            if (allSplitPolygons.get(i).size() == rectNum) {
                validI.add(i);
            }
        }
        System.out.println("valid ray (group) num: " + validI.size());

        // keep the most rectangular results
        double[] ratioResult = new double[validI.size()];
        for (int i = 0; i < validI.size(); i++) {
            double ratioSum = 0;
            if (allSplitPolygons.get(validI.get(i)).size() == rectNum) {
                for (Polygon p : allSplitPolygons.get(validI.get(i))) {
                    if (p.getArea() < 5) {
                        ratioSum = 0;
                        break;
                    } else {
                        Geometry obb = MinimumDiameter.getMinimumRectangle(p);
                        ratioSum = ratioSum + (p.getArea() / obb.getArea());
                    }
                }
            }
            ratioResult[i] = ratioSum;
        }
        int max = ZMath.getMaxIndex(ratioResult);
        System.out.println("max  " + "[" + max + "]" + "  " + ratioResult[max]);

        // generate nets
        this.bestRects = new ArrayList<>();
        this.grid = new ArrayList<>();
        for (Polygon p : allSplitPolygons.get(validI.get(max))) {
            Polygon obb = (Polygon) MinimumDiameter.getMinimumRectangle(p);
            bestRects.add(obb);
            Coordinate c0 = obb.getCoordinates()[0];
            Coordinate c1 = obb.getCoordinates()[1];
            Coordinate c2 = obb.getCoordinates()[2];

            ZLine line01 = new ZLine(new ZPoint(c0), new ZPoint(c1));
            ZLine line12 = new ZLine(new ZPoint(c1), new ZPoint(c2));

            ZPoint dir10 = new ZPoint(c0.x - c1.x, c0.y - c1.y);
            ZPoint dir12 = new ZPoint(c2.x - c1.x, c2.y - c1.y);

            List<ZPoint> dividePoint01 = line01.divideByThreshold(threshold[0], threshold[1]);
            List<ZPoint> dividePoint12 = line12.divideByThreshold(threshold[0], threshold[1]);

            List<ZLine> divideLines = new ArrayList<>();
            for (ZPoint pt : dividePoint01) {
                divideLines.add(new ZLine(pt, pt.add(dir12)));
            }
            for (ZPoint pt : dividePoint12) {
                divideLines.add(new ZLine(pt, pt.add(dir10)));
            }
            grid.add(divideLines);
        }
    }

    /* ------------- setter & getter ------------- */

    public void setRectNum(int rectNum) {
        this.rectNum = rectNum;
    }

    public void setRayDensity(int rayDensity) {
        this.rayDensity = rayDensity;
    }

    public void setBoundary(WB_Polygon boundary) {
        this.boundary = boundary;
    }

    public void setThreshold(int[] threshold) {
        this.threshold = threshold;
    }

    public List<ZLine> getRayExtends() {
        return rayExtends;
    }

    public List<Polygon> getBestRects() {
        return bestRects;
    }

    public List<List<ZLine>> getGrid() {
        return grid;
    }

    public int[] getThreshold() {
        return threshold;
    }

    /* ------------- draw ------------- */

}
